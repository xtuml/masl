#include "logging/log.hh"
#include <log4cplus/log4cplus.h>
#include <nlohmann/json.hpp>
#include <fmt/format.h>
#include <fmt/chrono.h>

namespace xtuml::logging {

    class JsonLayout : public log4cplus::Layout {
    public:

        JsonLayout() = default;

        JsonLayout(const log4cplus::helpers::Properties &properties) : Layout(properties) {

        }

        JsonLayout(const JsonLayout &) = delete;

        JsonLayout &operator=(const JsonLayout &) = delete;

        ~JsonLayout() override = default;

        virtual void formatAndAppend(log4cplus::tostream &output,
                                     const log4cplus::spi::InternalLoggingEvent &event) override {
            output << nlohmann::json(
                    {
                            {"file",      event.getFile()},
                            {"function",  event.getFunction()},
                            {"level",     llmCache.toString(event.getLogLevel())},
                            {"line",      event.getLine()},
                            {"logger",    event.getLoggerName()},
                            {"message",   event.getMessage()},
                            {"timestamp", fmt::format("{:%FT%TZ}", event.getTimestamp())},
                            {"thread",    event.getThread()},
                    }
            ).dump() << "\n";
        }
    };


    class JsonLayoutFactory : public log4cplus::spi::LayoutFactory {
    public:
        [[nodiscard]] const log4cplus::tstring &getTypeName() const override {
            return name;
        }

        std::unique_ptr<log4cplus::Layout> createObject(const log4cplus::helpers::Properties &props) override {
            return std::make_unique<JsonLayout>(props);
        }

    private:
        std::string name = "xtuml::JsonLayout";
    };



    struct Init {
        Init() {
            // register JSON layout
            log4cplus::spi::getLayoutFactoryRegistry().put(std::make_unique<JsonLayoutFactory>());
        }
    } init;
}