cmake_minimum_required(VERSION 3.15)
project(PackageTest CXX)

find_package(xtuml_sql CONFIG REQUIRED)

add_executable(testlib src/testlib.cc)

target_link_libraries(testlib PRIVATE xtuml_sql::xtuml_sql)
