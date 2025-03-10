cmake_minimum_required(VERSION 3.30)
project(xtuml_kafka CXX)

find_package(xtuml_swa REQUIRED)

find_package(CppKafka REQUIRED)
find_package(libuuid REQUIRED)
find_package(log4cplus REQUIRED)
find_package(fmt REQUIRED)


add_library(Kafka SHARED
        src/Consumer.cc
        src/DataConsumer.cc
        src/Kafka.cc
        src/ProcessHandler.cc
        src/Producer.cc
        src/ServiceHandler.cc
        src/LogAppender.cc
)

target_link_libraries(Kafka
        PUBLIC
        xtuml_swa::xtuml_swa
        CppKafka::cppkafka
        libuuid::libuuid
        fmt::fmt
        log4cplus::log4cplus
)

target_sources(Kafka
        PUBLIC FILE_SET HEADERS BASE_DIRS include FILES
        include/kafka)
target_compile_options(Kafka PRIVATE -Wall -Werror)
install(TARGETS Kafka FILE_SET HEADERS)
install(DIRECTORY share/ DESTINATION share/kafka)

