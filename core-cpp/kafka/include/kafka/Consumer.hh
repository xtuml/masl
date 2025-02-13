#ifndef Kafka_Consumer_HH
#define Kafka_Consumer_HH

#include "cppkafka/consumer.h"
#include "cppkafka/message.h"

#include "DataConsumer.hh"

#include <condition_variable>
#include <mutex>
#include <queue>
#include <string>
#include <utility>
#include <vector>

namespace Kafka {

class MessageQueue {
public:
  void enqueue(cppkafka::Message &msg);
  cppkafka::Message dequeue();
  std::vector<cppkafka::Message> dequeue_all();
  bool empty() { return queue.empty(); }

private:
  std::queue<cppkafka::Message> queue;
  mutable std::mutex mutex;
  std::condition_variable cond;
};

class Consumer {

public:
  Consumer(std::string topic);
  Consumer(std::vector<std::string> topics);
  bool consumeOne(DataConsumer& dataConsumer);

private:
  MessageQueue messageQueue;
  std::unique_ptr<cppkafka::Consumer> consumer;

  void initialize(std::vector<std::string> topics);
  void handleMessages();
  void createTopics(std::vector<std::string> topics);
};

} // namespace Kafka

#endif
