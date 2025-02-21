cmake_minimum_required(VERSION 3.15)
project(xtuml_backlog_monitor CXX)

find_package(xtuml_cmake_helpers REQUIRED)
find_package(xtuml_swa REQUIRED)


xtuml_add_library(BacklogMonitor SHARED
        src/BacklogMonitor.cc
)

target_link_libraries(BacklogMonitor PRIVATE xtuml_swa::xtuml_swa )

