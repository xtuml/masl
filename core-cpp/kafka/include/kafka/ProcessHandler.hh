#ifndef Kafka_ProcessHandler_HH
#define Kafka_ProcessHandler_HH

#include "ServiceHandler.hh"
#include "Producer.hh"

#include "amqp_asio/sender.hh"
#include "logging/log.hh"

#include <asio/awaitable.hpp>
#include <asio/io_context.hpp>
#include <map>
#include <memory>
#include <string>
#include <vector>

namespace Kafka {

class ProcessHandler {
public:
  ProcessHandler(): log(xtuml::logging::Logger("amqp-bridge")) {};
  ProcessHandler(const ProcessHandler&) = delete;
  ProcessHandler& operator=(const ProcessHandler&) = delete;

  bool registerServiceHandler(int domainId, int serviceId,
                              std::shared_ptr<ServiceHandler> handler);

  ServiceHandler &getServiceHandler(int domainId, int serviceId);

  ServiceHandler &getServiceHandler(std::string topicName);

  std::vector<std::string> getTopicNames();

  bool hasRegisteredServices() { return serviceLookup.size() > 0; }

  bool setCustomTopicName(int domainId, int serviceId, std::string topicName);

  std::string getTopicName(int domainId, int serviceId);

  asio::io_context &getContext();

  amqp_asio::Sender &getSender();

  xtuml::logging::Logger &getLog();

  std::shared_ptr<Producer> getProducer();

  asio::awaitable<void> run();

  static ProcessHandler &getInstance();



private:
  typedef std::map<std::string, std::shared_ptr<ServiceHandler>> ServiceLookup;
  typedef std::map<std::pair<int, int>, std::string> TopicMap;

  ServiceLookup serviceLookup;
  TopicMap customTopicNames;
  asio::io_context ctx;
  std::shared_ptr<Producer> producer;
  xtuml::logging::Logger log;
};

} // namespace Kafka

#endif
