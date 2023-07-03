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

#include <iostream>
#include "sql/Exception.hh"
#include "sql/RelationshipSql.hh"
#include "sql/RelationshipSqlRepository.hh"

namespace SQL {

// ***********************************************************************
// ***********************************************************************
RelationshipSqlRepository& RelationshipSqlRepository::getInstance()
{
   static RelationshipSqlRepository instance;
   return instance;
}

// ***********************************************************************
// ***********************************************************************
RelationshipSqlRepository::RelationshipSqlRepository()
{

}

// ***********************************************************************
// ***********************************************************************
RelationshipSqlRepository::~RelationshipSqlRepository()
{

}

// ***********************************************************************
// ***********************************************************************
bool RelationshipSqlRepository::registerRelationshipSql(const RelationshipSql* const sql)
{
   const std::string relationshipKey(sql->getRelationshipName());
   repository.insert(std::make_pair(relationshipKey, sql));
   return true;
}

// ***********************************************************************
// ***********************************************************************
void RelationshipSqlRepository::deregisterRelationshipSql(const RelationshipSql* const sql)
{
    // Cannot remove the registered components as cannot be sure the
    // order that this singleton will be destroyed on shutdown. Therefore
    // do not remove any registered components.
}

// ***********************************************************************
// ***********************************************************************
const RelationshipSql& RelationshipSqlRepository::getRelationshipSql(const std::string& domainName, const std::string& relationshipName) const
{
    const std::string relationshipKey(domainName + "_" + relationshipName);
    std::map<std::string, const RelationshipSql* const>::const_iterator relSqlItr = repository.find(relationshipKey);
    if (relSqlItr == repository.end()){
        throw SqlException(std::string("RelationshipSqlRepository::getRelationshipSql : Failed to find SQL details for relationship ") + relationshipKey);
    }
    return *(relSqlItr->second);
}


} // end namespace SQL
