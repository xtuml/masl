cmake_minimum_required(VERSION 3.30)
project(xtuml_swa CXX)

find_package(Boost REQUIRED COMPONENTS headers)
find_package(xtuml_logging REQUIRED)
find_package(nlohmann_json REQUIRED)
find_package(asio REQUIRED)

add_library(xtuml_swa SHARED
        src/CommandLine.cc
        src/Domain.cc
        src/Duration.cc
        src/Event.cc
        src/EventQueue.cc
        src/EventTimer.cc
        src/EventTimers.cc
        src/Exception.cc
        src/Logging.cc
        src/Main.cc
        src/NameFormatter.cc
        src/PluginRegistry.cc
        src/Process.cc
        src/ProcessMonitor.cc
        src/Schedule.cc
        src/Stack.cc
        src/StackFrame.cc
        src/Timestamp.cc
        src/console.cc
        src/parse.cc
)


target_link_libraries(xtuml_swa
        PUBLIC
        nlohmann_json::nlohmann_json
        Boost::headers
        asio::asio
        PRIVATE
        xtuml_logging::xtuml_logging
        -ldl
)

target_sources(xtuml_swa
        PUBLIC FILE_SET HEADERS BASE_DIRS include FILES
        include/swa)

target_compile_options(xtuml_swa PRIVATE -Wall -Werror)

install(TARGETS xtuml_swa FILE_SET HEADERS)
