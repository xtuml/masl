cmake_minimum_required(VERSION 3.30)
project(PackageTest CXX)
MESSAGE(STATUS "cmake: Using toolchain file: ${CMAKE_TOOLCHAIN_FILE}")

find_package(xtuml_logging REQUIRED)

add_executable(example src/example.cc)
target_link_libraries(example PRIVATE xtuml_logging::xtuml_logging)
