/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#ifndef Sql_UnitOfWorkObserver_HH
#define Sql_UnitOfWorkObserver_HH

#include <string>

namespace SQL {

    // ***********************************************************************
    // ***********************************************************************
    class UnitOfWorkContext {
      public:
        UnitOfWorkContext(std::string &statements)
            : statements_(statements) {}
        ~UnitOfWorkContext() {}

        std::string &getStatements() {
            return statements_;
        }

      private:
        std::string &statements_;
    };

    // ***********************************************************************
    // ***********************************************************************
    class UnitOfWorkObserver {
      public:
        virtual void flush(UnitOfWorkContext &context) = 0;
        virtual void committed(UnitOfWorkContext &context) = 0;
        virtual void startTransaction(UnitOfWorkContext &context) = 0;
        virtual void commitTransaction(UnitOfWorkContext &context) = 0;
        virtual void abortTransaction(UnitOfWorkContext &context) = 0;

      private:
        // Disable copy and assignment
        UnitOfWorkObserver(const UnitOfWorkObserver &rhs);
        UnitOfWorkObserver &operator=(const UnitOfWorkObserver &rhs);

      protected:
        UnitOfWorkObserver() {}
        virtual ~UnitOfWorkObserver() {} // do not allow deletion using a pointer to this base class
    };

} // namespace SQL

#endif
