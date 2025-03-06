cmake_minimum_required(VERSION 3.30)
project(xtuml_kafka CXX)

find_package(xtuml_swa REQUIRED)

find_package(CppKafka REQUIRED)
find_package(libuuid REQUIRED)
find_package(log4cplus REQUIRED)
find_package(fmt REQUIRED)


add_library(xtuml_kafka SHARED
        src/Consumer.cc
        src/DataConsumer.cc
        src/Kafka.cc
        src/ProcessHandler.cc
        src/Producer.cc
        src/ServiceHandler.cc
        src/LogAppender.cc
)

target_link_libraries(xtuml_kafka
        PUBLIC
        xtuml_swa::xtuml_swa
        CppKafka::cppkafka
        libuuid::libuuid
        fmt::fmt
        log4cplus::log4cplus
)

target_sources(xtuml_kafka
        PUBLIC FILE_SET HEADERS BASE_DIRS include FILES
        include/kafka)
target_compile_options(xtuml_kafka PRIVATE -Wall -Werror)
install(TARGETS xtuml_kafka FILE_SET HEADERS)
install(DIRECTORY share/ DESTINATION share/kafka)

