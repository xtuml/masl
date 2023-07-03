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

#ifndef Sql_RelationshipTenaryDefinitions_HH
#define Sql_RelationshipTenaryDefinitions_HH

#include "Util.hh"
#include "RelationshipTenaryMapper.hh"
#include "RelationshipAssociativeContainers.hh"

namespace SQL {

// ***********************************************************************
//! @Brief Class to handle a associative one-to-one relationship mapper.
// ***********************************************************************
template <int rel, class Lhs, class Rhs, class Ass, bool lhsC, bool rhsC>
struct AssociativeOneToOneRelationship 
{
    typedef OneAssociativeRelationshipContainer<rel,Lhs,Ass> LhsContainer;
    typedef OneAssociativeRelationshipContainer<rel,Rhs,Ass> RhsContainer;
    typedef OneAssociativeRelationshipContainer<rel,Lhs,Rhs> AssContainer;
    typedef RelationshipTenaryMapper<rel,LhsContainer,RhsContainer,AssContainer,lhsC,rhsC> mapper_type;
};

// ***********************************************************************
//! @Brief Class to handle a associative one-to-many relationship mapper.
// ***********************************************************************
template <int rel, class Lhs, class Rhs, class Ass, bool lhsC, bool rhsC>
struct AssociativeOneToManyRelationship 
{
    typedef OneAssociativeRelationshipContainer <rel,Lhs,Ass> LhsContainer;
    typedef ManyAssociativeRelationshipContainer<rel,Rhs,Ass> RhsContainer;
    typedef OneAssociativeRelationshipContainer <rel,Lhs,Rhs> AssContainer;
    typedef RelationshipTenaryMapper <rel,LhsContainer,RhsContainer,AssContainer,lhsC,rhsC> mapper_type;
};

// ***********************************************************************
//! @Brief Class to handle a associative many-to-one relationship mapper.
// ***********************************************************************
template <int rel, class Lhs, class Rhs, class Ass, bool lhsC, bool rhsC>
struct AssociativeManyToOneRelationship 
{
    typedef ManyAssociativeRelationshipContainer<rel,Lhs,Ass> LhsContainer;
    typedef OneAssociativeRelationshipContainer <rel,Rhs,Ass> RhsContainer;
    typedef OneAssociativeRelationshipContainer <rel,Lhs,Rhs> AssContainer;
    typedef RelationshipTenaryMapper<rel,LhsContainer,RhsContainer,AssContainer,lhsC,rhsC> mapper_type;
};

// ***********************************************************************
//! @Brief Class to handle a associative many-to-many relationship mapper.
// ***********************************************************************
template <int rel, class Lhs, class Rhs, class Ass, bool lhsC, bool rhsC>
struct AssociativeManyToManyRelationship 
{
    typedef ManyAssociativeRelationshipContainer<rel,Lhs,Ass> LhsContainer;
    typedef ManyAssociativeRelationshipContainer<rel,Rhs,Ass> RhsContainer;
    typedef OneAssociativeRelationshipContainer <rel,Lhs,Rhs> AssContainer;
    typedef RelationshipTenaryMapper<rel,LhsContainer,RhsContainer,AssContainer,lhsC,rhsC> mapper_type;
};

} // end namepsace SQL


#endif
