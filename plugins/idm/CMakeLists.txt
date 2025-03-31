cmake_minimum_required(VERSION 3.30)
project(xtuml_idm CXX)

find_package(xtuml_swa REQUIRED)
find_package(asio REQUIRED)

add_library(xtuml_idm SHARED
        src/ProcessHandler.cc
)

target_link_libraries(xtuml_idm
        PUBLIC
        xtuml_swa::xtuml_swa
        asio::asio
)

target_sources(xtuml_idm
        PUBLIC FILE_SET HEADERS BASE_DIRS include FILES
        include/idm)
target_compile_options(xtuml_idm PRIVATE -Wall -Werror)
install(TARGETS xtuml_idm FILE_SET HEADERS)

