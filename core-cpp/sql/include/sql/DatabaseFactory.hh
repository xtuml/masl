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

#ifndef Sql_DatabaseFactory_HH
#define Sql_DatabaseFactory_HH

#include "Database.hh"

namespace SQL {

// *****************************************************************
//! @brief Factory class to hold the database implementation
//!
//! This factory class will be populated with the required database 
//! implementation using some form of static registration. The sql 
//! framework will use this instance for all of its database access.
//!
//! Note that this factory class does not use any kind of smart pointer 
//! to manage the registered databse implementation. This is done so
//! that the implementation can maintain ownership of the supplied database 
//! instance.
// *****************************************************************
class DatabaseFactory
{
   public:
     // ****************************************************
     //! Return the single instance of this factory.
     // ****************************************************
     static DatabaseFactory& singleton();
     
     // ****************************************************
     //! Register the database implementation that should be
     //! used by the sql framework. Only one registration should
     //! be undertaken. This factory will not take ownership of
     //! the supplied database instance. It is upto the supplying
     //! to manage this resource.
     // ****************************************************
     bool registerImpl(Database* impl){ impl_ = impl; return true; }

     // ****************************************************
     //! @return the registered database implementation 
     // ****************************************************
     Database& getImpl() { return *impl_; }

   private:
      DatabaseFactory(const DatabaseFactory& rhs);
      DatabaseFactory& operator=(const DatabaseFactory& rhs);

   private:
      DatabaseFactory();
     ~DatabaseFactory();

   private:
      Database* impl_;
};

} // end namespace SQL

#endif
