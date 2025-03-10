cmake_minimum_required(VERSION 3.30)
project(xtuml_metadata CXX)

find_package(xtuml_swa REQUIRED)

add_library ( MetaData SHARED
            src/MetaData.cc
)

target_link_libraries( MetaData 
    PUBLIC
        xtuml_swa::xtuml_swa
)

target_sources(MetaData
        PUBLIC FILE_SET HEADERS BASE_DIRS include FILES
        include/metadata)
target_compile_options(MetaData PRIVATE -Wall -Werror)
install(TARGETS MetaData FILE_SET HEADERS)
