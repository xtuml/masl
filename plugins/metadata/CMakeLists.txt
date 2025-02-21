cmake_minimum_required(VERSION 3.15)
project(xtuml_metadata CXX)

find_package(xtuml_cmake_helpers REQUIRED)
find_package(xtuml_swa REQUIRED)

xtuml_add_library ( MetaData SHARED
            src/MetaData.cc
)

target_link_libraries( MetaData 
    PUBLIC
        xtuml_swa::xtuml_swa
)
