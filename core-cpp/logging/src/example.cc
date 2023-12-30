#include "logging/log.hh"
#include <map>
#include <fstream>
#include <thread>

using namespace xtuml::logging;
using namespace std::literals;

int main ( int argc, char** argv )
{

    if( argc > 1 ) {
        Logger::load_config(argv[1],10s);
    }

    Logger log("xtuml.logging.example");
    Logger log2("xtuml.logging.example2");

    std::string name = "World";

    log.debug("Hello");
    log.info("Hello {}", name);

    std::string dynamic_message = "Runtime Hello {}";
    log.info(fmt::runtime(dynamic_message), name);

    if ( log.info_enabled() ) {
        log.info("Hello");
   }

    log.set_level(Logger::Level::TRACE);

    for ( auto i = 0; i < 100; ++i) {
        std::this_thread::sleep_for(5s);
        log.trace("Trace");
        log.info("Hello");
    }

    log2.info("Example 2");
}
