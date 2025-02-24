#include "kafka/Producer.hh"

#include "kafka/Kafka.hh"
#include "kafka/ProcessHandler.hh"

#include "amqp_asio/messages.hh"
#include "swa/CommandLine.hh"

#include <asio/co_spawn.hpp>
#include <asio/use_future.hpp>
#include <future>

namespace Kafka {

void Producer::publish(int domainId, int serviceId, std::string data, std::string partKey) {
  // TODO do we need both of these signatures? Maybe just JSON...
  // TODO partKey doesn't do anything?
  // TODO redo the publish API to take advantage of the fact that sending a nlohmann_json object directly is supported
  auto log = ProcessHandler::getInstance().getLog();
  auto executor = ProcessHandler::getInstance().getContext().get_executor();
  std::future<void> future = asio::co_spawn(executor, [&]() -> asio::awaitable<void> {
    std::string topicName = ProcessHandler::getInstance().getTopicName(domainId, serviceId);
    co_await ProcessHandler::getInstance().getSender().send(data, amqp_asio::messages::Properties{.to = topicName});
  }, asio::use_future);
  future.wait();
}

void Producer::publish(int domainId, int serviceId, std::vector<std::uint8_t> data, std::vector<std::uint8_t> partKey) {
  // TODO do we need both of these signatures? Maybe just JSON...
  // TODO partKey doesn't do anything?
  // TODO redo the publish API to take advantage of the fact that sending a nlohmann_json object directly is supported
  auto log = ProcessHandler::getInstance().getLog();
  std::vector<std::byte> payload(data.size());
  for (size_t i = 0; i < data.size(); ++i) {
    payload[i] = static_cast<std::byte>(data[i]);
  }
  auto executor = ProcessHandler::getInstance().getContext().get_executor();
  std::future<void> future = asio::co_spawn(executor, [&]() -> asio::awaitable<void> {
    std::string topicName = ProcessHandler::getInstance().getTopicName(domainId, serviceId);
    co_await ProcessHandler::getInstance().getSender().send(payload, amqp_asio::messages::Properties{.to = topicName});
  }, asio::use_future);
  future.wait();
}

void Producer::publish(int domainId, int serviceId, std::vector<std::uint8_t> data) {
  publish(domainId, serviceId, data, std::vector<std::uint8_t>());
}

void Producer::publish(int domainId, int serviceId, std::string data) {
  publish(domainId, serviceId, data, "");
}

Producer &Producer::getInstance() {
  static Producer producer;
  return producer;
}

} // namespace Kafka
