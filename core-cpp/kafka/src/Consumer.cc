#include "kafka/Consumer.hh"

#include "kafka/BufferedIO.hh"
#include "kafka/Kafka.hh"
#include "kafka/ProcessHandler.hh"

#include "swa/CommandLine.hh"
#include "swa/Duration.hh"
#include "swa/Process.hh"
#include "swa/ProgramError.hh"
#include "swa/RealTimeSignalListener.hh"
#include "swa/parse.hh"

#include "cppkafka/buffer.h"
#include "cppkafka/configuration.h"
#include "cppkafka/producer.h"
#include "cppkafka/utils/consumer_dispatcher.h"

#include <nlohmann/json.hpp>
#include <uuid/uuid.h>

namespace Kafka {

const char *const MaxCapacityOptionDefault = "1000";
const char *const BatchSizeOptionDefault   = "1000";
const char *const PollDelayOptionDefault   = "100";

void Consumer::run() {

  // Get command line options
  const std::string brokers = SWA::CommandLine::getInstance().getOption(BrokersOption);
  const size_t batch_size = SWA::parse<uint32_t>(SWA::CommandLine::getInstance().getOption(BatchSizeOption, BatchSizeOptionDefault));
  const std::chrono::milliseconds poll_delay(SWA::parse<uint32_t>(SWA::CommandLine::getInstance().getOption(PollDelayOption, PollDelayOptionDefault)));
  const bool publish_debug = SWA::CommandLine::getInstance().optionPresent(DebugStatisticsOption);

  std::string groupId;
  if (SWA::CommandLine::getInstance().optionPresent(GroupIdOption)) {
    groupId = SWA::CommandLine::getInstance().getOption(GroupIdOption);
  } else {
    // generate a random group ID
    uuid_t uuid;
    uuid_generate(uuid);
    char formatted[37];
    uuid_unparse(uuid, formatted);
    groupId = std::string(formatted);
  }

  // Construct the configuration
  cppkafka::Configuration config = {{"metadata.broker.list", brokers},
                                    {"group.id", groupId},
                                    { "enable.auto.commit", false }};

  // Create the consumer
  consumer = std::make_shared<cppkafka::Consumer>(config);

  // create topics if they don't already exist
  createTopics(consumer, ProcessHandler::getInstance().getTopicNames());

  // short delay to avoid race conditions if other processes initiated topic creation
  SWA::delay(SWA::Duration::fromMillis(100));

  // Subscribe to topics
  consumer->subscribe(ProcessHandler::getInstance().getTopicNames());

  // create a signal listener
  SWA::RealTimeSignalListener listener(
      [this](int pid, int uid) { this->handleMessage(); },
      SWA::Process::getInstance().getActivityMonitor());

  bool running_ = true;

  // stop polling when the architecture shuts down
  SWA::Process::getInstance().registerShutdownListener([&running_]() { running_ = false; });

  // create a producer for stats
  cppkafka::MessageBuilder builder("SWA_debug_stats");
  cppkafka::Producer producer(config);
  uuid_t producer_uuid;
  uuid_generate(producer_uuid);
  char producer_formatted[37];
  uuid_unparse(producer_uuid, producer_formatted);
  std::string producer_id = std::string(producer_formatted);

  while (running_) {

    auto t0 = std::chrono::high_resolution_clock::now();

    // poll messages from broker
    MessageQueue::size_type max_batch = messageQueue.capacity() - messageQueue.size();
    std::vector<cppkafka::Message> msgs = consumer->poll_batch(std::min(max_batch, batch_size), poll_delay);

    // TODO handle errors (msg.get_error())

    // queue the messages
    messageQueue.enqueue(msgs);

    // send signals to the architecture for each new message
    for (std::vector<cppkafka::Message>::size_type i = 0; i < msgs.size(); i++) {
      listener.queueSignal();
    }

    // publish stats
    if (publish_debug) {
      auto t1 = std::chrono::high_resolution_clock::now();
      auto d = std::chrono::duration_cast<std::chrono::microseconds>(t1 - t0);
      nlohmann::json j;
      j["name"] = SWA::Process::getInstance().getName();
      j["producer_id"] = producer_id;
      j["duration"] = d.count();
      j["num_msgs_polled"] = msgs.size();
      j["msg_queue_size"] = messageQueue.size();
      std::string json_string = j.dump();
      builder.payload(json_string);
      producer.produce(builder);
    }

  }

}

void Consumer::handleMessage() {

  // handle the next message in the queue
  cppkafka::Message msg = messageQueue.dequeue();

  // TODO maybe this is the right spot to handle errors and check conditions on the msg instance?

  // create an input stream for the parameter data
  const cppkafka::Buffer &data = msg.get_payload();
  std::vector<unsigned char> vec(data.begin(), data.end());
  BufferedInputStream buf(vec.begin(), vec.end());

  // get the service invoker
  Callable service = ProcessHandler::getInstance().getServiceHandler(msg.get_topic()).getInvoker(buf);

  // run the service
  service();

  // commit offset
  consumer->commit(msg);
}

void Consumer::createTopics(std::shared_ptr<cppkafka::Consumer> consumer, std::vector<std::string> topics) {
  // TODO clean up error handling in this routine
  for (auto it = topics.begin(); it != topics.end(); it++) {

    const char* topicname = (*it).data();
    int partition_cnt = 1;
    int replication_factor = 1;

    rd_kafka_t *rk = consumer->get_handle();
    rd_kafka_NewTopic_t *newt[1];
    const size_t newt_cnt = 1;
    rd_kafka_AdminOptions_t *options;
    rd_kafka_queue_t *rkqu;
    rd_kafka_event_t *rkev;
    const rd_kafka_CreateTopics_result_t *res;
    const rd_kafka_topic_result_t **terr;
    int timeout_ms = 10000;
    size_t res_cnt;
    rd_kafka_resp_err_t err;
    char errstr[512];

    rkqu = rd_kafka_queue_new(rk);

    newt[0] = rd_kafka_NewTopic_new(topicname, partition_cnt, replication_factor, errstr, sizeof(errstr));

    if (newt[0] == NULL) {
      throw SWA::ProgramError(errstr);
    }

    options = rd_kafka_AdminOptions_new(rk, RD_KAFKA_ADMIN_OP_CREATETOPICS);
    err = rd_kafka_AdminOptions_set_operation_timeout(options, timeout_ms, errstr, sizeof(errstr));

    if (err) {
      throw SWA::ProgramError(errstr);
    }

    rd_kafka_CreateTopics(rk, newt, newt_cnt, options, rkqu);

    /* Wait for result */
    rkev = rd_kafka_queue_poll(rkqu, timeout_ms + 2000);


    if (rd_kafka_event_error(rkev)) {
      throw SWA::ProgramError(rd_kafka_event_error_string(rkev));
    }

    res = rd_kafka_event_CreateTopics_result(rkev);

    terr = rd_kafka_CreateTopics_result_topics(res, &res_cnt);

    if (!terr) {
      throw SWA::ProgramError("terr is null");
    }

    if (res_cnt != newt_cnt) {
      throw SWA::ProgramError("res_cnt != newt_cnt");
    }

    if (rd_kafka_topic_result_error(terr[0]) && rd_kafka_topic_result_error(terr[0]) != RD_KAFKA_RESP_ERR_TOPIC_ALREADY_EXISTS) {
      throw SWA::ProgramError(std::string(rd_kafka_topic_result_name(terr[0])) + ": " + std::string(rd_kafka_topic_result_error_string(terr[0])));
    }

    rd_kafka_event_destroy(rkev);
    rd_kafka_queue_destroy(rkqu);
    rd_kafka_AdminOptions_destroy(options);
    rd_kafka_NewTopic_destroy(newt[0]);

  }
}

Consumer &Consumer::getInstance() {
  const size_t max_capacity = SWA::parse<uint32_t>(SWA::CommandLine::getInstance().getOption(MaxCapacityOption, MaxCapacityOptionDefault));
  static Consumer instance(max_capacity);
  return instance;
}

void MessageQueue::enqueue(std::vector<cppkafka::Message> &msgs) {
  if (msgs.size() > 0) {
    std::lock_guard<std::mutex> lock(mutex);
    if (size() + msgs.size() <= capacity()) {
      for (auto it = msgs.begin(); it != msgs.end(); it++) {
        transferQueue.push_back(std::move(*it));
      }
    } else {
      throw std::length_error("No space left in queue.");
    }
  }
}

cppkafka::Message MessageQueue::dequeue() {
  // transfer a batch of messages if necessary
  if ((internalQueue.empty() && !transferQueue.empty()) || transferQueue.size() > (capacity() / 2)) {
    std::lock_guard<std::mutex> lock(mutex);
    while (!transferQueue.empty()) {
      internalQueue.push_back(std::move(transferQueue.front()));
      transferQueue.pop_front();
    }
  }
  // pop the next message in line
  if (!internalQueue.empty()) {
    cppkafka::Message msg = std::move(internalQueue.front());
    internalQueue.pop_front();
    return msg;
  } else {
    throw std::out_of_range("Queue is empty");
  }
}

} // namespace Kafka
