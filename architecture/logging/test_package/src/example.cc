#include <fstream>
#include <logging/log.hh>
#include <map>
#include <thread>

using namespace xtuml::logging;
using namespace std::literals;

int main(int argc, char **argv) {

    if (argc > 1) {
        Logger::load_config(argv[1], 10s);
    }

    Logger log("xtuml.logging.example");
    log.set_level(Logger::Level::INFO);

    std::string name = "World";
    log.info("Hello {}", name);

    log.trace("Trace");
    log.debug("Debug");
    log.info("Hello");

    std::string dynamic_message = "Runtime Hello {}";
    log.info(fmt::runtime(dynamic_message), name);

    if (log.info_enabled()) {
        log.info("Hello");
    }

    log.set_level(Logger::Level::TRACE);

    log.trace("Trace");
    log.debug("Debug");
    log.info("Hello");

    Logger log2("xtuml.logging.example2");
    log2.info("Example 2");
}
