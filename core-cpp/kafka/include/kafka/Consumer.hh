#ifndef Kafka_Consumer_HH
#define Kafka_Consumer_HH

#include "cppkafka/message.h"

#include <condition_variable>
#include <mutex>
#include <queue>
#include <string>
#include <utility>
#include <vector>

namespace Kafka {

typedef std::pair<std::string, std::vector<unsigned char>> Message;

class MessageQueue {
public:
  void enqueue(cppkafka::Message &msg);
  Message dequeue();

private:
  std::queue<Message> queue;
  mutable std::mutex mutex;
  std::condition_variable cond;
};

class Consumer {

public:
  void run();
  static Consumer &getInstance();

private:
  MessageQueue messageQueue;

  void handleMessage();
};

} // namespace Kafka

#endif
