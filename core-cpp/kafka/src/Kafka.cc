#include "kafka/Kafka.hh"

#include "kafka/Consumer.hh"
#include "kafka/ProcessHandler.hh"

#include "swa/CommandLine.hh"
#include "swa/Process.hh"

#include <asio/co_spawn.hpp>
#include <asio/detached.hpp>
#include <thread>

namespace Kafka {

const char *const BrokerOption       = "-activemq-hostname";
const char *const UsernameOption     = "-activemq-username";
const char *const PasswordOption     = "-activemq-password";
const char *const PortNoOption       = "-activemq-port";

bool startup() {
  std::thread{[] { 
    try {
        auto& ctx = ProcessHandler::getInstance().getContext();
        auto ex = ctx.get_executor();
        asio::co_spawn(ex, ProcessHandler::getInstance().run(), asio::detached);
        ctx.run();
        return true;
    } catch (std::exception &e) {
        return false;
    }
  }}.detach();
  return true;
}

struct Init {
  Init() {

    // register command line arguments
    SWA::CommandLine::getInstance().registerOption(SWA::NamedOption(BrokerOption, std::string("Broker URL"), true, "broker", true, false));
    SWA::CommandLine::getInstance().registerOption(SWA::NamedOption(UsernameOption, std::string("Broker Username"), false, "username", true, false));
    SWA::CommandLine::getInstance().registerOption(SWA::NamedOption(PasswordOption, std::string("Broker Password"), false, "password", true, false));
    SWA::CommandLine::getInstance().registerOption(SWA::NamedOption(PortNoOption, std::string("Broker Port Number"), false, "port", true, false));

    SWA::Process::getInstance().registerStartedListener(&startup);
  }
} init;

} // namespace Kafka
