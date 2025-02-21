cmake_minimum_required(VERSION 3.15)
project(PackageTest CXX)

find_package(xtuml_logging CONFIG REQUIRED)

add_executable(example src/example.cc)
target_link_libraries(example PRIVATE xtuml_logging::xtuml_logging)
