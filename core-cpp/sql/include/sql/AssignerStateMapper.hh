//============================================================================//
// UK Crown Copyright (c) 2007. All rights reserved.
//
// File:   AssignerStateMapper.hh
//
//============================================================================//
#ifndef Sql_AssignerStateMapper_HH
#define Sql_AssignerStateMapper_HH

#include <map>
#include <string>
#include <stdint.h>

#include "Exception.hh"
#include "AssignerStateImpl.hh"

#include "boost/shared_ptr.hpp"

namespace SQL {

// ********************************************************************
//! @brief Class to store the states associated with assigner state models
//! 
//! The assigner state models have one state model instance per class type 
//! rather than per class instance. Therefore the current state of an assigner
//! state model is stored and accessed via the generated population classes.
//! Rather than creating a seperate table for each assigner state model which
//! would contain a single row detailing the current state information, all the
//! information for every assigner state model within a process is stored in a 
//! single table that uses the object name for the assigner key and a textual 
//! representation of the current state.
// ********************************************************************
class AssignerStateMapper
{
   public:

      // ********************************************************************
      //! Singleton class to only allow one instance in the process.
      // ********************************************************************
      static AssignerStateMapper& singleton();

      // ********************************************************************
      //! @returns true if the specified key has an associated loaded value.
      // ********************************************************************
      bool isAssignerSet (const std::string& objectKey);

      // ********************************************************************
      //! When database is warm started the assigner states need to be loaded 
      //! and cached. This is a helper method to enable the specified state to 
      //! be cached.
      //! throws SqlException if update fails
      //!
      //! @param objectKey    the unique key for the assigner state model
      //! @param currentState textual representation of the current state
      // ********************************************************************
      void cacheAssignerState(const std::string& objectKey, const int32_t currentState);
    
      // ********************************************************************
      //! Sets the specified assigner state.
      //! throws SqlException if update fails
      //!
      //! @param objectKey    the unique key for the assigner state model
      //! @param currentState textual representation of the current state
      // ********************************************************************
      void setAssignerState(const std::string& objectKey, const int32_t currentState);

      // ********************************************************************
      //!
      //! throws SqlException if key value not found
      //!
      //! @returns the current state for the specified key value.
      // ********************************************************************
      template<class StateType>
      StateType getAssignerState(const std::string& objectKey)
      {
          AssignerStateContType::iterator assignerItr = assignerStates_.find(objectKey);
          if (assignerItr == assignerStates_.end()){
             std::string errMsg("Failed to find assigner state for object : ");
             errMsg += objectKey;
             throw SqlException(errMsg);
          }
          return static_cast<StateType>((*assignerItr).second);        
      }

   private:
       AssignerStateMapper();
      ~AssignerStateMapper();

   private:
       AssignerStateMapper(const AssignerStateMapper& rhs);
       AssignerStateMapper& operator=(const AssignerStateMapper& rhs);

   private:
      typedef std::map<std::string,int32_t> AssignerStateContType;

      boost::shared_ptr<AssignerStateImpl> impl_;
      AssignerStateContType                assignerStates_;

};



} // end namespace SQL

#endif
