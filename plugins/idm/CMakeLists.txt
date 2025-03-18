cmake_minimum_required(VERSION 3.30)
project(xtuml_idm CXX)

find_package(xtuml_swa REQUIRED)

add_library(xtuml_idm INTERFACE)

target_sources(xtuml_idm
        PUBLIC FILE_SET HEADERS BASE_DIRS include FILES
        include/idm)
install(TARGETS xtuml_idm FILE_SET HEADERS)

