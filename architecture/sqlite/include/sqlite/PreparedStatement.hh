/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#ifndef Sqlite_PreparedStatement_HH
#define Sqlite_PreparedStatement_HH

#include <iostream>
#include <stdint.h>
#include <string>

#include "Exception.hh"

#include "boost/tuple/tuple.hpp"
#include <memory>

#include "BlobData.hh"

namespace SWA {
    class Timestamp;
    class Duration;
    class String;
} // namespace SWA

namespace SQLITE {

    // *****************************************************************
    //! The Sqlite database engine has a C API that enables commonly executed
    //! SQL statements to be precompiled. The precompiled statements have
    //! place holders located in the defined statement that can be bound to
    //! data values using a further C API. These kind of statements are known
    //! as Prepared Statements. An example SQL INSERT  prepared statement is
    //! shown below:
    //!
    //!   INSERT INTO TEST_TABLE_A VALUES(:1,:2,:3,:4);
    //!
    //! Notice the place holder text ':1', ':2', ':3', ':4'. Values can be
    //! bound to these positions at runtime using the sqlite_bind_* functions.
    //!
    //! This class hides the sqlite specific implementation from any
    //! exposed interfaces of this package.
    //!
    //! IMPORTANT NOTE:
    //!    A prepared statement once 'prepared' is locked to the database
    //! that was open at the time. If the database is closed and a new
    //! database opened, execution of the prepared statements will not
    //! operate on the new database, but the old one. To update the prepared
    //! statement to work on the new database the prepare method should be invoked.
    // *****************************************************************
    class PreparedStatement {
      public:
        enum { MAX_PARAMETER_COUNT = 70 };

        // *****************************************************************
        //! Constructor
        //!    example statement : "DELETE FROM TEST_TABLE_A WHERE col1 = :1;"
        //! @param statement The SQL statement to prepare
        // *****************************************************************
        PreparedStatement(const std::string &statement);
        ~PreparedStatement();

        // *****************************************************************
        //! Must be called before any other non-constructor method to initialise
        //! the sqlite database with the associated prepared statement.
        // *****************************************************************
        void prepare();

        // *****************************************************************
        //! A lot of the generated preparedStatement objects are configured with
        //! a single integer bind parameter, as the SQL statement is deleting a
        //! row from the table using the business objects architecture id. Therefore
        //! for efficiency reasons the following method is defined so the single
        //! parameter does not need to be placed in a boost tuple.
        //!
        //! @param p1 the parameter to bind to position ":1".
        // *****************************************************************
        void execute(const int32_t &p1) const;

        // *****************************************************************
        //! A lot of the generated preparedStatement objects are configured with
        //! a single integer bind parameter. Therefore for efficiency reasons the
        //! following method is defined so the single parameter does not need
        //! to be placed in a boost tuple.
        //!
        //! @param p1 the parameter to bind to position ":1".
        // *****************************************************************
        void execute(const uint32_t &p1) const;

        // *****************************************************************
        //! Binds the values supplied in the tuple to the column positions
        //! and then executes the SQL statement with these bound values.
        //! A tuple can only hold upto ten values therefore this interface
        //! supprts tables with 1-10 columns.
        //!
        //! @param p1 the tuple holding values for columns 1-10
        // *****************************************************************
        template <class P1>
        void execute(const P1 &p1) const;

        // *****************************************************************
        //! Binds the values supplied in the tuples to the column positions
        //! and then executes the SQL statement with these bound values.
        //! A tuple can only hold upto ten values therefore this interface
        //! supports tables with 1-20 columns.
        //!
        //! @param p1 the tuple holding values for columns 1-10
        //! @param p2 the tuple holding values for columns 11-20
        // *****************************************************************
        template <class P1, class P2>
        void execute(const P1 &p1, const P2 &p2) const;

        // *****************************************************************
        //! Binds the values supplied in the tuples to the column positions
        //! and then executes the SQL statement with these bound values.
        //! A tuple can only hold upto ten values therefore this interface
        //! supprts tables with 1-30 columns.
        //!
        //! @param p1 the tuple holding values for columns 1-10
        //! @param p2 the tuple holding values for columns 11-20
        //! @param p3 the tuple holding values for columns 21-30
        // *****************************************************************
        template <class P1, class P2, class P3>
        void execute(const P1 &p1, const P2 &p2, const P3 &p3) const;

        // *****************************************************************
        //! Binds the values supplied in the tuples to the column positions
        //! and then executes the SQL statement with these bound values.
        //! A tuple can only hold upto ten values therefore this interface
        //! only supprts tables with 1-40 columns.
        //!
        //! @param p1 the tuple holding values for columns 1-10
        //! @param p2 the tuple holding values for columns 11-20
        //! @param p3 the tuple holding values for columns 21-30
        //! @param p4 the tuple holding values for columns 31-40
        // *****************************************************************
        template <class P1, class P2, class P3, class P4>
        void execute(const P1 &p1, const P2 &p2, const P3 &p3, const P4 &p4) const;

        // *****************************************************************
        //! Binds the values supplied in the tuples to the column positions
        //! and then executes the SQL statement with these bound values.
        //! A tuple can only hold upto ten values therefore this interface
        //! only supprts tables with 1-50 columns.
        //!
        //! @param p1 the tuple holding values for columns 1-10
        //! @param p2 the tuple holding values for columns 11-20
        //! @param p3 the tuple holding values for columns 21-30
        //! @param p4 the tuple holding values for columns 31-40
        //! @param p5 the tuple holding values for columns 41-50
        // *****************************************************************
        template <class P1, class P2, class P3, class P4, class P5>
        void execute(const P1 &p1, const P2 &p2, const P3 &p3, const P4 &p4, const P5 &p5) const;

        // *****************************************************************
        //! Binds the values supplied in the tuples to the column positions
        //! and then executes the SQL statement with these bound values.
        //! A tuple can only hold upto ten values therefore this interface
        //! only supprts tables with 1-60 columns.
        //!
        //! @param p1 the tuple holding values for columns 1-10
        //! @param p2 the tuple holding values for columns 11-20
        //! @param p3 the tuple holding values for columns 21-30
        //! @param p4 the tuple holding values for columns 31-40
        //! @param p5 the tuple holding values for columns 41-50
        //! @param p6 the tuple holding values for columns 51-60
        // *****************************************************************
        template <class P1, class P2, class P3, class P4, class P5, class P6>
        void execute(const P1 &p1, const P2 &p2, const P3 &p3, const P4 &p4, const P5 &p5, const P6 &p6) const;

        // *****************************************************************
        //! Binds the values supplied in the tuples to the column positions
        //! and then executes the SQL statement with these bound values.
        //! A tuple can only hold upto ten values therefore this interface
        //! only supprts tables with 1-60 columns.
        //!
        //! @param p1 the tuple holding values for columns 1-10
        //! @param p2 the tuple holding values for columns 11-20
        //! @param p3 the tuple holding values for columns 21-30
        //! @param p4 the tuple holding values for columns 31-40
        //! @param p5 the tuple holding values for columns 41-50
        //! @param p6 the tuple holding values for columns 51-60
        //! @param p7 the tuple holding values for columns 61-70
        // *****************************************************************
        template <class P1, class P2, class P3, class P4, class P5, class P6, class P7>
        void
        execute(const P1 &p1, const P2 &p2, const P3 &p3, const P4 &p4, const P5 &p5, const P6 &p6, const P7 &p7) const;

      private:
        // *****************************************************************
        //! Helper class used by BindTuple (see bleow) to bind the individual
        //! element values of a boost::tuple to the required positional indexes
        //! associated with the SQL statement of the specified PreparedStatement
        //! object.
        // *****************************************************************
        template <int32_t index, class T>
        struct BindTupleElement {
            static void bind(const PreparedStatement &pstatement, int32_t baseIndex, const T &tuple) {
                pstatement.bind(baseIndex + index + 1, tuple.template get<index>());
                // Use compile time recursion to instantate the required set of bind
                // calls for each of the elements found in the specified tuple type.
                // The recursion will continue until the a BindTupleElement<0,T>
                // specialisation (see below) is encountered.
                BindTupleElement<index - 1, T>::bind(pstatement, baseIndex, tuple);
            }
        };

        // *****************************************************************
        //! Partial specialisation to end compile time recursion of
        //! BindTupleElement::bind(...).
        // *****************************************************************
        template <class T>
        struct BindTupleElement<0, T> {
            static void bind(const PreparedStatement &pstatement, int32_t baseIndex, const T &tuple) {
                pstatement.bind(baseIndex + 1, tuple.template get<0>());
            }
        };

        // *****************************************************************
        //! Define an inner class that uses Meta-Programming techniques (compile
        //! time recursion in this case) to bind boost::tuple elements and their
        //! associated values to positions within the contained SQL prepared
        //! statement.
        //! So for example a prepared statement of the form
        //!            INSERT INTO TABLE_A VALUES(:1,:2,:3);
        //! will bind a three element boost tuple in the following manner
        //!       `:1`  bound to value from tuple.get<0>();
        //!       `:2`  bound to value from tuple.get<1>();
        //!       `:3`  bound to value from tuple.get<2>();
        // *****************************************************************
        template <class T>
        struct BindTuple {
            // *****************************************************************
            //! Used to bind the values associated with a tuple to the required
            //! positions within the SQL statement of the specified
            //! PreparedStatement object.
            //!
            //! @param pstatement the prepared statement the tuple values should be
            //! bound too.
            //! @param baseIndex  the starting SQL position index for the current
            //! tuple (0,10,20,30).
            //! @param tuple the  tuple instance with the values to be used by SQL
            //! statement.
            // *****************************************************************
            static void bind(const PreparedStatement &pstatement, int32_t baseIndex, const T &tuple) {
                const int tuple_length = ::boost::tuples::length<T>::value;
                // Tuple elements are accessed using an index starting at 0,
                // therefore subtract one from the tuple length to get the last
                // valid index value. Notice that the binding of the tuple elements
                // to their specified SQL statement positions will occur in reverse
                // order (10,9,8...0).
                BindTupleElement<tuple_length - 1, T>::bind(pstatement, baseIndex, tuple);
            }
        };

      private:
        // Prevent copy and assignment
        PreparedStatement(const PreparedStatement &rhs);
        PreparedStatement &operator=(const PreparedStatement &rhs);

        // The bind methods only need to be accessed by the inner classes used
        // in the binding of a tuple to its column positions, therefore they
        // can be restricted to private scope.
        void bind(const int32_t position, const uint32_t value) const;
        void bind(const int32_t position, const int32_t value) const;
        void bind(const int32_t position, const uint64_t value) const;
        void bind(const int32_t position, const int64_t value) const;
        void bind(const int32_t position, const double value) const;
        void bind(const int32_t position, const std::string &value) const;
        void bind(const int32_t position, const char *const value) const;

        void bind(const int32_t position, const SWA::Timestamp &value) const;
        void bind(const int32_t position, const SWA::Duration &value) const;
        void bind(const int32_t position, const SWA::String &value) const;

        void bind(const int32_t position, const BlobData &encodedBlob) const;

        void execute() const;
        void reportError(const std::string &message) const;
        int32_t getPositionIndex(const int32_t position) const;

      private:
        class PreparedStatementImpl;
        std::shared_ptr<PreparedStatementImpl> impl;
    };

    // *****************************************************************
    // *****************************************************************
    template <class P1>
    void PreparedStatement::execute(const P1 &p1) const {
        try {
            BindTuple<P1>::bind(*this, 0, p1);
            execute();
        } catch (const SqliteSchemaException &sse) {
            // If a prepared statement has been initialised (prepared) and the
            // schema changes after this point, i.e. new tables are added, then the
            // execution of a prepared statement will fail with a schema error. This
            // error needs to be caught and the prepared statement re-tried as the
            // schema change is probably not related to the actual table used by the
            // prepared statement. If the execute fails on the second attempt, then
            // the schema change has effected the undelying prepared statement
            // table.
            BindTuple<P1>::bind(*this, 0, p1);
            execute();
        }
    }

    // *****************************************************************
    // *****************************************************************
    template <class P1, class P2>
    void PreparedStatement::execute(const P1 &p1, const P2 &p2) const {
        try {
            BindTuple<P2>::bind(*this, ::boost::tuples::length<P1>::value, p2);
            BindTuple<P1>::bind(*this, 0, p1);
            execute();
        } catch (const SqliteSchemaException &sse) {
            // If a prepared statement has been initialised (prepared) and the
            // schema changes after this point, i.e. new tables are added, then the
            // execution of a prepared statement will fail with a schema error. This
            // error needs to be caught and the prepared statement re-tried as the
            // schema change is probably not related to the actual table used by the
            // prepared statement. If the execute fails on the second attempt, then
            // the schema change has effected the undelying prepared statement
            // table.
            BindTuple<P2>::bind(*this, ::boost::tuples::length<P1>::value, p2);
            BindTuple<P1>::bind(*this, 0, p1);
            execute();
        }
    }

    // *****************************************************************
    // *****************************************************************
    template <class P1, class P2, class P3>
    void PreparedStatement::execute(const P1 &p1, const P2 &p2, const P3 &p3) const {
        try {
            BindTuple<P3>::bind(*this, ::boost::tuples::length<P1>::value + ::boost::tuples::length<P2>::value, p3);
            BindTuple<P2>::bind(*this, ::boost::tuples::length<P1>::value, p2);
            BindTuple<P1>::bind(*this, 0, p1);
            execute();
        } catch (const SqliteSchemaException &sse) {
            // If a prepared statement has been initialised (prepared) and the
            // schema changes after this point, i.e. new tables are added, then the
            // execution of a prepared statement will fail with a schema error. This
            // error needs to be caught and the prepared statement re-tried as the
            // schema change is probably not related to the actual table used by the
            // prepared statement. If the execute fails on the second attempt, then
            // the schema change has effected the undelying prepared statement
            // table.
            BindTuple<P3>::bind(*this, ::boost::tuples::length<P1>::value + ::boost::tuples::length<P2>::value, p3);
            BindTuple<P2>::bind(*this, ::boost::tuples::length<P1>::value, p2);
            BindTuple<P1>::bind(*this, 0, p1);
            execute();
        }
    }

    // *****************************************************************
    // *****************************************************************
    template <class P1, class P2, class P3, class P4>
    void PreparedStatement::execute(const P1 &p1, const P2 &p2, const P3 &p3, const P4 &p4) const {
        try {
            BindTuple<P4>::bind(
                *this,
                ::boost::tuples::length<P1>::value + ::boost::tuples::length<P2>::value +
                    ::boost::tuples::length<P3>::value,
                p4
            );
            BindTuple<P3>::bind(*this, ::boost::tuples::length<P1>::value + ::boost::tuples::length<P2>::value, p3);
            BindTuple<P2>::bind(*this, ::boost::tuples::length<P1>::value, p2);
            BindTuple<P1>::bind(*this, 0, p1);
            execute();
        } catch (const SqliteSchemaException &sse) {
            // If a prepared statement has been initialised (prepared) and the
            // schema changes after this point, i.e. new tables are added, then the
            // execution of a prepared statement will fail with a schema error. This
            // error needs to be caught and the prepared statement re-tried as the
            // schema change is probably not related to the actual table used by the
            // prepared statement. If the execute fails on the second attempt, then
            // the schema change has effected the undelying prepared statement
            // table.
            BindTuple<P4>::bind(
                *this,
                ::boost::tuples::length<P1>::value + ::boost::tuples::length<P2>::value +
                    ::boost::tuples::length<P3>::value,
                p4
            );
            BindTuple<P3>::bind(*this, ::boost::tuples::length<P1>::value + ::boost::tuples::length<P2>::value, p3);
            BindTuple<P2>::bind(*this, ::boost::tuples::length<P1>::value, p2);
            BindTuple<P1>::bind(*this, 0, p1);
            execute();
        }
    }

    // *****************************************************************
    // *****************************************************************
    template <class P1, class P2, class P3, class P4, class P5>
    void PreparedStatement::execute(const P1 &p1, const P2 &p2, const P3 &p3, const P4 &p4, const P5 &p5) const {
        try {
            BindTuple<P5>::bind(
                *this,
                ::boost::tuples::length<P1>::value + ::boost::tuples::length<P2>::value +
                    ::boost::tuples::length<P3>::value + ::boost::tuples::length<P4>::value,
                p5
            );
            BindTuple<P4>::bind(
                *this,
                ::boost::tuples::length<P1>::value + ::boost::tuples::length<P2>::value +
                    ::boost::tuples::length<P3>::value,
                p4
            );
            BindTuple<P3>::bind(*this, ::boost::tuples::length<P1>::value + ::boost::tuples::length<P2>::value, p3);
            BindTuple<P2>::bind(*this, ::boost::tuples::length<P1>::value, p2);
            BindTuple<P1>::bind(*this, 0, p1);
            execute();
        } catch (const SqliteSchemaException &sse) {
            // If a prepared statement has been initialised (prepared) and the
            // schema changes after this point, i.e. new tables are added, then the
            // execution of a prepared statement will fail with a schema error. This
            // error needs to be caught and the prepared statement re-tried as the
            // schema change is probably not related to the actual table used by the
            // prepared statement. If the execute fails on the second attempt, then
            // the schema change has effected the undelying prepared statement
            // table.
            BindTuple<P5>::bind(
                *this,
                ::boost::tuples::length<P1>::value + ::boost::tuples::length<P2>::value +
                    ::boost::tuples::length<P3>::value + ::boost::tuples::length<P4>::value,
                p5
            );
            BindTuple<P4>::bind(
                *this,
                ::boost::tuples::length<P1>::value + ::boost::tuples::length<P2>::value +
                    ::boost::tuples::length<P3>::value,
                p4
            );
            BindTuple<P3>::bind(*this, ::boost::tuples::length<P1>::value + ::boost::tuples::length<P2>::value, p3);
            BindTuple<P2>::bind(*this, ::boost::tuples::length<P1>::value, p2);
            BindTuple<P1>::bind(*this, 0, p1);
            execute();
        }
    }

    // *****************************************************************
    // *****************************************************************
    template <class P1, class P2, class P3, class P4, class P5, class P6>
    void PreparedStatement::execute(const P1 &p1, const P2 &p2, const P3 &p3, const P4 &p4, const P5 &p5, const P6 &p6)
        const {
        try {
            BindTuple<P6>::bind(
                *this,
                ::boost::tuples::length<P1>::value + ::boost::tuples::length<P2>::value +
                    ::boost::tuples::length<P3>::value + ::boost::tuples::length<P4>::value +
                    ::boost::tuples::length<P5>::value,
                p6
            );
            BindTuple<P5>::bind(
                *this,
                ::boost::tuples::length<P1>::value + ::boost::tuples::length<P2>::value +
                    ::boost::tuples::length<P3>::value + ::boost::tuples::length<P4>::value,
                p5
            );
            BindTuple<P4>::bind(
                *this,
                ::boost::tuples::length<P1>::value + ::boost::tuples::length<P2>::value +
                    ::boost::tuples::length<P3>::value,
                p4
            );
            BindTuple<P3>::bind(*this, ::boost::tuples::length<P1>::value + ::boost::tuples::length<P2>::value, p3);
            BindTuple<P2>::bind(*this, ::boost::tuples::length<P1>::value, p2);
            BindTuple<P1>::bind(*this, 0, p1);
            execute();
        } catch (const SqliteSchemaException &sse) {
            // If a prepared statement has been initialised (prepared) and the
            // schema changes after this point, i.e. new tables are added, then the
            // execution of a prepared statement will fail with a schema error. This
            // error needs to be caught and the prepared statement re-tried as the
            // schema change is probably not related to the actual table used by the
            // prepared statement. If the execute fails on the second attempt, then
            // the schema change has effected the undelying prepared statement
            // table.
            BindTuple<P6>::bind(
                *this,
                ::boost::tuples::length<P1>::value + ::boost::tuples::length<P2>::value +
                    ::boost::tuples::length<P3>::value + ::boost::tuples::length<P4>::value +
                    ::boost::tuples::length<P5>::value,
                p6
            );
            BindTuple<P5>::bind(
                *this,
                ::boost::tuples::length<P1>::value + ::boost::tuples::length<P2>::value +
                    ::boost::tuples::length<P3>::value + ::boost::tuples::length<P4>::value,
                p5
            );
            BindTuple<P4>::bind(
                *this,
                ::boost::tuples::length<P1>::value + ::boost::tuples::length<P2>::value +
                    ::boost::tuples::length<P3>::value,
                p4
            );
            BindTuple<P3>::bind(*this, ::boost::tuples::length<P1>::value + ::boost::tuples::length<P2>::value, p3);
            BindTuple<P2>::bind(*this, ::boost::tuples::length<P1>::value, p2);
            BindTuple<P1>::bind(*this, 0, p1);
            execute();
        }
    }

    // *****************************************************************
    // *****************************************************************
    template <class P1, class P2, class P3, class P4, class P5, class P6, class P7>
    void PreparedStatement::execute(
        const P1 &p1, const P2 &p2, const P3 &p3, const P4 &p4, const P5 &p5, const P6 &p6, const P7 &p7
    ) const {
        try {
            BindTuple<P7>::bind(
                *this,
                ::boost::tuples::length<P1>::value + ::boost::tuples::length<P2>::value +
                    ::boost::tuples::length<P3>::value + ::boost::tuples::length<P4>::value +
                    ::boost::tuples::length<P5>::value + ::boost::tuples::length<P6>::value,
                p7
            );
            BindTuple<P6>::bind(
                *this,
                ::boost::tuples::length<P1>::value + ::boost::tuples::length<P2>::value +
                    ::boost::tuples::length<P3>::value + ::boost::tuples::length<P4>::value +
                    ::boost::tuples::length<P5>::value,
                p6
            );
            BindTuple<P5>::bind(
                *this,
                ::boost::tuples::length<P1>::value + ::boost::tuples::length<P2>::value +
                    ::boost::tuples::length<P3>::value + ::boost::tuples::length<P4>::value,
                p5
            );
            BindTuple<P4>::bind(
                *this,
                ::boost::tuples::length<P1>::value + ::boost::tuples::length<P2>::value +
                    ::boost::tuples::length<P3>::value,
                p4
            );
            BindTuple<P3>::bind(*this, ::boost::tuples::length<P1>::value + ::boost::tuples::length<P2>::value, p3);
            BindTuple<P2>::bind(*this, ::boost::tuples::length<P1>::value, p2);
            BindTuple<P1>::bind(*this, 0, p1);
            execute();
        } catch (const SqliteSchemaException &sse) {
            // If a prepared statement has been initialised (prepared) and the
            // schema changes after this point, i.e. new tables are added, then the
            // execution of a prepared statement will fail with a schema error. This
            // error needs to be caught and the prepared statement re-tried as the
            // schema change is probably not related to the actual table used by the
            // prepared statement. If the execute fails on the second attempt, then
            // the schema change has effected the undelying prepared statement
            // table.
            BindTuple<P7>::bind(
                *this,
                ::boost::tuples::length<P1>::value + ::boost::tuples::length<P2>::value +
                    ::boost::tuples::length<P3>::value + ::boost::tuples::length<P4>::value +
                    ::boost::tuples::length<P5>::value + ::boost::tuples::length<P6>::value,
                p7
            );
            BindTuple<P6>::bind(
                *this,
                ::boost::tuples::length<P1>::value + ::boost::tuples::length<P2>::value +
                    ::boost::tuples::length<P3>::value + ::boost::tuples::length<P4>::value +
                    ::boost::tuples::length<P5>::value,
                p6
            );
            BindTuple<P5>::bind(
                *this,
                ::boost::tuples::length<P1>::value + ::boost::tuples::length<P2>::value +
                    ::boost::tuples::length<P3>::value + ::boost::tuples::length<P4>::value,
                p5
            );
            BindTuple<P4>::bind(
                *this,
                ::boost::tuples::length<P1>::value + ::boost::tuples::length<P2>::value +
                    ::boost::tuples::length<P3>::value,
                p4
            );
            BindTuple<P3>::bind(*this, ::boost::tuples::length<P1>::value + ::boost::tuples::length<P2>::value, p3);
            BindTuple<P2>::bind(*this, ::boost::tuples::length<P1>::value, p2);
            BindTuple<P1>::bind(*this, 0, p1);
            execute();
        }
    }

} // namespace SQLITE

#endif
