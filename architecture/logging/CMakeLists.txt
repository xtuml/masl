cmake_minimum_required(VERSION 3.30)
project(xtuml_logging CXX)
MESSAGE(STATUS "cmake: Using toolchain file: ${CMAKE_TOOLCHAIN_FILE}")

find_package(fmt REQUIRED)
find_package(log4cplus REQUIRED)
find_package(nlohmann_json REQUIRED)

add_library(xtuml_logging SHARED
        src/log.cc
)

target_sources(xtuml_logging
        PUBLIC FILE_SET HEADERS BASE_DIRS include FILES
        include/logging
)

target_link_libraries(xtuml_logging
        PUBLIC
        fmt::fmt
        log4cplus::log4cplus
        PRIVATE
        nlohmann_json::nlohmann_json
)

target_compile_options(xtuml_logging PRIVATE -Wall -Werror)

install(TARGETS xtuml_logging FILE_SET HEADERS)
install(DIRECTORY share/ DESTINATION share/logging)


