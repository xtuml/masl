//============================================================================//
// UK Crown Copyright (c) 2007. All rights reserved.
//
// File:  RelationshipTenaryDefinitions.hh 
//
//============================================================================//

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
