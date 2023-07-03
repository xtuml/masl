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

#include "sql/ObjectSql.hh"
#include "sql/Exception.hh"
#include "sql/ObjectSqlRepository.hh"

namespace SQL {

// ***********************************************************************
// ***********************************************************************
ObjectSqlRepository& ObjectSqlRepository::getInstance()
{
   static ObjectSqlRepository instance;
   return instance;
}

// ***********************************************************************
// ***********************************************************************
ObjectSqlRepository::ObjectSqlRepository()
{

}

// ***********************************************************************
// ***********************************************************************
ObjectSqlRepository::~ObjectSqlRepository()
{

}

// ***********************************************************************
// ***********************************************************************
bool ObjectSqlRepository::registerObjectSql(const ObjectSql* const sql)
{
   const std::string objectKey(sql->getDomainName() + "::" + sql->getObjectName());
   repository.insert(std::make_pair(objectKey, sql));
   return true;
}

// ***********************************************************************
// ***********************************************************************
void ObjectSqlRepository::deregisterObjectSql(const ObjectSql* const sql)
{
    // Cannot remove the registered components as cannot be sure the
    // order that this singleton will be destroyed on shutdown. Therefore
    // do not remove any registered components.
}

// ***********************************************************************
// ***********************************************************************
const ObjectSql& ObjectSqlRepository::getObjectSql(const std::string& domainName, const std::string& objectName) const
{
    const std::string objectKey(domainName + "::" + objectName);
    std::map<std::string, const ObjectSql* const>::const_iterator objSqlItr = repository.find(objectKey);
    if (objSqlItr == repository.end()){
        throw SqlException(std::string("ObjectSqlRepository::getObjectSql : Failed to find SQL details for object ") + objectKey);
    }
    return *(objSqlItr->second);
}


} // end namespace SQL
