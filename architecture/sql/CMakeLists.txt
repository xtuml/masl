cmake_minimum_required(VERSION 3.30)
project(xtuml_sql CXX)

find_package(xtuml_swa REQUIRED)

find_package(Boost REQUIRED COMPONENTS headers)

add_library(xtuml_sql SHARED
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

target_link_libraries(xtuml_sql
        PUBLIC
        xtuml_swa::xtuml_swa
        Boost::headers
)

target_sources(xtuml_sql
        PUBLIC FILE_SET HEADERS BASE_DIRS include FILES include/sql )
target_compile_options(xtuml_sql PRIVATE -Wall -Werror)
install(TARGETS xtuml_sql FILE_SET HEADERS)
