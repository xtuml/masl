cmake_minimum_required(VERSION 3.30)
project(PackageTest CXX)

find_package(xtuml_kafka REQUIRED)

add_executable(testlib src/testlib.cc)

target_link_libraries(testlib PRIVATE xtuml_kafka::xtuml_kafka)
