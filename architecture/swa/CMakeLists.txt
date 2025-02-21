cmake_minimum_required(VERSION 3.15)
project(xtuml_swa CXX)

find_package(xtuml_cmake_helpers REQUIRED)
find_package(Boost REQUIRED COMPONENTS headers)
find_package(xtuml_logging REQUIRED)
find_package(nlohmann_json REQUIRED)

xtuml_add_library( ${CMAKE_PROJECT_NAME} SHARED
    src/ActivityMonitor.cc
    src/CommandLine.cc
    src/Domain.cc
    src/Duration.cc
    src/Event.cc
    src/EventQueue.cc
    src/EventTimer.cc
    src/EventTimers.cc
    src/Exception.cc
    src/FileDescriptorListener.cc
    src/ListenerPriority.cc
    src/Logging.cc
    src/Main.cc
    src/NameFormatter.cc
    src/PluginRegistry.cc
    src/Process.cc
    src/ProcessMonitor.cc
    src/RealTimeSignalListener.cc
    src/Schedule.cc
    src/Stack.cc
    src/StackFrame.cc
    src/TimerListener.cc
    src/Timestamp.cc
    src/console.cc
    src/parse.cc
)

target_link_libraries( ${CMAKE_PROJECT_NAME}
  PUBLIC
    nlohmann_json::nlohmann_json
    Boost::headers
  PRIVATE
    xtuml_logging::xtuml_logging
    -lrt
    -ldl
)
