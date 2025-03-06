#ifndef Kafka_Producer_HH
#define Kafka_Producer_HH

#include "cppkafka/message_builder.h"
#include "cppkafka/producer.h"

namespace Kafka {

class Producer {

public:
  Producer();
  void publish(int domainId, int serviceId, std::vector<std::uint8_t> data, std::vector<std::uint8_t> partKey);
  void publish(int domainId, int serviceId, std::string data, std::string partKey);
  void publish(int domainId, int serviceId, std::vector<std::uint8_t> data);
  void publish(int domainId, int serviceId, std::string data);
  static Producer &getInstance();

private:
  typedef std::pair<int, int> ServiceKey;
  typedef std::map<ServiceKey, std::shared_ptr<cppkafka::MessageBuilder>> TopicLookup;
  TopicLookup topicLookup;
  std::unique_ptr<cppkafka::Producer> prod;
  void publish(int domainId, int serviceId, cppkafka::Buffer& data, cppkafka::Buffer& partKey);
};

} // namespace Kafka

#endif
