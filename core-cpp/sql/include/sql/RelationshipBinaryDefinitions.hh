//============================================================================//
// UK Crown Copyright (c) 2007. All rights reserved.
//
// File:  RelationshipBinaryDefinitions.hh 
//
//============================================================================//

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
