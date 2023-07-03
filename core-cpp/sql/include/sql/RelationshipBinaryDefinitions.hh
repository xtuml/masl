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

#ifndef Sql_RelationshipBinaryDefinitions_HH
#define Sql_RelationshipBinaryDefinitions_HH

#include "RelationshipContainers.hh"
#include "RelationshipBinaryMapper.hh"

namespace SQL {

// ***********************************************************************
//! @Brief Class to handle a one-to-one relationship mapper.
// ***********************************************************************
template <int rel, class Lhs, class Rhs, bool lhsC, bool rhsC>
struct OneToOneRelationship {

    typedef OneRelationshipContainer<rel,Lhs> RhsToLhsContainer;
    typedef OneRelationshipContainer<rel,Rhs> LhsToRhsContainer;
    typedef RelationshipBinaryMapper<rel,RhsToLhsContainer,LhsToRhsContainer,lhsC,rhsC> mapper_type;
};

// ***********************************************************************
//! @Brief Class to handle a one-to-many relationship mapper.
// ***********************************************************************
template <int rel, class Lhs, class Rhs, bool lhsC, bool rhsC>
struct OneToManyRelationship {

    typedef OneRelationshipContainer <rel,Lhs> RhsToLhsContainer;
    typedef ManyRelationshipContainer<rel,Rhs> LhsToRhsContainer;
    typedef RelationshipBinaryMapper<rel,RhsToLhsContainer,LhsToRhsContainer,lhsC,rhsC> mapper_type;
};

// ***********************************************************************
//! @Brief Class to handle a many-to-one relationship mapper.
// ***********************************************************************
template <int rel, class Lhs, class Rhs, bool lhsC, bool rhsC>
struct ManyToOneRelationship {

    typedef OneRelationshipContainer<rel,Rhs>  LhsToRhsContainer;
    typedef ManyRelationshipContainer<rel,Lhs> RhsToLhsContainer;
    typedef RelationshipBinaryMapper<rel,RhsToLhsContainer,LhsToRhsContainer,lhsC,rhsC> mapper_type;
};

} // end namepsace SQL


#endif
