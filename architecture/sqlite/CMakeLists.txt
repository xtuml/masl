cmake_minimum_required(VERSION 3.30)
project(xtuml_sqlite CXX)

find_package(xtuml_sql REQUIRED)

find_package(SQLite3 REQUIRED)


add_library(xtuml_sqlite SHARED
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

target_link_libraries(xtuml_sqlite
        PUBLIC
        xtuml_sql::xtuml_sql
        SQLite::SQLite3
)

target_sources(xtuml_sqlite
        PUBLIC FILE_SET HEADERS BASE_DIRS include FILES
        include/sqlite)
target_compile_options(xtuml_sqlite PRIVATE -Wall -Werror)
install(TARGETS xtuml_sqlite FILE_SET HEADERS)


add_executable(sqlite3_backup src/sqlite3_backup.cc)

target_link_libraries(sqlite3_backup PRIVATE SQLite::SQLite3)
install(TARGETS sqlite3_backup)
