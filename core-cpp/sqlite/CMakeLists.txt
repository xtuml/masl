# ----------------------------------------------------------------------------
# (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
# The copyright of this Software is vested in the Crown
# and the Software is the property of the Crown.
# ----------------------------------------------------------------------------
# Classification: UK OFFICIAL
# ----------------------------------------------------------------------------

find_package(SQLite3 CONFIG QUIET REQUIRED)
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
            SQLite::SQLite3
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
  LINKS   SQLite::SQLite3
  INSTALL
  )
