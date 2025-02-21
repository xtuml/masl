cmake_minimum_required(VERSION 3.15)
project(xtuml_kafka CXX)

find_package(xtuml_cmake_helpers REQUIRED)
find_package(xtuml_swa REQUIRED)

find_package(CppKafka REQUIRED)
find_package(libuuid REQUIRED)
find_package(log4cplus REQUIRED)
find_package(fmt REQUIRED)


xtuml_add_library(${CMAKE_PROJECT_NAME} SHARED
  src/Consumer.cc
  src/DataConsumer.cc
  src/Kafka.cc
  src/ProcessHandler.cc
  src/Producer.cc
  src/ServiceHandler.cc
  src/LogAppender.cc
)

target_link_libraries( ${CMAKE_PROJECT_NAME}
  PUBLIC
    xtuml_swa::xtuml_swa
    CppKafka::cppkafka
    libuuid::libuuid
    fmt::fmt
    log4cplus::log4cplus
)

install(DIRECTORY share/ DESTINATION share/kafka)

