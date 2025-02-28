cmake_minimum_required(VERSION 3.30)
project(xtuml_trace CXX)

find_package(xtuml_swa REQUIRED)
find_package(xtuml_metadata REQUIRED)


add_library(Trace SHARED
        src/Trace.cc
)

target_link_libraries(Trace PRIVATE xtuml_swa::xtuml_swa xtuml_metadata::xtuml_metadata)

target_compile_options(Trace PRIVATE -Wall -Werror)
install(TARGETS Trace)
