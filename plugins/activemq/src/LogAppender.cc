#include "LogAppender.hh"

#include "idm/ProcessHandler.hh"
#include "idm/Producer.hh"

#include <fmt/chrono.h>
#include <fmt/format.h>
#include <log4cplus/initializer.h>
#include <sstream>

namespace InterDomainMessaging {

    namespace ActiveMQ {
        class ActiveMQAppender : public log4cplus::Appender {

          public:
            explicit ActiveMQAppender(const log4cplus::helpers::Properties &props)
                : Appender(props), producer(InterDomainMessaging::ProcessHandler::getInstance().createProducer(props.getProperty("topic", "xtuml.logging"))) {}

            void close() override {}

            void append(const log4cplus::spi::InternalLoggingEvent &event) override {
                std::ostringstream output;
                layout->formatAndAppend(output, event);
                std::string message = output.str();
                producer->produce(message);
            }

            ~ActiveMQAppender() override {
                destructorImpl();
            }

          private:
            std::unique_ptr<InterDomainMessaging::Producer> producer;
        };

        log4cplus::SharedAppenderPtr ActiveMQAppenderFactory::createObject(const log4cplus::helpers::Properties &props) {
            return log4cplus::SharedAppenderPtr(new ActiveMQAppender(props));
        };

    } // namespace ActiveMQ

} // namespace InterDomainMessaging
