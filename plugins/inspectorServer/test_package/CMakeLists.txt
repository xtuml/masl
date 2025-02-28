cmake_minimum_required(VERSION 3.30)
project(PackageTest CXX)

find_package(xtuml_swa REQUIRED)
find_package(xtuml_metadata REQUIRED)
find_package(xtuml_inspector_server REQUIRED)

add_executable(testlib src/testlib.cc)

add_library(PluginTest_metadata SHARED
        src/example_metadata.cc)

target_link_libraries(PluginTest_metadata PUBLIC
        xtuml_metadata::xtuml_metadata
)

add_library(PluginTest_inspector SHARED
        src/example_inspector.cc)

target_link_libraries(PluginTest_inspector PUBLIC
        xtuml_inspector_server::xtuml_inspector_server
)

target_link_libraries(testlib PRIVATE xtuml_swa::xtuml_swa)
