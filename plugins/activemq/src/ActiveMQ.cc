#include "activemq/ActiveMQ.hh"

#include "LogAppender.hh"

#include "idm/ProcessHandler.hh"
#include "swa/CommandLine.hh"
#include "swa/Process.hh"

#include <thread>

namespace InterDomainMessaging {

    namespace ActiveMQ {

        const char *const BrokerOption = "-activemq-hostname";
        const char *const UsernameOption = "-activemq-username";
        const char *const PasswordOption = "-activemq-password";
        const char *const PortNoOption = "-activemq-port";

        bool startup() {
            std::thread{[] {
                try {
                    auto &ctx = ProcessHandler::getInstance().getContext();
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

                // register log appender
                log4cplus::spi::getAppenderFactoryRegistry().put(std::make_unique<InterDomainMessaging::ActiveMQ::ActiveMQAppenderFactory>());

                // register command line arguments
                SWA::CommandLine::getInstance().registerOption(SWA::NamedOption(BrokerOption, std::string("Broker URL"), true, "broker", true, false));
                SWA::CommandLine::getInstance().registerOption(SWA::NamedOption(UsernameOption, std::string("Broker Username"), false, "username", true, false));
                SWA::CommandLine::getInstance().registerOption(SWA::NamedOption(PasswordOption, std::string("Broker Password"), false, "password", true, false));
                SWA::CommandLine::getInstance().registerOption(SWA::NamedOption(PortNoOption, std::string("Broker Port Number"), false, "port", true, false));

                SWA::Process::getInstance().registerStartedListener(&startup);
            }
        } init;

    } // namespace ActiveMQ

} // namespace InterDomainMessaging
