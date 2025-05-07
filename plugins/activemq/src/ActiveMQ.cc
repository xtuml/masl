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

        const char *const SslCertOption = "-ssl-cert";
        const char *const SslKeyOption = "-ssl-key";
        const char *const SslCertAuthOption = "-ssl-ca";
        const char *const SslPasswordOption = "-ssl-password";

        bool init() {
            // register log appender
            log4cplus::spi::getAppenderFactoryRegistry().put(std::make_unique<InterDomainMessaging::ActiveMQ::ActiveMQAppenderFactory>());

            // register command line arguments
            SWA::CommandLine::getInstance().registerOption(SWA::NamedOption(BrokerOption, "Broker URL", true, "broker", true, false));
            SWA::CommandLine::getInstance().registerOption(SWA::NamedOption(UsernameOption, "Broker Username", false, "username", true, false));
            SWA::CommandLine::getInstance().registerOption(SWA::NamedOption(PasswordOption, "Broker Password", false, "password", true, false));
            SWA::CommandLine::getInstance().registerOption(SWA::NamedOption(PortNoOption, "Broker Port Number", false, "port", true, false));
            SWA::CommandLine::getInstance().registerOption(SWA::NamedOption(TopicPrefixOption, "Topic Prefix", false, "prefix", true, false));
            SWA::CommandLine::getInstance().registerOption(SWA::NamedOption(SslCertOption, "SSL Certificate (pem)", false, "cert_file", true, false));
            SWA::CommandLine::getInstance().registerOption(SWA::NamedOption(SslKeyOption, "SSL Private Key (pem)", false, "key_file", true, false));
            SWA::CommandLine::getInstance().registerOption(SWA::NamedOption(SslCertAuthOption, "SSL CA (pem)", false, "ca_file", true, false));
            SWA::CommandLine::getInstance().registerOption(SWA::NamedOption(SslPasswordOption, "SSL password", false, "ssl_password", true, false));

            return true;
        }

        bool initialised = init();

    } // namespace ActiveMQ

} // namespace InterDomainMessaging
