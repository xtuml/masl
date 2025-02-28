cmake_minimum_required(VERSION 3.30)
project(xtuml_thread_timer CXX)

find_package(xtuml_swa REQUIRED)
find_package(xtuml_metadata REQUIRED)


add_library(ThreadTimer SHARED
        src/ThreadTimer.cc
)

target_link_libraries(ThreadTimer PRIVATE xtuml_swa::xtuml_swa xtuml_metadata::xtuml_metadata)

target_compile_options(ThreadTimer PRIVATE -Wall -Werror)
install(TARGETS ThreadTimer)
