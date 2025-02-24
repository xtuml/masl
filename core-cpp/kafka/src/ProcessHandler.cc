#include "kafka/ProcessHandler.hh"

#include "kafka/Kafka.hh"
#include "kafka/Consumer.hh"

#include "swa/CommandLine.hh"
#include "swa/Process.hh"
#include "swa/ProgramError.hh"

#include <asio/detached.hpp>

using namespace std::literals;

namespace Kafka {

bool ProcessHandler::registerServiceHandler(
    int domainId, int serviceId, std::shared_ptr<ServiceHandler> handler) {
  const std::string topicName = getTopicName(domainId, serviceId);
  serviceLookup.insert(ServiceLookup::value_type(topicName, handler));
  return true;
}

ServiceHandler &ProcessHandler::getServiceHandler(int domainId, int serviceId) {
  const std::string topicName = getTopicName(domainId, serviceId);
  return getServiceHandler(topicName);
}
ServiceHandler &ProcessHandler::getServiceHandler(std::string topicName) {
  ServiceLookup::iterator it = serviceLookup.find(topicName);

  if (it == serviceLookup.end()) {
    throw SWA::ProgramError("No Kafka topic registered for '" + topicName + "'");
  }

  return *(it->second);
}

std::vector<std::string> ProcessHandler::getTopicNames() {
  std::vector<std::string> topicNames;
  for (auto it = serviceLookup.begin(); it != serviceLookup.end(); it++) {
    topicNames.push_back(it->first);
  }
  return topicNames;
}

bool ProcessHandler::setCustomTopicName(int domainId, int serviceId, std::string topicName) {
  std::pair<int, int> key (domainId, serviceId);
  customTopicNames.insert(TopicMap::value_type(key, topicName));
  return true;
}

std::string ProcessHandler::getTopicName(int domainId, int serviceId) {
  std::string name = "";

  // get the base name
  std::pair<int, int> key (domainId, serviceId);
  if (customTopicNames.contains(key)) {
    name = customTopicNames[key];
  } else {
    name = SWA::Process::getInstance().getDomain(domainId).getName() + "_service" + std::to_string(serviceId);
  }

  return name;
}

xtuml::logging::Logger &ProcessHandler::getLog() {
  return log;
}

asio::io_context &ProcessHandler::getContext() {
  return ctx;
}

amqp_asio::Sender ProcessHandler::getSender() {
  return sender;
}

amqp_asio::Session ProcessHandler::getSession() {
  return session;
}

asio::awaitable<void> ProcessHandler::run() {

  const std::string hostname = SWA::CommandLine::getInstance().getOption(BrokerOption);
  const std::string username = SWA::CommandLine::getInstance().getOption(UsernameOption, "artemis");
  const std::string password = SWA::CommandLine::getInstance().getOption(PasswordOption, "artemis");
  const std::string port = SWA::CommandLine::getInstance().getOption(PortNoOption, "5672");

  try {
      auto executor = co_await asio::this_coro::executor;

      // create connection
      conn = amqp_asio::Connection::create_amqp("amqp-bridge", executor);
      co_await conn.open(amqp_asio::ConnectionOptions().hostname(hostname).port(port).sasl_options(
          amqp_asio::SaslOptions().authname(username).password(password)
      ));
      log.debug("Connection open");

      // open a session
      session = co_await conn.open_session();
      log.debug("Session open");

      // launch consumer
      if (hasRegisteredServices()) {
        consumer.initialize(getTopicNames());
      }

      // create producer
      sender = co_await session.open_sender(amqp_asio::SenderOptions().name("sender").delivery_mode(amqp_asio::DeliveryMode::at_least_once));
      log.debug("Sender created");

      // Clean up on shutdown
      SWA::Process::getInstance().registerShutdownListener([this, executor]() {
        asio::co_spawn(executor, [this]() -> asio::awaitable<void> {
          co_await session.end();
          co_await conn.close();
          ctx.stop();
        }, asio::detached);
      });

  } catch (std::bad_variant_access &e) {
      throw std::system_error(make_error_code(std::errc::bad_message));
  } catch (const std::system_error &e) {
      fmt::println("Error: {}", e.code().message());
  }

  log.debug("Initialization complete");

}

ProcessHandler &ProcessHandler::getInstance() {
  static ProcessHandler instance;
  return instance;
}

namespace {

// Load up the domain handlers once the process has initialised and domains are
// known.
void loadLibs() {
  SWA::Process::getInstance().loadDynamicLibraries("kafka", "_if", false);
}

bool initialise() {
  SWA::Process::getInstance().registerInitialisedListener(&loadLibs);
  return true;
}

bool initialised = initialise();

} // namespace

} // namespace Kafka
