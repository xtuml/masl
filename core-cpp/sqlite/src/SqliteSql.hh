//============================================================================//
// UK Crown Copyright (c) 2007. All rights reserved.
//
// File:   SqliteSql.cc
//
//============================================================================//
#ifndef Sqlite_Sql_HH
#define Sqlite_Sql_HH

namespace SQLITE {

  namespace {
     const std::string SPACE              = " ";
     const std::string SQL_TERMINATOR     = ";";
     const std::string NEW_LINE           = "\n";
     const std::string ALL_COLUMNS        = "*";

     const std::string SELECT             = "SELECT";
     const std::string DELETE             = "DELETE";
     const std::string WHERE              = "WHERE";
     const std::string FROM               = "FROM";
     const std::string DROP_TABLE         = "DROP TABLE";
     const std::string CREATE_DATABASE    = "CREATE DATABASE";
     const std::string DROP_DATABASE      = "DROP DATABASE";

     const std::string START_TRANSACTION  = "BEGIN    TRANSACTION";
     const std::string COMMIT_TRANSACTION = "COMMIT   TRANSACTION";
     const std::string ABORT_TRANSACTION  = "ROLLBACK TRANSACTION";
  }

}
#endif
