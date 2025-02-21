/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#ifndef Sql_AssignerStateImpl_HH
#define Sql_AssignerStateImpl_HH

#include <stdint.h>
#include <string>
#include <vector>

namespace SQL {

class AssignerStateImpl {
  protected:
    AssignerStateImpl() {}

  public:
    virtual ~AssignerStateImpl() {}

    // ********************************************************************
    //! Initialise the assigner state mapper.
    //!
    //! throws SqlException on database access error
    //!
    // ********************************************************************
    virtual std::vector<std::pair<std::string, int32_t>> initialise() = 0;

    // ********************************************************************
    //! Executes an SQL UPDATE statement to store the key value pair in the
    //! assigner state table.
    //!
    //! throws SqlException on database access error
    //!
    // ********************************************************************
    virtual void updateState(const std::string &objectKey,
                             const int32_t currentState) = 0;

    // ********************************************************************
    //! Executes an SQL INSERT statement to store the key value pair in the
    //! assigner state table.
    //!
    //! throws SqlException on database access error
    //!
    // ********************************************************************
    virtual void insertState(const std::string &objectKey,
                             const int32_t currentState) = 0;

  private:
    AssignerStateImpl(const AssignerStateImpl &rhs);
    AssignerStateImpl &operator=(const AssignerStateImpl &rhs);
};

} // end namespace SQL

#endif
