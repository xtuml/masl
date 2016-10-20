//============================================================================//
// UK Crown Copyright (c) 2008. All rights reserved.
//
// File:   AssignerStateFactory..hh
//
//============================================================================//
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
