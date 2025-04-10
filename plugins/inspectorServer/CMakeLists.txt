cmake_minimum_required(VERSION 3.30)
project(xtuml_inspector_server CXX)

find_package(xtuml_swa REQUIRED)
find_package(xtuml_metadata REQUIRED)
find_package(asio REQUIRED)


add_library(Inspector SHARED
        src/Inspector.cc
        src/BufferedIO.cc
        src/ConsoleRedirection.cc
        src/CommunicationChannel.cc
        src/DomainHandler.cc
        src/GenericObjectHandler.cc
        src/TerminatorHandler.cc
        src/ProcessHandler.cc
        src/MetaData.cc
)


target_link_libraries(Inspector
        PUBLIC
        xtuml_swa::xtuml_swa
        xtuml_metadata::xtuml_metadata
        asio::asio
)

target_sources(Inspector
        PUBLIC FILE_SET HEADERS BASE_DIRS include FILES
        include/inspector)

target_compile_options(Inspector PRIVATE -Wall -Werror)
install(TARGETS Inspector FILE_SET HEADERS)
