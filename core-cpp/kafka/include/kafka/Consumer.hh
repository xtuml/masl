#ifndef Kafka_Consumer_HH
#define Kafka_Consumer_HH

#include "cppkafka/consumer.h"
#include "cppkafka/message.h"

#include <mutex>
#include <queue>
#include <string>
#include <utility>
#include <vector>

namespace Kafka {

class MessageQueue {
public:
  static const size_t MAX_CAPACITY;
  typedef typename std::queue<cppkafka::Message>::size_type size_type;
  void enqueue(std::vector<cppkafka::Message> &msgs);
  cppkafka::Message dequeue();
  bool empty() { return !(internalQueue.empty() && transferQueue.empty()); };
  MessageQueue::size_type size() { return internalQueue.size() + transferQueue.size(); };

private:
  std::queue<cppkafka::Message> internalQueue;
  std::queue<cppkafka::Message> transferQueue;
  mutable std::mutex mutex;
};

class Consumer {

public:
  void run();
  static Consumer &getInstance();

private:
  MessageQueue messageQueue;
  std::shared_ptr<cppkafka::Consumer> consumer;

  void handleMessage();

  void createTopics(std::shared_ptr<cppkafka::Consumer> consumer, std::vector<std::string> topics);
};

} // namespace Kafka

#endif
