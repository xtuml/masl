#ifndef Kafka_Producer_HH
#define Kafka_Producer_HH

#include "BufferedIO.hh"

#include "cppkafka/message_builder.h"
#include "cppkafka/producer.h"

namespace Kafka {

class Producer {

public:
  Producer();
  void publish(int domainId, int serviceId, BufferedOutputStream &buf, BufferedOutputStream &partKey);
  static Producer &getInstance();

private:
  typedef std::pair<int, int> ServiceKey;
  typedef std::map<ServiceKey, std::shared_ptr<cppkafka::MessageBuilder>> TopicLookup;
  TopicLookup topicLookup;
  std::unique_ptr<cppkafka::Producer> prod;
};

} // namespace Kafka

#endif
