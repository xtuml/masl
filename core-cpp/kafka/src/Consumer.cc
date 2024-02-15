#include "kafka/Consumer.hh"

#include "kafka/BufferedIO.hh"
#include "kafka/Kafka.hh"
#include "kafka/ProcessHandler.hh"

#include "swa/CommandLine.hh"
#include "swa/Duration.hh"
#include "swa/Process.hh"
#include "swa/ProgramError.hh"
#include "swa/Timestamp.hh"
#include "swa/parse.hh"

#include "cppkafka/buffer.h"
#include "cppkafka/configuration.h"

#include <uuid/uuid.h>

namespace Kafka {

const char *const MaxCapacityOptionDefault = "1000";
void Consumer::run() {

  // Get command line options
  const std::string brokers = SWA::CommandLine::getInstance().getOption(BrokersOption);

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
  consumer = std::make_unique<cppkafka::Consumer>(config);

  // create topics if they don't already exist
  createTopics(ProcessHandler::getInstance().getTopicNames());

  // short delay to avoid race conditions if other processes initiated topic creation
  SWA::delay(SWA::Duration::fromMillis(100));

  // Subscribe to topics
  consumer->subscribe(ProcessHandler::getInstance().getTopicNames());

  // schedule the polling timer
  timer.schedule(SWA::Timestamp::now(), SWA::Duration::fromMillis(10));

}

void Consumer::handleSignal() {
  // handle the next message in the queue
  cppkafka::Message msg = std::move(messageQueue.front());
  messageQueue.pop_front();

  // if the message queue is empty, trigger a poll as soon as possible
  if (messageQueue.empty()) {
    timer.cancel();
    timer.schedule(SWA::Timestamp::now(), SWA::Duration::fromMillis(10));
  }

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

void Consumer::pollMessages() {
  if (messageQueue.size() < (messageQueue.capacity() / 2)) {
    // poll a batch of messages. do not wait.
    std::vector<cppkafka::Message> msgs = consumer->poll_batch(messageQueue.capacity() - messageQueue.size(), std::chrono::milliseconds::zero());

    // queue the messages for handling by the architecture
    for (auto it = msgs.begin(); it != msgs.end(); it++) {
      messageQueue.push_back(std::move(*it));
      msgListener.queueSignal();
    }
  }
}

void Consumer::createTopics(std::vector<std::string> topics) {
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

} // namespace Kafka
