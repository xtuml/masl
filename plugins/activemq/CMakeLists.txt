cmake_minimum_required(VERSION 3.30)
project(xtuml_activemq CXX)

find_package(xtuml_swa REQUIRED)
find_package(xtuml_amqp_client REQUIRED)
find_package(xtuml_idm REQUIRED)

find_package(log4cplus REQUIRED)
find_package(fmt REQUIRED)
find_package(libuuid REQUIRED)


add_library(ActiveMQ SHARED
        src/ActiveMQ.cc
        src/Consumer.cc
        src/LogAppender.cc
        src/ProcessHandler.cc
        src/Producer.cc
)

target_link_libraries(ActiveMQ
        PUBLIC
        xtuml_swa::xtuml_swa
        xtuml_idm::xtuml_idm
        xtuml_amqp_client::xtuml_amqp_client
        fmt::fmt
        log4cplus::log4cplus
        libuuid::libuuid
)

target_sources(ActiveMQ
        PUBLIC FILE_SET HEADERS BASE_DIRS include FILES
        include/activemq)
target_compile_options(ActiveMQ PRIVATE -Wall -Werror)
install(TARGETS ActiveMQ FILE_SET HEADERS)
install(DIRECTORY share/ DESTINATION share/activemq)

