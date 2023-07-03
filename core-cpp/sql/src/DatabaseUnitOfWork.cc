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

#include <functional>
#include <algorithm>

#include "sql/Database.hh"
#include "sql/Exception.hh"
#include "sql/ResourceMonitor.hh"
#include "sql/UnitOfWorkObserver.hh"
#include "sql/DatabaseUnitOfWork.hh"

#include "boost/bind/bind.hpp"
using namespace boost::placeholders;

namespace SQL {


// **********************************************************
// **********************************************************
DatabaseUnitOfWork::DatabaseUnitOfWork(Database& database):
        database_(database)
{

}

// **********************************************************
// **********************************************************
DatabaseUnitOfWork::~DatabaseUnitOfWork()
{

}

// **********************************************************
// **********************************************************
void DatabaseUnitOfWork::clearObservers()
{
   observerList_.clear();
}

// **********************************************************
// **********************************************************
void DatabaseUnitOfWork::startTransaction  ()
{
   notifyStart();
}

// **********************************************************
// **********************************************************
void DatabaseUnitOfWork::commitTransaction ()
{
  notifyCommit();
}

// **********************************************************
// **********************************************************
void DatabaseUnitOfWork::abortTransaction ()
{
  notifyAbort();
}

// **********************************************************
// **********************************************************
void DatabaseUnitOfWork::committed ()
{
  notifyCommitted();
}

// **********************************************************
// **********************************************************
void DatabaseUnitOfWork::flushObserver(::SQL::UnitOfWorkObserver* const observer)
{
  // store the SQL modification statements (INSERT/UPDATE/DELETE) 
  // from the specified observer object.
  std::string statements;

  ::SQL::UnitOfWorkContext context(statements);
  observer->flush(context);

  // If the observer returned any changes then apply them to the database.
  if (statements.empty() == false){
      if (database_.executeStatement(statements) == false){
          throw SqlException(std::string("DatabaseUnitOfWork::flushObserver : failed to flush statements : ") + statements);
      }
  }

  // As the observer is clean, deregister it from this object. The observer will register 
  // register itself again should it have further modifications to apply to the database.
  deregisterObserver(observer);
}

// **********************************************************
// **********************************************************
void DatabaseUnitOfWork::registerDirtyObserver (::SQL::UnitOfWorkObserver* const observer)
{
  observerList_.insert(observer);
}

// **********************************************************
// **********************************************************
void DatabaseUnitOfWork::deregisterObserver (::SQL::UnitOfWorkObserver* const observer)
{
  observerList_.erase(observer);
}

// **********************************************************
// **********************************************************
void DatabaseUnitOfWork::notifyCommit()
{
   std::string statements;
   ::SQL::UnitOfWorkContext context(statements);
   std::for_each(observerList_.begin(),observerList_.end(),boost::bind(&::SQL::UnitOfWorkObserver::commitTransaction,_1,context));
   if (statements.empty() == false){
       if (database_.executeStatement(statements) == false){
           throw SqlException(std::string("DatabaseUnitOfWork::notify_commit : failed to commit statements : ") + statements);
       }
   }

   // Even though statement string may be empty, may have undertaken prepared statements
   // on dirty objects. Therefore always notify of the commit so objects taking part in
   // any prepared statements can be marked as clean.
   notifyCommitted();
}

// **********************************************************
// **********************************************************
void DatabaseUnitOfWork::notifyStart()
{
   std::string statements;
   ::SQL::UnitOfWorkContext context(statements);
   std::for_each(observerList_.begin(),observerList_.end(),boost::bind(&::SQL::UnitOfWorkObserver::startTransaction,_1,boost::ref(context)));
}

// **********************************************************
// **********************************************************
void DatabaseUnitOfWork::notifyAbort()
{
   std::string statements;
   ::SQL::UnitOfWorkContext context(statements);
   std::for_each(observerList_.begin(),observerList_.end(),boost::bind(&::SQL::UnitOfWorkObserver::abortTransaction,_1,boost::ref(context)));
   observerList_.clear();
}

// **********************************************************
// **********************************************************
void DatabaseUnitOfWork::notifyCommitted()
{
   std::string statements;
   ::SQL::UnitOfWorkContext context(statements);
   std::for_each(observerList_.begin(),observerList_.end(),boost::bind(&::SQL::UnitOfWorkObserver::committed,_1,boost::ref(context)));   
   observerList_.clear();

   ResourceMonitor::singleton().committed();
}

} // end namespace SQLITE
