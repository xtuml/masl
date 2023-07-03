/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ----------------------------------------------------------------------------
 * Classification: UK OFFICIAL
 * ----------------------------------------------------------------------------
 */

#include "sqlite/Database.hh"
#include "Transaction.hh"

#include "swa/Process.hh"
using namespace boost::placeholders;

namespace SQLITE {

  bool init = Transaction::initialise();

  bool Transaction::initialise()
  {
    getInstance();
    return true;
  }

  Transaction::Transaction()
  {
    SWA::Process::getInstance().registerThreadStartedListener(boost::bind(&Transaction::startTransaction,this,_1));
    SWA::Process::getInstance().registerThreadCompletingListener(boost::bind(&Transaction::committingTransaction,this));
    SWA::Process::getInstance().registerThreadCompletedListener(boost::bind(&Transaction::commitTransaction,this));
    SWA::Process::getInstance().registerThreadAbortedListener(boost::bind(&Transaction::abortTransaction,this));
  }

  void Transaction::startTransaction( const std::string& name )
  {
      Database::singleton().startTransaction(name);
  }

  void Transaction::committingTransaction()
  {
      Database::singleton().committingTransaction();
  }

  void Transaction::commitTransaction()
  {
      Database::singleton().commitTransaction();
  }

  void Transaction::abortTransaction()
  {
      Database::singleton().abortTransaction();
  }

  Transaction& Transaction::getInstance()
  {
    static Transaction instance;
    return instance;
  }

} // end namespace SQLITE
