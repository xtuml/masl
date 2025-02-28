#include "logging/log.hh"

#include <fmt/chrono.h>
#include <fmt/format.h>

#include <log4cplus/configurator.h>
#include <log4cplus/initializer.h>
#include <log4cplus/log4cplus.h>

#include <nlohmann/json.hpp>

#include <fstream>
#include <optional>

namespace xtuml::logging {

    bool Logger::load_config(const std::string &filename, std::chrono::milliseconds interval) {
        static Logger log{"xtuml.logging.config"};
        static std::optional<log4cplus::ConfigureAndWatchThread> config_watcher{};

        if (std::fstream{filename}) {
            if (interval > std::chrono::seconds{0}) {
                log.debug("Watching log config file: {}", filename);
                config_watcher.emplace(filename, interval.count());
            } else {
                config_watcher.reset();
                log.debug("Loading log config file: {}", filename);
                log4cplus::PropertyConfigurator::doConfigure(filename);
            }
            return true;
        } else {
            config_watcher.reset();
            log.error("Error opening log config: {}", filename);
            return false;
        }
    }

    class JsonLayout : public log4cplus::Layout {
      public:
        JsonLayout() = default;

        JsonLayout(const log4cplus::helpers::Properties &properties)
            : Layout(properties) {}

        JsonLayout(const JsonLayout &) = delete;

        JsonLayout &operator=(const JsonLayout &) = delete;

        ~JsonLayout() override = default;

        virtual void
        formatAndAppend(log4cplus::tostream &output, const log4cplus::spi::InternalLoggingEvent &event) override {
            output << nlohmann::json({
                                         {"file", event.getFile()},
                                         {"function", event.getFunction()},
                                         {"level", llmCache.toString(event.getLogLevel())},
                                         {"line", event.getLine()},
                                         {"logger", event.getLoggerName()},
                                         {"message", event.getMessage()},
                                         {"timestamp", fmt::format("{:%FT%TZ}", event.getTimestamp())},
                                         {"thread", event.getThread()},
                                     })
                          .dump()
                   << "\n";
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
            log4cplus::BasicConfigurator::doConfigure();
            log4cplus::Logger::getRoot().setLogLevel(log4cplus::INFO_LOG_LEVEL);
            for (const auto &app : log4cplus::Logger::getRoot().getAllAppenders()) {
                app->setLayout(std::make_unique<log4cplus::PatternLayout>("%-5p %c - %m%n"));
            }

            // register JSON layout
            log4cplus::spi::getLayoutFactoryRegistry().put(std::make_unique<JsonLayoutFactory>());
        }
        static inline log4cplus::Initializer initializer{};
    } init;
} // namespace xtuml::logging