cmake_minimum_required(VERSION 3.15)
project(xtuml_sql CXX)

find_package(xtuml_cmake_helpers REQUIRED)
find_package(xtuml_swa REQUIRED)

find_package(Boost REQUIRED COMPONENTS headers)

xtuml_add_library(${CMAKE_PROJECT_NAME} SHARED
  src/CacheStrategy.cc
  src/ResourceMonitor.cc
  src/Criteria.cc
  src/Schema.cc
  src/AssignerStateMapper.cc
  src/WriteOnChangeEnabler.cc
  src/DatabaseUnitOfWork.cc
  src/DatabaseFactory.cc
  src/AssignerStateFactory.cc
  src/CriteriaFactory.cc
  src/TimerMapper.cc
  src/TimerMapperSqlFactory.cc
  src/TimerMapperUnitOfWork.cc
  src/ObjectSqlRepository.cc
  src/RelationshipSqlRepository.cc
)

target_link_libraries( ${CMAKE_PROJECT_NAME}
  PUBLIC
    xtuml_swa::xtuml_swa
    Boost::headers
)
