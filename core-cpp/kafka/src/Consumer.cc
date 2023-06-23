#include "kafka/Consumer.hh"

#include "kafka/BufferedIO.hh"
#include "kafka/Kafka.hh"
#include "kafka/ProcessHandler.hh"

#include "swa/CommandLine.hh"
#include "swa/Process.hh"
#include "swa/RealTimeSignalListener.hh"

#include "cppkafka/buffer.h"
#include "cppkafka/configuration.h"
#include "cppkafka/consumer.h"
#include "cppkafka/utils/consumer_dispatcher.h"

#include <uuid/uuid.h>

namespace Kafka {

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
                                    {"group.id", groupId}};

  // Create the consumer
  cppkafka::Consumer consumer(config);

  // Subscribe to topics
  consumer.subscribe(ProcessHandler::getInstance().getTopicNames());

  // Create a consumer dispatcher
  cppkafka::ConsumerDispatcher dispatcher(consumer);

  // Stop processing on SIGINT
  // TODO set up lifecycle event to stop dispatcher
  SWA::Process::getInstance().registerShutdownListener(
      [&]() { dispatcher.stop(); });

  // create a signal listener
  SWA::RealTimeSignalListener listener(
      [this](int pid, int uid) { this->handleMessages(); },
      SWA::Process::getInstance().getActivityMonitor());

  // Now run the dispatcher, providing a callback to handle messages, one to
  // handle errors and another one to handle EOF on a partition
  dispatcher.run(
      // Callback executed whenever a new message is consumed
      [&](cppkafka::Message msg) {
        // Queue the message to be handled on the main thread
        messageQueue.enqueue(msg);
        listener.queueSignal();
      }
      // TODO error handling
  );
}

void Consumer::handleMessages() {
  // drain the message queue
  try {
    while (true) {
      // dequeue the message
      Message msg = messageQueue.dequeue();

      // create an input stream for the parameter data
      BufferedInputStream buf(msg.second.begin(), msg.second.end());

      // get the service invoker
      std::function<void()> service = ProcessHandler::getInstance().getServiceHandler(msg.first).getInvoker(buf);

      // run the service
      service();
    }
  } catch (std::out_of_range &e) {
    // the queue is empty
  }
}

Consumer &Consumer::getInstance() {
  static Consumer instance;
  return instance;
}

void MessageQueue::enqueue(cppkafka::Message &msg) {
  const cppkafka::Buffer &data = msg.get_payload();
  std::vector<unsigned char> vec(data.begin(), data.end());
  std::lock_guard<std::mutex> lock(mutex);
  queue.push(std::make_pair(msg.get_topic(), vec));
  cond.notify_one();
}

Message MessageQueue::dequeue() {
  std::lock_guard<std::mutex> lock(mutex);
  if (queue.empty()) {
    throw std::out_of_range("Queue is empty");
  }
  Message msg = queue.front();
  queue.pop();
  return msg;
}

} // namespace Kafka
