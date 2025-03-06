cmake_minimum_required(VERSION 3.30)
project(xtuml_activemq CXX)

find_package(xtuml_swa REQUIRED)
find_package(xtuml_amqp_client REQUIRED)

find_package(libuuid REQUIRED)
find_package(log4cplus REQUIRED)
find_package(fmt REQUIRED)


add_library(xtuml_activemq SHARED
        src/Consumer.cc
        src/DataConsumer.cc
        src/ActiveMQ.cc
        src/ProcessHandler.cc
        src/Producer.cc
        src/ServiceHandler.cc
)

target_link_libraries(xtuml_activemq
        PUBLIC
        xtuml_swa::xtuml_swa
        xtuml_amqp_client::xtuml_amqp_client
        libuuid::libuuid
        fmt::fmt
        log4cplus::log4cplus
)

target_sources(xtuml_activemq
        PUBLIC FILE_SET HEADERS BASE_DIRS include FILES
        include/activemq)
target_compile_options(xtuml_activemq PRIVATE -Wall -Werror)
install(TARGETS xtuml_activemq FILE_SET HEADERS)
install(DIRECTORY share/ DESTINATION share/activemq)

