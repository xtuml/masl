cmake_minimum_required(VERSION 3.15)
project(xtuml_trace CXX)

find_package(xtuml_cmake_helpers REQUIRED)
find_package(xtuml_swa REQUIRED)
find_package(xtuml_metadata REQUIRED)


xtuml_add_library(Trace SHARED
        src/Trace.cc
)

target_link_libraries(Trace PRIVATE xtuml_swa::xtuml_swa xtuml_metadata::xtuml_metadata)

  