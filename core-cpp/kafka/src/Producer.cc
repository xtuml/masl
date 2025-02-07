#include "kafka/Producer.hh"

#include "kafka/Kafka.hh"
#include "kafka/ProcessHandler.hh"

#include "amqp_asio/messages.hh"
#include "swa/CommandLine.hh"

namespace Kafka {

Producer::Producer() {
  const std::string brokers = SWA::CommandLine::getInstance().getOption(BrokersOption);
  /* TODO
  cppkafka::Configuration config = {{"metadata.broker.list", brokers}};
  prod = std::unique_ptr<cppkafka::Producer>(new cppkafka::Producer(config));
  */
}

void Producer::publish(int domainId, int serviceId, std::string data, std::string partKey) {
  co_await prod.send(data,amqp_asio::messages::Properties{.to = "example.channel"});
}

void Producer::publish(int domainId, int serviceId, std::vector<std::uint8_t> data, std::vector<std::uint8_t> partKey) {
  /* TODO
  cppkafka::Buffer dataBuffer = cppkafka::Buffer(data);
  cppkafka::Buffer keyBuffer = cppkafka::Buffer(partKey);
  publish(domainId, serviceId, dataBuffer, keyBuffer);
  */
}

void Producer::publish(int domainId, int serviceId, std::vector<std::uint8_t> data) {
  publish(domainId, serviceId, data, std::vector<std::uint8_t>());
}

void Producer::publish(int domainId, int serviceId, std::string data) {
  publish(domainId, serviceId, data, "");
}

Producer &Producer::getInstance() {
  static Producer instance;
  return instance;
}

} // namespace Kafka
