/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#include "Transaction.hh"
#include "sqlite/Database.hh"

#include "swa/Process.hh"
#include <string>

namespace SQLITE {

    bool init = Transaction::initialise();

    bool Transaction::initialise() {
        getInstance();
        return true;
    }

    Transaction::Transaction() {
        SWA::Process::getInstance().registerThreadStartedListener([this](const std::string &name) {
            startTransaction(name);
        });
        SWA::Process::getInstance().registerThreadCompletingListener([this]() {
            committingTransaction();
        });
        SWA::Process::getInstance().registerThreadCompletedListener([this]() {
            commitTransaction();
        });
        SWA::Process::getInstance().registerThreadAbortedListener([this]() {
            abortTransaction();
        });
    }

    void Transaction::startTransaction(const std::string &name) {
        Database::singleton().startTransaction(name);
    }

    void Transaction::committingTransaction() {
        Database::singleton().committingTransaction();
    }

    void Transaction::commitTransaction() {
        Database::singleton().commitTransaction();
    }

    void Transaction::abortTransaction() {
        Database::singleton().abortTransaction();
    }

    Transaction &Transaction::getInstance() {
        static Transaction instance;
        return instance;
    }

} // end namespace SQLITE
