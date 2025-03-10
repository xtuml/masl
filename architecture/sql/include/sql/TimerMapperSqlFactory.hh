/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#ifndef Sql_TimermapperSqlFactory_HH
#define Sql_TimermapperSqlFactory_HH

#include <memory>

namespace SQL {

    class TimerMapperSql;
    class TimerMapperSqlFactory {
      public:
        static TimerMapperSqlFactory &singleton();

        bool registerImpl(const std::shared_ptr<TimerMapperSql> &impl);

        std::shared_ptr<TimerMapperSql> &getImpl();

      private:
        TimerMapperSqlFactory();
        ~TimerMapperSqlFactory();

      private:
        TimerMapperSqlFactory(const TimerMapperSqlFactory &rhs);
        TimerMapperSqlFactory &operator=(const TimerMapperSqlFactory &rhs);

      private:
        std::shared_ptr<TimerMapperSql> impl_;
    };

} // end namespace SQL

#endif
