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

#ifndef Sql_StatementGenerator_HH
#define Sql_StatementGenerator_HH

namespace SQL {

class Criteria;
// *****************************************************************
//! \brief 
//!
//! 
// *****************************************************************
template <class T>
class SQLGenerator
{
   protected:
       virtual ~StatementGenerator();

       virtual const std::string getTableName  () const = 0;
       virtual const std::string getObjectName () const = 0;

       virtual void getRowCount      (std::string& oStatement);
       virtual void getMaxIdentifier (std::string& oStatement);

       virtual void update (const T& object, std::string& oStatement) const;
       virtual void insert (const T& object, std::string& oStatement) const;
       virtual void remove (const T& object, std::string& oStatement) const;

       virtual void selectOne          (std::string& oStatement);
       virtual void selectAll          (std::string& oStatement);

       virtual void select             (const Criteria& criteria,      std::string& oStatement);
       virtual void selectOne          (const unsigned int identifier, std::string& oStatement);
       virtual void selectAllWithLimit (const unsigned int rowCount,   std::string& oStatement);

    private:
        SQLGenerator(const SQLGenerator& rhs);
        SQLGenerator& operator=(const SQLGenerator& rhs);

   protected:
        SQLGenerator();
};

} // end namespace SQL

#endif
