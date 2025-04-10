cmake_minimum_required(VERSION 3.30)
project(xtuml_backlog_monitor CXX)

find_package(xtuml_swa REQUIRED)
find_package(asio REQUIRED)


add_library(BacklogMonitor SHARED
        src/BacklogMonitor.cc
)

target_link_libraries(BacklogMonitor PRIVATE xtuml_swa::xtuml_swa asio::asio)

target_compile_options(BacklogMonitor PRIVATE -Wall -Werror)
install(TARGETS BacklogMonitor)
