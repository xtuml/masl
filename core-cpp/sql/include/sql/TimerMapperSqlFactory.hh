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

#ifndef Sql_TimermapperSqlFactory_HH
#define Sql_TimermapperSqlFactory_HH

#include "boost/shared_ptr.hpp"

namespace SQL {

class TimerMapperSql;
class TimerMapperSqlFactory
{
   public:
     static TimerMapperSqlFactory& singleton();
     
     bool registerImpl(const boost::shared_ptr<TimerMapperSql>& impl);

     boost::shared_ptr<TimerMapperSql>& getImpl();

   private:
       TimerMapperSqlFactory();
      ~TimerMapperSqlFactory();

   private:
      TimerMapperSqlFactory(const TimerMapperSqlFactory& rhs);
      TimerMapperSqlFactory& operator=(const TimerMapperSqlFactory& rhs);

   private:
     boost::shared_ptr<TimerMapperSql> impl_;
};

}  // end namespace SQL

#endif
