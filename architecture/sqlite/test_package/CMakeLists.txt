cmake_minimum_required(VERSION 3.15)
project(PackageTest CXX)

find_package(xtuml_sqlite CONFIG REQUIRED)

add_executable(testlib src/testlib.cc)

target_link_libraries(testlib PRIVATE xtuml_sqlite::xtuml_sqlite)
