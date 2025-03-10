cmake_minimum_required(VERSION 3.15)
project(xtuml_event_collector CXX)

find_package(xtuml_cmake_helpers REQUIRED)
find_package(xtuml_swa REQUIRED)
find_package(xtuml_metadata REQUIRED)
find_package(xtuml_sockets REQUIRED)

xtuml_add_library(Events SHARED
  src/EventEncoder.cc
  src/EventCollector.cc
  src/EventXmlEncoder.cc
  src/EventInspectorWriter.cc
  src/EventConsoleWriter.cc
  src/EventFileWriter.cc
  src/EventContext.cc
  src/EventFinishedContext.cc
  src/ObjectServiceContext.cc
  src/DomainServiceContext.cc
  src/StateServiceContext.cc
  src/ProcessContext.cc
  src/TerminatorServiceContext.cc
)

target_link_libraries(Events PUBLIC
        xtuml_metadata::xtuml_metadata
        xtuml_swa::xtuml_swa
        xtuml_sockets::xtuml_sockets
)
