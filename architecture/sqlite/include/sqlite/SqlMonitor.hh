/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
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
class SqlQueryMonitor {
  public:
    explicit SqlQueryMonitor(const std::string &query);
    explicit SqlQueryMonitor(const std::string &query,
                             const ResultSet &resultSet, const bool result);
    ~SqlQueryMonitor();

  private:
    // prevent copy and assignment
    SqlQueryMonitor(const SqlQueryMonitor &rhs);
    SqlQueryMonitor &operator=(const SqlQueryMonitor &rhs);

    static bool isEnabled();
};

// *****************************************************************
// *****************************************************************
class SqlStatementMonitor {
  public:
    explicit SqlStatementMonitor(const std::string &statement,
                                 const bool result);
    ~SqlStatementMonitor();

  private:
    // prevent copy and assignment
    SqlStatementMonitor(const SqlStatementMonitor &rhs);
    SqlStatementMonitor &operator=(const SqlStatementMonitor &rhs);

    static bool isEnabled();
};

// *****************************************************************
// *****************************************************************
class SqlPreparedStatementMonitor {
  public:
    explicit SqlPreparedStatementMonitor(const std::string &statement,
                                         const std::string &values,
                                         const bool result);
    ~SqlPreparedStatementMonitor();
    static bool isEnabled();

  private:
    // prevent copy and assignment
    SqlPreparedStatementMonitor(const SqlPreparedStatementMonitor &rhs);
    SqlPreparedStatementMonitor &
    operator=(const SqlPreparedStatementMonitor &rhs);
};

} // end namespace SQLITE
#endif
