cmake_minimum_required(VERSION 3.15)
project(xtuml_sqlite CXX)

find_package(xtuml_cmake_helpers REQUIRED)
find_package(xtuml_sql REQUIRED)

find_package(SQLite3 REQUIRED)


xtuml_add_library(${CMAKE_PROJECT_NAME} SHARED
  src/Database.cc
  src/Resultset.cc
  src/SqlMonitor.cc
  src/Transaction.cc
  src/PreparedStatement.cc
  src/AssignerStateMapper.cc
  src/EventParameterCodecs.cc
  src/SqliteStatementFormatter.cc
  src/SqliteCriteria.cc
  src/SqliteTimerMapperSql.cc
)

target_link_libraries( ${CMAKE_PROJECT_NAME}
  PUBLIC
    xtuml_sql::xtuml_sql
    SQLite::SQLite3
)



xtuml_add_executable ( sqlite3_backup src/sqlite3_backup.cc )

target_link_libraries( sqlite3_backup
  PRIVATE
    SQLite::SQLite3
)
