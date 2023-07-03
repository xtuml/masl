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

#include "sql/Criteria.hh"
#include "sql/CriteriaFactory.hh"

namespace SQL {

// ***********************************************************************
// ***********************************************************************
Criteria::Criteria():
   impl_(CriteriaFactory::singleton().newInstance())
{

}

// ***********************************************************************
// ***********************************************************************
Criteria::~Criteria()
{

}

// ***********************************************************************
// ***********************************************************************
bool Criteria::empty() const
{
  return impl_->empty();
}

// ***********************************************************************
// ***********************************************************************
void Criteria::setLimit (const int32_t limit)
{
   impl_->setLimit(limit);
}

// ***********************************************************************
// ***********************************************************************
void Criteria::addFromClause (const std::string& tableName)
{
    impl_->addFromClause(tableName);
}

// ***********************************************************************
// ***********************************************************************
void Criteria::addWhereClause (const std::string& where)
{
    impl_->addWhereClause(where);
}

// ***********************************************************************
// ***********************************************************************
void Criteria::addColumn (const std::string& columnName)
{
   impl_->addColumn(columnName);
}

// ***********************************************************************
// ***********************************************************************
void Criteria::addAllColumn ()
{
   impl_->addAllColumn();
}

// ***********************************************************************
// ***********************************************************************
void Criteria::addAllColumns (const std::string& tableName)
{
   impl_->addAllColumns(tableName);
}

// ***********************************************************************
// ***********************************************************************
std::string Criteria::selectStatement() const
{
    return impl_->selectStatement();
}


} // end namespace SQL


