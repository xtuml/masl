#ifndef Kafka_Consumer_HH
#define Kafka_Consumer_HH

#include "boost/circular_buffer.hpp"

#include "cppkafka/consumer.h"
#include "cppkafka/message.h"

#include "swa/ListenerPriority.hh"
#include "swa/Process.hh"
#include "swa/RealTimeSignalListener.hh"
#include "swa/TimerListener.hh"

#include <mutex>
#include <string>
#include <utility>
#include <vector>

namespace Kafka {

class Consumer {

public:
  Consumer(size_t c)
    : messageQueue(c),
      msgListener([this](int pid, int uid) { this->handleSignal(); }, SWA::Process::getInstance().getActivityMonitor()),
      timer(SWA::ListenerPriority::getNormal(), [this](int overrun) { this->pollMessages(); }) {}
  void run();
  static Consumer &getInstance();

private:
  std::unique_ptr<cppkafka::Consumer> consumer;
  boost::circular_buffer<cppkafka::Message> messageQueue;
  SWA::RealTimeSignalListener msgListener;
  SWA::TimerListener timer;

  void createTopics(std::vector<std::string> topics);
  void handleSignal();
  void pollMessages();
};

} // namespace Kafka

#endif
