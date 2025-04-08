#include "activemq/ActiveMQ.hh"

#include "LogAppender.hh"
#include "swa/CommandLine.hh"

#include <thread>

namespace InterDomainMessaging {

    namespace ActiveMQ {

        const char *const BrokerOption = "-activemq-hostname";
        const char *const UsernameOption = "-activemq-username";
        const char *const PasswordOption = "-activemq-password";
        const char *const PortNoOption = "-activemq-port";
        const char *const TopicPrefixOption = "-activemq-topic-prefix";

        struct Init {
            Init() {

                // register log appender
                log4cplus::spi::getAppenderFactoryRegistry().put(std::make_unique<InterDomainMessaging::ActiveMQ::ActiveMQAppenderFactory>());

                // register command line arguments
                SWA::CommandLine::getInstance().registerOption(SWA::NamedOption(BrokerOption, std::string("Broker URL"), true, "broker", true, false));
                SWA::CommandLine::getInstance().registerOption(SWA::NamedOption(UsernameOption, std::string("Broker Username"), false, "username", true, false));
                SWA::CommandLine::getInstance().registerOption(SWA::NamedOption(PasswordOption, std::string("Broker Password"), false, "password", true, false));
                SWA::CommandLine::getInstance().registerOption(SWA::NamedOption(PortNoOption, std::string("Broker Port Number"), false, "port", true, false));
                SWA::CommandLine::getInstance().registerOption(SWA::NamedOption(TopicPrefixOption, std::string("Topic Prefix"), false, "prefix", true, false));

                SWA::Process::getInstance().registerStartedListener(&startup);
            }
        } init;

    } // namespace ActiveMQ

} // namespace InterDomainMessaging
