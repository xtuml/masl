/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#ifndef Sql_RelationshipTernaryDefinitions_HH
#define Sql_RelationshipTernaryDefinitions_HH

#include "RelationshipAssociativeContainers.hh"
#include "RelationshipTernaryMapper.hh"
#include "Util.hh"

namespace SQL {

    // ***********************************************************************
    //! @Brief Class to handle a associative one-to-one relationship mapper.
    // ***********************************************************************
    template <int rel, class Lhs, class Rhs, class Ass, bool lhsC, bool rhsC>
    struct AssociativeOneToOneRelationship {
        typedef OneAssociativeRelationshipContainer<rel, Lhs, Ass> LhsContainer;
        typedef OneAssociativeRelationshipContainer<rel, Rhs, Ass> RhsContainer;
        typedef OneAssociativeRelationshipContainer<rel, Lhs, Rhs> AssContainer;
        typedef RelationshipTernaryMapper<rel, LhsContainer, RhsContainer, AssContainer, lhsC, rhsC> mapper_type;
    };

    // ***********************************************************************
    //! @Brief Class to handle a associative one-to-many relationship mapper.
    // ***********************************************************************
    template <int rel, class Lhs, class Rhs, class Ass, bool lhsC, bool rhsC>
    struct AssociativeOneToManyRelationship {
        typedef OneAssociativeRelationshipContainer<rel, Lhs, Ass> LhsContainer;
        typedef ManyAssociativeRelationshipContainer<rel, Rhs, Ass> RhsContainer;
        typedef OneAssociativeRelationshipContainer<rel, Lhs, Rhs> AssContainer;
        typedef RelationshipTernaryMapper<rel, LhsContainer, RhsContainer, AssContainer, lhsC, rhsC> mapper_type;
    };

    // ***********************************************************************
    //! @Brief Class to handle a associative many-to-one relationship mapper.
    // ***********************************************************************
    template <int rel, class Lhs, class Rhs, class Ass, bool lhsC, bool rhsC>
    struct AssociativeManyToOneRelationship {
        typedef ManyAssociativeRelationshipContainer<rel, Lhs, Ass> LhsContainer;
        typedef OneAssociativeRelationshipContainer<rel, Rhs, Ass> RhsContainer;
        typedef OneAssociativeRelationshipContainer<rel, Lhs, Rhs> AssContainer;
        typedef RelationshipTernaryMapper<rel, LhsContainer, RhsContainer, AssContainer, lhsC, rhsC> mapper_type;
    };

    // ***********************************************************************
    //! @Brief Class to handle a associative many-to-many relationship mapper.
    // ***********************************************************************
    template <int rel, class Lhs, class Rhs, class Ass, bool lhsC, bool rhsC>
    struct AssociativeManyToManyRelationship {
        typedef ManyAssociativeRelationshipContainer<rel, Lhs, Ass> LhsContainer;
        typedef ManyAssociativeRelationshipContainer<rel, Rhs, Ass> RhsContainer;
        typedef OneAssociativeRelationshipContainer<rel, Lhs, Rhs> AssContainer;
        typedef RelationshipTernaryMapper<rel, LhsContainer, RhsContainer, AssContainer, lhsC, rhsC> mapper_type;
    };

} // namespace SQL

#endif
