/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#ifndef Sql_RelationshipSql_HH
#define Sql_RelationshipSql_HH

#include <string>

#include "Exception.hh"
#include "boost/tuple/tuple.hpp"

namespace SQL {

    // *****************************************************************
    //! \brief
    //! Define an interface that provides the basic sql details for
    //! a relationship that is being stored in a SQL database.
    //!
    // *****************************************************************
    class RelationshipSql {
      public:
        virtual ~RelationshipSql() {}

        virtual const std::string &getTableName() const = 0;
        virtual const std::string getDomainName() const = 0;
        virtual const std::string getLhsColumnName() const = 0;
        virtual const std::string getRhsColumnName() const = 0;
        virtual const std::string getAssocColumnName() const {
            throw SqlException(::boost::make_tuple(
                "RelationshipSql::getAssocColumnName : invalid "
                "call for relationship ",
                getDomainName(),
                getRelationshipName()
            ));
        }
        virtual const std::string &getRelationshipName() const = 0;

      protected:
        RelationshipSql() {}

      private:
        RelationshipSql(const RelationshipSql &rhs);
        RelationshipSql &operator=(const RelationshipSql &rhs);
    };

} // end namespace SQL

#endif
