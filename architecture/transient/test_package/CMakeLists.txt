cmake_minimum_required(VERSION 3.30)
project(PackageTest CXX)

find_package(xtuml_transient REQUIRED)

add_executable(testlib src/testlib.cc)

target_link_libraries(testlib PRIVATE xtuml_transient::xtuml_transient)
