#
# UK Crown Copyright (c) 2016. All Rights Reserved
#
find_package(Sqlite3 REQUIRED)

simple_add_shared_library(
  NAME      sqlite
  SOURCES   
            Database.cc
            Resultset.cc
            SqlMonitor.cc
            Transaction.cc
            PreparedStatement.cc
            AssignerStateMapper.cc
            EventParameterCodecs.cc
            SqliteStatementFormatter.cc
            SqliteCriteria.cc
            SqliteTimerMapperSql.cc
  LINKS     
            sql 
            Sqlite3::Sqlite3
  EXPORT    MaslCore
  INCLUDES  
            sqlite/BlobData.hh
            sqlite/Database.hh
            sqlite/EventParameterCodecs.hh
            sqlite/Exception.hh
            sqlite/PreparedStatement.hh
            sqlite/Resultset.hh
            sqlite/SqlMonitor.hh
            sqlite/SqliteTimerMapperSql.hh
  )


simple_add_executable (
  NAME    sqlite3_backup
  SOURCES sqlite3_backup.cc
  LINKS   Sqlite3::Sqlite3
  INSTALL
  )
