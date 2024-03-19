#include "LogAppender.hh"

#include <log4cplus/initializer.h>
#include <cppkafka/producer.h>
#include <cppkafka/configuration.h>
#include <nlohmann/json.hpp>
#include <fmt/format.h>
#include <fmt/chrono.h>
#include "swa/CommandLine.hh"
#include "kafka/Kafka.hh"
#include <sstream>

namespace Kafka {
    class KafkaAppender : public log4cplus::Appender {

    public:
        explicit KafkaAppender(const log4cplus::helpers::Properties &props)
                : Appender(props),
                  config({{"metadata.broker.list", props.getProperty("broker", SWA::CommandLine::getInstance().getOption(BrokersOption))}}),
                  producer(config),
                  messageBuilder(props.getProperty("topic", "xtuml.logging")) {
        }

        void close() override {
            producer.flush();
        }

        void append(const log4cplus::spi::InternalLoggingEvent &event) override {
            std::ostringstream output;
            layout->formatAndAppend(output,event);
            std::string message = output.str();
            producer.produce(messageBuilder.payload(message));
        }

        ~KafkaAppender() override {
            destructorImpl();
        }

    private:
        cppkafka::Configuration config;
        cppkafka::Producer producer;
        cppkafka::MessageBuilder messageBuilder;
    };

    log4cplus::SharedAppenderPtr KafkaAppenderFactory::createObject(const log4cplus::helpers::Properties &props) {
        return log4cplus::SharedAppenderPtr(new KafkaAppender(props));
    };

}