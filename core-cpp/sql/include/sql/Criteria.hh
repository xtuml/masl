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

#ifndef Sql_Criteria_HH
#define Sql_Criteria_HH

#include <string>
#include <stdint.h>

#include "Util.hh"

namespace SQL {

// *****************************************************************
//! @brief Class to produce user defined SQL querys
//!
//! The MASL find operations undertaken in a domain are abstracted out into
//! interface operations on the MASL objects generated Population class; rather
//! than being implemented as a linear lookup loop at the call site of the find.
//! For the SQL implementation the find condition has to be translated into
//! a SQL WHERE clause, the Criteria class is therefore used in the generated
//! c++ code to facilite this translation. 
//! 
//! The interface for this class provides the minimum required to support the
//! generated SQL implementation code, it is not a fully functional Critiera 
//! class.
//!
//! Although the Critera class may be created in the find implementation it 
//! might not be used because the implementation may determine that a linear 
//! lookup, using the generated find predicate, on the cached object 
//! population may give better performance.
// *****************************************************************

#include <string>

#include "boost/shared_ptr.hpp"

// *****************************************************************
// *****************************************************************
class CriteriaImpl
{
   protected:
      CriteriaImpl() {}

   public:
      virtual ~CriteriaImpl(){}

      virtual void setLimit       (const int32_t limit)           = 0;
      virtual void addColumn      (const std::string& columnName) = 0;
      virtual void addFromClause  (const std::string& tableName)  = 0;
      virtual void addWhereClause (const std::string& where)      = 0;

      virtual bool empty         () const = 0;
      virtual void addAllColumn  () = 0;
      virtual void addAllColumns (const std::string& tableName) = 0;

      virtual std::string selectStatement() const = 0;
      
   private:
      CriteriaImpl(const CriteriaImpl& rhs);
      CriteriaImpl& operator=(const CriteriaImpl& rhs);
};


// *****************************************************************
// *****************************************************************
class Criteria 
{  
   public:
      // *****************************************************************
      // ! CONSTRUCTOR
      // *****************************************************************
      Criteria();

      // *****************************************************************
      // ! DESTRUCTOR
      // *****************************************************************
     ~Criteria();

      // *****************************************************************
      //! Enable the number of rows returned by the where clause to be limited
      //! by the required amount using the SQL LIMIT statement as part of the 
      //! generated where clause.
      //! 
      //! @param limit the maximum number of rows to return.
      // *****************************************************************
      void setLimit (const int32_t limit);

      // *****************************************************************
      //! Add a column name to the list of columns the query should return.
      //! 
      //! @param columnName the name of the column
      // *****************************************************************
      void addColumn (const std::string& columnName);

      // *****************************************************************
      //! Add the all column token to the list of columns the query should 
      //! return.
      // *****************************************************************
      void addAllColumn ();

      // *****************************************************************
      //! Add all the columns from the specified table to the criteria. 
      //! 
      //! @param tableName the name of the table
      // *****************************************************************
      void addAllColumns (const std::string& tableName);

      // *****************************************************************
      //! Add a column name to the list of columns the query should return.
      //! 
      //! @param tableName the name of the table
      // *****************************************************************
      void addFromClause (const std::string& tableName);

      // *****************************************************************
      //! Add a fully formed where clause condition to the select statement. 
      //! The passed in condition should not include the 'WHERE' text or a 
      //! terminating semi-colon. This method can be called multiple times.
      //!  
      //! @param where the where clause condition
      // *****************************************************************
      void addWhereClause (const std::string& where);

      // *****************************************************************
      //! @return the fully formed SQL select statement
      // *****************************************************************
      std::string selectStatement() const;
      
      // *****************************************************************
      //! @return true if criteria is empty and should not be used in query
      // *****************************************************************
      bool empty() const;

   private:
      boost::shared_ptr<CriteriaImpl> impl_;
};

} // end namespace SQL

#endif
