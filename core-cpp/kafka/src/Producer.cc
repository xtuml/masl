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
  log.info("LEVI4");
  std::future<void> future = asio::co_spawn(executor, [&]() -> asio::awaitable<void> {
    log.info("LEVI5");
    std::string topicName = ProcessHandler::getInstance().getTopicName(domainId, serviceId);
    log.info("LEVI6");
    co_await ProcessHandler::getInstance().getSender().send(data, amqp_asio::messages::Properties{.to = topicName});
    log.info("LEVI7");
  }, asio::use_future);
  future.wait();
  log.info("LEVI7.1");
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
  log.info("LEVI8");
  std::future<void> future = asio::co_spawn(executor, [&]() -> asio::awaitable<void> {
    log.info("LEVI9");
    std::string topicName = ProcessHandler::getInstance().getTopicName(domainId, serviceId);
    log.info("LEVI10");
    co_await ProcessHandler::getInstance().getSender().send(payload, amqp_asio::messages::Properties{.to = topicName});
    log.info("LEVI11");
  }, asio::use_future);
  future.wait();
  log.info("LEVI11.1");
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
