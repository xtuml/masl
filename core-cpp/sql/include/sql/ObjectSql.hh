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

#ifndef Sql_ObjectSql_HH
#define Sql_ObjectSql_HH

#include <string>

namespace SQL {

// *****************************************************************
//! \brief 
//! Define an interface that provides the basic sql details for
//! an object that is being stored in a SQL database.
//! 
// *****************************************************************
class ObjectSql
{
   public:
    virtual ~ObjectSql() {}

    virtual const std::string& getTableName  () const = 0;
    virtual const std::string& getObjectName () const = 0;
    virtual const std::string  getDomainName () const = 0;
    virtual const std::string  getColumnName (const std::string& attribute) const = 0;

   protected:
      ObjectSql() {}

   private:
      ObjectSql(const ObjectSql& rhs);
      ObjectSql& operator=(const ObjectSql& rhs);

};

} // end namespace SQL

#endif
