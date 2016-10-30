//============================================================================//
// UK Crown Copyright (c) 2008. All rights reserved.
//
// File:   Database.hh
//
//============================================================================//
#ifndef Sql_Database_HH
#define Sql_Database_HH

#include "Schema.hh"
#include "StatementFormatter.hh"
#include "DatabaseUnitOfWork.hh"

#include "boost/shared_ptr.hpp"

namespace SQL {

class DatabaseUnitOfWork;
class Database
{
   public:
       virtual ~Database() {}

       DatabaseUnitOfWork& getCurrentUnitOfWork() { return unitOfWork; }

       virtual void close    () = 0;
       virtual void destroy  () = 0;
       virtual void shutdown () = 0;

       virtual bool executeStatement  (const std::string& statement) = 0; 

       virtual void abortTransaction       () = 0;
       virtual void committingTransaction  () = 0;
       virtual void commitTransaction      () = 0;
       virtual void startTransaction  (const std::string& iName) = 0;

       virtual std::string normalise(const std::string& statement) const = 0;

       virtual boost::shared_ptr<AbstractStatementFactory> getStatementFormatter() = 0;   

   protected:
       Database():unitOfWork(*this) {}

   private:
      Database(const Database& rhs);
      Database& operator=(const Database& rhs);

   protected:
     DatabaseUnitOfWork unitOfWork;
};

}  // end SQL namespace

#endif
