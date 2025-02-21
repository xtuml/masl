cmake_minimum_required(VERSION 3.15)
project(xtuml_thread_timer CXX)

find_package(xtuml_cmake_helpers REQUIRED)
find_package(xtuml_swa REQUIRED)
find_package(xtuml_metadata REQUIRED)


xtuml_add_library(ThreadTimer SHARED
        src/ThreadTimer.cc
)

target_link_libraries(ThreadTimer PRIVATE xtuml_swa::xtuml_swa xtuml_metadata::xtuml_metadata)

