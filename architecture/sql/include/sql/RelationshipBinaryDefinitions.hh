/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#ifndef Sql_RelationshipBinaryDefinitions_HH
#define Sql_RelationshipBinaryDefinitions_HH

#include "RelationshipBinaryMapper.hh"
#include "RelationshipContainers.hh"

namespace SQL {

    // ***********************************************************************
    //! @Brief Class to handle a one-to-one relationship mapper.
    // ***********************************************************************
    template <int rel, class Lhs, class Rhs, bool lhsC, bool rhsC>
    struct OneToOneRelationship {

        typedef OneRelationshipContainer<rel, Lhs> RhsToLhsContainer;
        typedef OneRelationshipContainer<rel, Rhs> LhsToRhsContainer;
        typedef RelationshipBinaryMapper<rel, RhsToLhsContainer, LhsToRhsContainer, lhsC, rhsC> mapper_type;
    };

    // ***********************************************************************
    //! @Brief Class to handle a one-to-many relationship mapper.
    // ***********************************************************************
    template <int rel, class Lhs, class Rhs, bool lhsC, bool rhsC>
    struct OneToManyRelationship {

        typedef OneRelationshipContainer<rel, Lhs> RhsToLhsContainer;
        typedef ManyRelationshipContainer<rel, Rhs> LhsToRhsContainer;
        typedef RelationshipBinaryMapper<rel, RhsToLhsContainer, LhsToRhsContainer, lhsC, rhsC> mapper_type;
    };

    // ***********************************************************************
    //! @Brief Class to handle a many-to-one relationship mapper.
    // ***********************************************************************
    template <int rel, class Lhs, class Rhs, bool lhsC, bool rhsC>
    struct ManyToOneRelationship {

        typedef OneRelationshipContainer<rel, Rhs> LhsToRhsContainer;
        typedef ManyRelationshipContainer<rel, Lhs> RhsToLhsContainer;
        typedef RelationshipBinaryMapper<rel, RhsToLhsContainer, LhsToRhsContainer, lhsC, rhsC> mapper_type;
    };

} // namespace SQL

#endif
