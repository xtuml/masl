#ifndef Kafka_LogAppender_HH
#define Kafka_LogAppender_HH

#include <log4cplus/log4cplus.h>
#include <string>

namespace Kafka {

    class KafkaAppenderFactory : public log4cplus::spi::AppenderFactory {
    public:
        [[nodiscard]] const log4cplus::tstring &getTypeName() const override {
            return name;
        }

        log4cplus::SharedAppenderPtr createObject(const log4cplus::helpers::Properties &props) override;

    private:
        std::string name = "xtuml::KafkaAppender";
    };


}

#endif
