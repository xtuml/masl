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

#ifndef Sql_AssignerStateFactory_HH
#define Sql_AssignerStateFactory_HH

#include "AssignerStateImpl.hh"

#include "boost/shared_ptr.hpp"

namespace SQL {

// *********************************************************************
//! @brief Factory class to hold the assigner state implementation.
//!
//! The assigner state implementation is hidden behide the AssignerStateImpl
//! interface. This allows the databse implementation to decide on the best
//! storage stragery based on the database technology. Note that this factory
//! class takes ownership of the the registered implementation.
//!
// *********************************************************************
class AssignerStateFactory
{
   public:
     static AssignerStateFactory& singleton();
     
     bool registerImpl(const boost::shared_ptr<AssignerStateImpl>& impl);

     boost::shared_ptr<AssignerStateImpl>& getImpl();

   private:
       AssignerStateFactory();
      ~AssignerStateFactory();

   private:
      AssignerStateFactory(const AssignerStateFactory& rhs);
      AssignerStateFactory& operator=(const AssignerStateFactory& rhs);

   private:
     boost::shared_ptr<AssignerStateImpl> impl_;
};

} // end namespace SQL

#endif
