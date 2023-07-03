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

#ifndef Sql_UnitOfWorkObserver_HH
#define Sql_UnitOfWorkObserver_HH

#include <string>

namespace SQL {

// ***********************************************************************
// ***********************************************************************
class UnitOfWorkContext
{
   public:
       UnitOfWorkContext(std::string& statements):statements_(statements){}
      ~UnitOfWorkContext(){}

      std::string&  getStatements() { return statements_; }

   private:
       std::string& statements_;
};


// ***********************************************************************
// ***********************************************************************
class UnitOfWorkObserver
{
   public:
        virtual void flush             (UnitOfWorkContext& context) = 0;
        virtual void committed         (UnitOfWorkContext& context) = 0;
        virtual void startTransaction  (UnitOfWorkContext& context) = 0;
        virtual void commitTransaction (UnitOfWorkContext& context) = 0;
        virtual void abortTransaction  (UnitOfWorkContext& context) = 0;

   private:
       // Disable copy and assignment
       UnitOfWorkObserver(const UnitOfWorkObserver& rhs);
       UnitOfWorkObserver& operator=(const UnitOfWorkObserver& rhs);

   protected:
               UnitOfWorkObserver(){}
      virtual ~UnitOfWorkObserver(){}   // do not allow deletion using a pointer to this base class
};


} // end namepsace SQL

#endif
