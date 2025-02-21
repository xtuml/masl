/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#ifndef Sql_AssignerStateFactory_HH
#define Sql_AssignerStateFactory_HH

#include "AssignerStateImpl.hh"

#include <memory>

namespace SQL {

// *********************************************************************
//! @brief Factory class to hold the assigner state implementation.
//!
//! The assigner state implementation is hidden behide the AssignerStateImpl
//! interface. This allows the databse implementation to decide on the best
//! storage stragery based on the database technology. Note that this factory
//! class takes ownership of the the registered implementation.
//!
// *********************************************************************
class AssignerStateFactory {
  public:
    static AssignerStateFactory &singleton();

    bool registerImpl(const std::shared_ptr<AssignerStateImpl> &impl);

    std::shared_ptr<AssignerStateImpl> &getImpl();

  private:
    AssignerStateFactory();
    ~AssignerStateFactory();

  private:
    AssignerStateFactory(const AssignerStateFactory &rhs);
    AssignerStateFactory &operator=(const AssignerStateFactory &rhs);

  private:
    std::shared_ptr<AssignerStateImpl> impl_;
};

} // end namespace SQL

#endif
