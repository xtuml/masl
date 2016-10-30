//============================================================================//
// UK Crown Copyright (c) 2007. All rights reserved.
//
// File:   SqlMonitor.cc
//
//============================================================================//
#include <iostream>
#include "sqlite/SqlMonitor.hh"
#include "sqlite/Resultset.hh"

#include "swa/Process.hh"
#include "swa/CommandLine.hh"

namespace {

// *****************************************************************
// *****************************************************************
const char * const commandLineArg   = "-sql";
const char * const commandLineUsage = "Monitor the database SQL operations";
const char * const commandLineDesc  = "monitor SQL";

// *****************************************************************
// *****************************************************************
bool configureCommandLine()
{
  SWA::Process::getInstance().getCommandLine().registerOption (SWA::NamedOption(commandLineArg,commandLineUsage,false,commandLineDesc,false) );
  return true;
}

// *****************************************************************
// *****************************************************************
bool isMonitorEnabled()
{
   static bool enabled = SWA::Process::getInstance().getCommandLine().optionPresent(commandLineArg) || getenv("SQL_MONITOR") != 0; ; 
   return enabled; 
}

// *****************************************************************
// *****************************************************************
bool initCommandLine = configureCommandLine();

}


namespace SQLITE {
// *****************************************************************
// *****************************************************************
SqlQueryMonitor::SqlQueryMonitor(const std::string& query)
{
   if (isEnabled()){
       std::cout << "query  : " << query  << std::endl;
   }
}

// *****************************************************************
// *****************************************************************
SqlQueryMonitor::SqlQueryMonitor(const std::string& query, const ResultSet& resultSet, const bool result)
{
   if (isEnabled()){
       std::cout << "query  : " << query  << std::endl;
       std::cout << "result : " << result << std::endl;
       resultSet.display();
       std::cout << std::endl;
   }
}

// *****************************************************************
// *****************************************************************
SqlQueryMonitor::~SqlQueryMonitor( )
{

}

// *****************************************************************
// *****************************************************************
bool SqlQueryMonitor::isEnabled() 
{
   return isMonitorEnabled(); 
}

// *****************************************************************
// *****************************************************************
SqlStatementMonitor::SqlStatementMonitor(const std::string& statement, const bool result)
{
   if (isEnabled()){
       std::cout << "statement : " << statement << std::endl;
       std::cout << "result    : " << result    << std::endl;
       std::cout << std::endl;
   }
}

// *****************************************************************
// *****************************************************************
SqlStatementMonitor::~SqlStatementMonitor( )
{

}

// *****************************************************************
// *****************************************************************
bool SqlStatementMonitor::isEnabled() 
{
   return isMonitorEnabled(); 
}

// *****************************************************************
// *****************************************************************
SqlPreparedStatementMonitor::SqlPreparedStatementMonitor(const std::string& statement, const std::string& values, const bool result)
{
   if (isEnabled()){
       std::cout << "prepared statement : " << statement << "  (" << values << ")" << std::endl;
   }
}

// *****************************************************************
// *****************************************************************
SqlPreparedStatementMonitor::~SqlPreparedStatementMonitor( )
{

}

// *****************************************************************
// *****************************************************************
bool SqlPreparedStatementMonitor::isEnabled() 
{
   return isMonitorEnabled(); 
}

} // end namespace SQLITE
