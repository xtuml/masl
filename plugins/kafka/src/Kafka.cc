#include "kafka/Kafka.hh"

#include "kafka/Consumer.hh"
#include "kafka/ProcessHandler.hh"

#include "swa/CommandLine.hh"
#include "swa/Process.hh"

#include "LogAppender.hh"

#include <thread>

namespace Kafka {

const char *const BrokersOption     = "-kafka-broker-list";
const char *const GroupIdOption     = "-kafka-group-id";
const char *const NamespaceOption   = "-kafka-namespace";
const char *const OffsetResetOption = "-kafka-offset-reset";

bool startup() {
  if (ProcessHandler::getInstance().hasRegisteredServices()) {
    // only start the consumer if there are registered services
    std::thread{[] { ProcessHandler::getInstance().startConsumer(); }}.detach();
  }

  return true;
}

struct Init {
  Init() {
    // register Kafka log appender
    log4cplus::spi::getAppenderFactoryRegistry().put(std::make_unique<Kafka::KafkaAppenderFactory>());


      // register command line arguments
    SWA::CommandLine::getInstance().registerOption(SWA::NamedOption(BrokersOption, std::string("Kafka Brokers"), true, "brokerList", true, false));
    SWA::CommandLine::getInstance().registerOption(SWA::NamedOption(GroupIdOption, std::string("Kafka Group ID"), false, "groupId", true, false));
    SWA::CommandLine::getInstance().registerOption(SWA::NamedOption(NamespaceOption, std::string("Kafka Topic Namespace"), false, "namespace", true, false));
    SWA::CommandLine::getInstance().registerOption(SWA::NamedOption(OffsetResetOption, std::string("Consumer Offset Reset Policy"), false, "offset", true, false));

    SWA::Process::getInstance().registerStartedListener(&startup);
  }
} init;

} // namespace Kafka
