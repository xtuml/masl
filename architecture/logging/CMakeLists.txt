cmake_minimum_required(VERSION 3.15)
project(xtuml_logging CXX)

find_package(xtuml_cmake_helpers REQUIRED)

find_package(fmt REQUIRED)
find_package(log4cplus REQUIRED)
find_package(nlohmann_json REQUIRED)

xtuml_add_library(${CMAKE_PROJECT_NAME} SHARED 
    src/log.cc
)

target_link_libraries( ${CMAKE_PROJECT_NAME}
  PUBLIC
    fmt::fmt
    log4cplus::log4cplus
  PRIVATE
    nlohmann_json::nlohmann_json
)

install(DIRECTORY share/ DESTINATION share/logging)


