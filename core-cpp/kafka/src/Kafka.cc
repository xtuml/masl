#include "kafka/Kafka.hh"

#include "kafka/Consumer.hh"
#include "kafka/ProcessHandler.hh"

#include "swa/CommandLine.hh"
#include "swa/Process.hh"

namespace Kafka {

const char *const BrokersOption         = "-kafka-broker-list";
const char *const GroupIdOption         = "-kafka-group-id";
const char *const NamespaceOption       = "-kafka-namespace";
const char *const MaxCapacityOption     = "-kafka-max-capacity";

bool startup() {
  if (ProcessHandler::getInstance().hasRegisteredServices()) {
    // only start the consumer if there are registered services
    Consumer::getInstance().run();
  }
  return true;
}

struct Init {
  Init() {
    // register command line arguments
    SWA::CommandLine::getInstance().registerOption(SWA::NamedOption(BrokersOption, std::string("Kafka Brokers"), true, "brokerList", true, false));
    SWA::CommandLine::getInstance().registerOption(SWA::NamedOption(GroupIdOption, std::string("Kafka Group ID"), false, "groupId", true, false));
    SWA::CommandLine::getInstance().registerOption(SWA::NamedOption(NamespaceOption, std::string("Kafka Topic Namespace"), false, "namespace", true, false));
    SWA::CommandLine::getInstance().registerOption(SWA::NamedOption(MaxCapacityOption, std::string("Kafka Max Pending Event Capacity"), false, "maxCapacity", true, false));

    SWA::Process::getInstance().registerStartedListener(&startup);
  }
} init;

} // namespace Kafka
