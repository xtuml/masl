#pragma once

#include <fmt/format.h>
#include <fmt/xchar.h>
#include <log4cplus/logger.h>

#include <chrono>
#include <source_location>
#include <string>

namespace xtuml::logging {
    /**
     * A Wrapper around log4cplus and fmt to allow formatted logging without nasty
     * macros.
     */
    class Logger {
      public:
        /**
         * A wrapper around an fmt::basic_format_string that captures the current
         * source location. This enables the log functions to receive their format
         * arguments in a variadic template. Without this, we would have to add the
         * location as a default final parameter on each log function, removing the
         * ability to use variadic arguments.
         */
        template <typename Char, typename... Args>
        class basic_format_string_with_source_location : public fmt::basic_format_string<Char, Args...> {
          public:
            template <std::convertible_to<fmt::basic_string_view<Char>> S>

            explicit(false) consteval basic_format_string_with_source_location(
                const S &s, std::source_location l = std::source_location::current()
            )
                : fmt::basic_format_string<Char, Args...>(s), location_(l) {}

            template <typename S>
                requires(!std::convertible_to<S, fmt::basic_string_view<Char>> ||
                         std::same_as<S, basic_format_string_with_source_location>)

            explicit(false
            ) basic_format_string_with_source_location(S &&s, std::source_location l = std::source_location::current())
                : fmt::basic_format_string<Char, Args...>(std::forward<S>(s)), location_{l} {}

            const auto &location() {
                return location_;
            }

          private:
            std::source_location location_;
        };

        template <typename... Args>
        using format_string_with_source_location =
            basic_format_string_with_source_location<char, std::type_identity_t<Args>...>;

        /**
         * Loads the log4cplus configuration from a config file, and then watches
         * the file for changes at the specified interval. A zero interval will stop
         * watching.
         * @param filename
         * @param interval
         */
        static bool
        load_config(const std::string &filename, std::chrono::milliseconds interval = std::chrono::seconds(60));

        enum class Level { TRACE, DEBUG, INFO, WARN, ERROR, FATAL };

        static Logger root() {
            return Logger{""};
        }

        template <typename... Args>
        explicit Logger(fmt::format_string<Args...> name, Args &&...args)
            : impl_(log4cplus::Logger::getInstance(fmt::format(name, std::forward<Args>(args)...))) {}

        template <typename... Args>
        void log(Level level, format_string_with_source_location<Args...> message, const Args &...args) const {
            log_impl(level, message.location(), message, fmt::make_format_args(args...));
        }

        template <typename... Args>
        void trace(format_string_with_source_location<Args...> message, const Args &...args) const {
            log_impl(Level::TRACE, message.location(), message, fmt::make_format_args(args...));
        }

        template <typename... Args>
        void debug(format_string_with_source_location<Args...> message, const Args &...args) const {
            log_impl(Level::DEBUG, message.location(), message, fmt::make_format_args(args...));
        }

        template <typename... Args>
        void info(format_string_with_source_location<Args...> message, const Args &...args) const {
            log_impl(Level::INFO, message.location(), message, fmt::make_format_args(args...));
        }

        template <typename... Args>
        void warn(format_string_with_source_location<Args...> message, const Args &...args) const {
            log_impl(Level::WARN, message.location(), message, fmt::make_format_args(args...));
        }

        template <typename... Args>
        void error(format_string_with_source_location<Args...> message, const Args &...args) const {
            log_impl(Level::ERROR, message.location(), message, fmt::make_format_args(args...));
        }

        template <typename... Args>
        void fatal(format_string_with_source_location<Args...> message, const Args &...args) const {
            log_impl(Level::FATAL, message.location(), message, fmt::make_format_args(args...));
        }

        void log_raw(
            Level level,
            const std::string &message,
            const std::string &file_name,
            int line,
            const std::string &function_name
        ) {
            impl_.log(convert_level(level), message, file_name.c_str(), line, function_name.c_str());
        }

        bool enabled(Level level) const {
            return impl_.isEnabledFor(convert_level(level));
        }

        bool trace_enabled() const {
            return enabled(Level::TRACE);
        }

        bool debug_enabled() const {
            return enabled(Level::DEBUG);
        }

        bool info_enabled() const {
            return enabled(Level::INFO);
        }

        bool warn_enabled() const {
            return enabled(Level::WARN);
        }

        bool error_enabled() const {
            return enabled(Level::ERROR);
        }

        bool fatal_enabled() const {
            return enabled(Level::FATAL);
        }

        void set_level(Level level) {
            impl_.setLogLevel(convert_level(level));
        }

      private:
        static constexpr log4cplus::LogLevel convert_level(Level level) {
            constexpr std::array<log4cplus::LogLevel, 6> lookup = {
                log4cplus::TRACE_LOG_LEVEL,
                log4cplus::DEBUG_LOG_LEVEL,
                log4cplus::INFO_LOG_LEVEL,
                log4cplus::WARN_LOG_LEVEL,
                log4cplus::ERROR_LOG_LEVEL,
                log4cplus::FATAL_LOG_LEVEL,
            };
            return lookup[static_cast<std::size_t>(level)];
        }

        void log_impl(
            Level level, const std::source_location &location, fmt::string_view message, fmt::format_args args
        ) const {
            if (enabled(level)) {
                impl_.log(
                    convert_level(level),
                    fmt::vformat(message, args),
                    location.file_name(),
                    static_cast<int>(location.line()),
                    location.function_name()
                );
            }
        };

        log4cplus::Logger impl_;
    };

} // namespace xtuml::logging
