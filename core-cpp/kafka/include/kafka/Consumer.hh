#ifndef Kafka_Consumer_HH
#define Kafka_Consumer_HH

#include "cppkafka/consumer.h"
#include "cppkafka/message.h"

#include "boost/circular_buffer.hpp"

#include <mutex>
#include <string>
#include <utility>
#include <vector>

namespace Kafka {

class MessageQueue {
public:
  typedef typename boost::circular_buffer<cppkafka::Message>::size_type size_type;

  MessageQueue(size_t c): max_capacity(c), internalQueue(c), transferQueue(c) {}

  void enqueue(std::vector<cppkafka::Message> &msgs);
  cppkafka::Message dequeue();

  bool empty() { return !(internalQueue.empty() && transferQueue.empty()); }
  MessageQueue::size_type size() { return internalQueue.size() + transferQueue.size(); }
  size_t capacity() { return max_capacity; }

private:
  size_t max_capacity;
  boost::circular_buffer<cppkafka::Message> internalQueue;
  boost::circular_buffer<cppkafka::Message> transferQueue;
  mutable std::mutex mutex;
};

class Consumer {

public:
  Consumer(size_t c): messageQueue(c) {}
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
