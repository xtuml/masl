cmake_minimum_required(VERSION 3.30)
project(PackageTest CXX)

find_package(xtuml_swa REQUIRED)
find_package(xtuml_metadata REQUIRED)

add_executable(testlib src/testlib.cc)

add_library(PluginTest_metadata SHARED
  src/example_metadata.cc )

target_link_libraries(PluginTest_metadata PUBLIC
    xtuml_metadata::xtuml_metadata
)

target_link_libraries(testlib PRIVATE xtuml_swa::xtuml_swa)
