#include "kafka/Producer.hh"

#include "cppkafka/buffer.h"

#include "kafka/Kafka.hh"
#include "kafka/ProcessHandler.hh"

#include "swa/CommandLine.hh"

namespace Kafka {

Producer::Producer() {
  const std::string brokers =
      SWA::CommandLine::getInstance().getOption(BrokersOption);
  cppkafka::Configuration config = {{"metadata.broker.list", brokers}};
  prod = std::unique_ptr<cppkafka::Producer>(new cppkafka::Producer(config));
}

void Producer::publish(int domainId, int serviceId, cppkafka::Buffer& data, cppkafka::Buffer& partKey) {
  // find/create a message builder
  std::shared_ptr<cppkafka::MessageBuilder> msgBuilder;
  TopicLookup::iterator entry = topicLookup.find(std::make_pair(domainId, serviceId));
  if (entry == topicLookup.end()) {
    std::string topicName = ProcessHandler::getInstance().getTopicName(domainId, serviceId);
    msgBuilder = std::shared_ptr<cppkafka::MessageBuilder>(new cppkafka::MessageBuilder(topicName));
    topicLookup.insert(TopicLookup::value_type(
        std::make_pair(domainId, serviceId), msgBuilder));
  } else {
    msgBuilder = entry->second;
  }

  // set the partion key
  if (partKey.begin() != partKey.end()) {
    msgBuilder->key(partKey);
  }

  // Set the payload on this builder
  msgBuilder->payload(data);

  // Produce the message
  prod->produce(*msgBuilder);
}

void Producer::publish(int domainId, int serviceId, std::vector<std::uint8_t> data, std::vector<std::uint8_t> partKey) {
  cppkafka::Buffer dataBuffer = cppkafka::Buffer(data);
  cppkafka::Buffer keyBuffer = cppkafka::Buffer(partKey);
  publish(domainId, serviceId, dataBuffer, keyBuffer);
}

void Producer::publish(int domainId, int serviceId, std::string data, std::string partKey) {
  cppkafka::Buffer dataBuffer = cppkafka::Buffer(data);
  cppkafka::Buffer keyBuffer = cppkafka::Buffer(partKey);
  publish(domainId, serviceId, dataBuffer, keyBuffer);
}

Producer &Producer::getInstance() {
  static Producer instance;
  return instance;
}

} // namespace Kafka
