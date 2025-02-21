cmake_minimum_required(VERSION 3.15)
project(xtuml_transient CXX)

find_package(xtuml_cmake_helpers REQUIRED)
find_package(Boost REQUIRED COMPONENTS headers)
find_package(xtuml_swa REQUIRED)

xtuml_add_library( ${CMAKE_PROJECT_NAME} SHARED
    src/ThreadListener.cc
    src/EventTimers.cc 
)

target_link_libraries( ${CMAKE_PROJECT_NAME}
  PUBLIC
    xtuml_swa::xtuml_swa
    Boost::headers
)