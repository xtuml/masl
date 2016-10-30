//============================================================================//
// UK Crown Copyright (c) 2007. All rights reserved.
//
// File:   Transaction.cc
//
//============================================================================//

#include "sqlite/Database.hh"
#include "Transaction.hh"

#include "swa/Process.hh"

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
