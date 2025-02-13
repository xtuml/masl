#include "kafka/Kafka.hh"

#include "kafka/Consumer.hh"
#include "kafka/ProcessHandler.hh"

#include "swa/CommandLine.hh"
#include "swa/Process.hh"

#include <asio/co_spawn.hpp>
#include <asio/detached.hpp>
#include <thread>

namespace Kafka {

const char *const BrokersOption     = "-kafka-broker-list";
const char *const GroupIdOption     = "-kafka-group-id";
const char *const NamespaceOption   = "-kafka-namespace";
const char *const OffsetResetOption = "-kafka-offset-reset";

bool startup() {
  std::thread{[] { 
    try {
        auto& ctx = ProcessHandler::getInstance().getContext();
        auto ex = ctx.get_executor();
        // log.info("Running"); TODO
        asio::co_spawn(ex, ProcessHandler::getInstance().run(), asio::detached);
        ctx.run();
        return true;
    } catch (std::exception &e) {
        // log.error("Exception: {}", e); TODO
        return false;
    }
  }}.detach();
  return true;
}

struct Init {
  Init() {

    // register command line arguments
    SWA::CommandLine::getInstance().registerOption(SWA::NamedOption(BrokersOption, std::string("Kafka Brokers"), false, "brokerList", true, false));
    SWA::CommandLine::getInstance().registerOption(SWA::NamedOption(GroupIdOption, std::string("Kafka Group ID"), false, "groupId", true, false));
    SWA::CommandLine::getInstance().registerOption(SWA::NamedOption(NamespaceOption, std::string("Kafka Topic Namespace"), false, "namespace", true, false));
    SWA::CommandLine::getInstance().registerOption(SWA::NamedOption(OffsetResetOption, std::string("Consumer Offset Reset Policy"), false, "offset", true, false));

    SWA::Process::getInstance().registerStartedListener(&startup);
  }
} init;

} // namespace Kafka
