#ifndef Kafka_Consumer_HH
#define Kafka_Consumer_HH

#include "DataConsumer.hh"

#include "amqp_asio/session.hh"
#include "amqp_asio/spawn.hh"

#include <condition_variable>
#include <mutex>
#include <queue>
#include <string>
#include <utility>
#include <vector>

namespace Kafka {

typedef std::pair<std::string, amqp_asio::Delivery> TaggedMessage;

class MessageQueue {
public:
  void enqueue(TaggedMessage &msg);
  TaggedMessage dequeue();
  std::vector<TaggedMessage> dequeue_all();
  bool empty() { return queue.empty(); }

private:
  std::queue<TaggedMessage> queue;
  mutable std::mutex mutex;
  std::condition_variable cond;
};

class Consumer {

public:
  Consumer(amqp_asio::Session& session) : session(session) {}
  bool consumeOne(DataConsumer& dataConsumer);
  void initialize(std::vector<std::string> topics);
  void initialize(std::string topic) {
    std::vector<std::string> topics;
    topics.push_back(topic);
    initialize(topics);
  }

private:
  MessageQueue messageQueue;
  amqp_asio::Session session;
  void handleMessages();
};

} // namespace Kafka

#endif
