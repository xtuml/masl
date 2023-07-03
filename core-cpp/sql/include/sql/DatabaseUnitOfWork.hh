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

#ifndef Sql_DatabaseUnitOfWork_HH
#define Sql_DatabaseUnitOfWork_HH

#include <set>

namespace SQL {

class Database;
class UnitOfWorkObserver;

// *****************************************************************
//! \brief A class to store all changes for the current transaction.
//!
//! The database will require a series of operations to be performed upon it
//! as threads of control are executed. Rather than applying these changes
//! direclty as they occur, which can be very inefficent, the changes 
//! can be stored and applied in one block near the end of the transaction. 
//! This DatabaseUnitOfWork class therefore stores the handles to observer 
//! objects that have changes that need to be applied to the database. 
//! Just before the database undertakes a transaction commit it will use 
//! an instance of this class to get all the changes that need to be applied 
//! from the list of registered observers. Once there changes have been 
//! applied the observers are informed and deregister.
// *****************************************************************
class DatabaseUnitOfWork
{
   public:

      // *****************************************************************
      //! Constructor    
      //! 
      //! \param database the parent database associated with this object.
      // *****************************************************************
      DatabaseUnitOfWork(Database& database);
     ~DatabaseUnitOfWork();

      // *****************************************************************
      //! Remove any registered observers. This will be called by the associated
      //! database during shutdown or re-initialisation   
      // *****************************************************************
      void clearObservers();

      // *****************************************************************
      //! Obtain all the changes that need to be applied to the database   
      //! from the specified observer object, in the form of SQL statements. 
      //! Apply these changes to the database and then inform the observer
      //! that this has been successful. At the end of the flush this observer 
      //! is essentially clean (has no changes) so it can be deregistered from 
      //! this unit of work object.
      //! 
      //! \param observer the observer object to perform operations upon.
      // *****************************************************************
      void flushObserver (UnitOfWorkObserver* const observer);

      // *****************************************************************
      //! An observer has some changes that need to be applied to the database,
      //! therefore store its handle so that at the next database commit the   
      //! registered observer can be querried for the changes it wishes to apply.
      //! 
      //! \param observer the observer object to perform operation upon.
      // *****************************************************************
      void registerDirtyObserver (UnitOfWorkObserver* const observer);

      // *****************************************************************
      //! Remove the specified observer from the dirty observer list. 
      //! 
      //! \param observer the observer object to perform operations upon.
      // *****************************************************************
      void deregisterObserver (UnitOfWorkObserver* const observer);

      // *****************************************************************
      //! Inform Observers of a start transaction 
      // *****************************************************************
      void startTransaction ();

      // *****************************************************************
      //! Inform Observers of a transaction commit
      // *****************************************************************
      void commitTransaction ();

      // *****************************************************************
      //! Inform Observers of an abort transaction
      // *****************************************************************
      void abortTransaction ();

      // *****************************************************************
      //! Inform Observers that their changes have been successfully committed
      // *****************************************************************
      void committed ();

    protected:
      // *****************************************************************
      //! Constructor    
      // *****************************************************************
      DatabaseUnitOfWork();

    private:
         void notifyCommit();
         void notifyStart();
         void notifyAbort();
         void notifyCommitted();

    private:
       DatabaseUnitOfWork(const DatabaseUnitOfWork& rhs);
       DatabaseUnitOfWork& operator=(const DatabaseUnitOfWork& rhs);

    private:
       Database& database_;

       std::set< UnitOfWorkObserver*> observerList_;

};

} // end namespace SQL

#endif

