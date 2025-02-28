/*
 * ----------------------------------------------------------------------------
 * (c) 2009-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
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

#ifndef Sql_RelationshipSqlRepository_HH
#define Sql_RelationshipSqlRepository_HH

#include <map>
#include <string>

namespace SQL {

    // *****************************************************************
    //! Define an repository that can be used to programatically determine
    //! the SQL mapping that have been used for a MASl object.
    // *****************************************************************
    class RelationshipSql;
    class RelationshipSqlRepository {
      public:
        static RelationshipSqlRepository &getInstance();

        bool registerRelationshipSql(const RelationshipSql *const sql);
        void deregisterRelationshipSql(const RelationshipSql *const sql);

        const RelationshipSql &
        getRelationshipSql(const std::string &domainName, const std::string &relationshipName) const;

      private:
        RelationshipSqlRepository();
        ~RelationshipSqlRepository();

      private:
        RelationshipSqlRepository(const RelationshipSqlRepository &rhs);
        RelationshipSqlRepository &operator=(const RelationshipSqlRepository &rhs);

      private:
        std::map<std::string, const RelationshipSql *const> repository;
    };

} // end namespace SQL

#endif
