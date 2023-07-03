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
