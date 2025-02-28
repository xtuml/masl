cmake_minimum_required(VERSION 3.30)
project(xtuml_transient CXX)

find_package(Boost REQUIRED COMPONENTS headers)
find_package(xtuml_swa REQUIRED)

add_library(xtuml_transient SHARED
        src/ThreadListener.cc
        src/EventTimers.cc
)

target_link_libraries(xtuml_transient
        PUBLIC
        xtuml_swa::xtuml_swa
        Boost::headers
)


target_sources(xtuml_transient
        PUBLIC FILE_SET HEADERS BASE_DIRS include FILES
        include/transient)
target_compile_options(xtuml_transient PRIVATE -Wall -Werror)
install(TARGETS xtuml_transient FILE_SET HEADERS)
