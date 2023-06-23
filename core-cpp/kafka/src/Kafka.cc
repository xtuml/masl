#include "kafka/Kafka.hh"

#include "kafka/Consumer.hh"
#include "kafka/ProcessHandler.hh"

#include "swa/CommandLine.hh"
#include "swa/Process.hh"

#include <thread>

namespace Kafka {

const char *const BrokersOption = "-kafka-broker-list";
const char *const GroupIdOption = "-kafka-group-id";

bool startup() {
  if (ProcessHandler::getInstance().hasRegisteredServices()) {
    // only start the consumer if there are registered services
    std::thread{[] { Consumer::getInstance().run(); }}.detach();
  }

  return true;
}

struct Init {
  Init() {
    // register command line arguments
    SWA::CommandLine::getInstance().registerOption(SWA::NamedOption(BrokersOption, std::string("Kafka Brokers"), true, "brokerList", true, false));
    SWA::CommandLine::getInstance().registerOption(SWA::NamedOption(GroupIdOption, std::string("Kafka Group ID"), false, "groupId", true, false));

    SWA::Process::getInstance().registerStartedListener(&startup);
  }
} init;

} // namespace Kafka
