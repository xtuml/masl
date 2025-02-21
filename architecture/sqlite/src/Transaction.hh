/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#ifndef Sqlite_Transaction_HH
#define Sqlite_Transaction_HH

#include <string>

namespace SQLITE {

class Transaction {
  private:
    Transaction();

    void startTransaction(const std::string &name);
    void committingTransaction();
    void commitTransaction();
    void abortTransaction();

  public:
    static Transaction &getInstance();
    static bool initialise();
};

} // end namespace SQLITE

#endif
