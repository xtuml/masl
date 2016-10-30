//============================================================================//
// UK Crown Copyright (c) 2008. All rights reserved.
//
// File:   AssignerStateImpl.hh
//
//============================================================================//
#ifndef Sql_AssignerStateImpl_HH
#define Sql_AssignerStateImpl_HH

#include <string>
#include <vector>
#include <stdint.h>

namespace SQL {

class AssignerStateImpl
{
   protected:
       AssignerStateImpl(){}

   public:
      virtual ~AssignerStateImpl(){}

       // ********************************************************************
       //! Initialise the assigner state mapper. 
       //!
       //! throws SqlException on database access error
       //!
       // ********************************************************************
       virtual std::vector< std::pair<std::string,int32_t> > initialise() = 0;

       // ********************************************************************
       //! Executes an SQL UPDATE statement to store the key value pair in the 
       //! assigner state table.
       //!
       //! throws SqlException on database access error
       //!
       // ********************************************************************
       virtual void updateState(const std::string& objectKey, const int32_t currentState) = 0;

       // ********************************************************************
       //! Executes an SQL INSERT statement to store the key value pair in the 
       //! assigner state table.
       //!
       //! throws SqlException on database access error
       //!
       // ********************************************************************
       virtual void insertState(const std::string& objectKey, const int32_t currentState) = 0;

   private:
      AssignerStateImpl(const AssignerStateImpl& rhs);
      AssignerStateImpl& operator=(const AssignerStateImpl& rhs);
};


}  // end namespace SQL

#endif
