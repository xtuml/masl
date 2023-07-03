/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ----------------------------------------------------------------------------
 * Classification: UK OFFICIAL
 * ----------------------------------------------------------------------------
 */

#ifndef Sqlite_SqlMonitor_HH
#define Sqlite_SqlMonitor_HH

#include <string>

namespace SQLITE {

class ResultSet;
class ObjectResultSet;
// *****************************************************************
// *****************************************************************
class SqlQueryMonitor
{
  public:
    explicit SqlQueryMonitor(const std::string& query);
    explicit SqlQueryMonitor(const std::string& query, const ResultSet& resultSet, const bool result);
   ~SqlQueryMonitor( );

  private:
     // prevent copy and assignment
     SqlQueryMonitor(const SqlQueryMonitor& rhs);  
     SqlQueryMonitor& operator=(const SqlQueryMonitor& rhs);  

     static bool isEnabled();
};

// *****************************************************************
// *****************************************************************
class SqlStatementMonitor
{
  public:
    explicit SqlStatementMonitor(const std::string& statement, const bool result);
   ~SqlStatementMonitor();

  private:
     // prevent copy and assignment
     SqlStatementMonitor(const SqlStatementMonitor& rhs);  
     SqlStatementMonitor& operator=(const SqlStatementMonitor& rhs);  

     static bool isEnabled();
};

// *****************************************************************
// *****************************************************************
class SqlPreparedStatementMonitor
{
  public:
    explicit SqlPreparedStatementMonitor(const std::string& statement, const std::string& values, const bool result);
   ~SqlPreparedStatementMonitor();
     static bool isEnabled();

  private:
     // prevent copy and assignment
     SqlPreparedStatementMonitor(const SqlPreparedStatementMonitor& rhs);  
     SqlPreparedStatementMonitor& operator=(const SqlPreparedStatementMonitor& rhs);  
};


} // end namespace SQLITE
#endif
