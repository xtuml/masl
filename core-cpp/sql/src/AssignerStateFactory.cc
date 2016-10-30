//============================================================================//
// UK Crown Copyright (c) 2008. All rights reserved.
//
// File:   AssignerStateFactory.cc
//
//============================================================================//
#include "sql/AssignerStateFactory.hh"

namespace SQL {

// **********************************************************************
// **********************************************************************
AssignerStateFactory& AssignerStateFactory::singleton()
{
  static AssignerStateFactory instance;
  return  instance;
}

// **********************************************************************
// **********************************************************************
AssignerStateFactory::AssignerStateFactory()
{

}

// **********************************************************************
// **********************************************************************
AssignerStateFactory::~AssignerStateFactory()
{

}
     
// **********************************************************************
// **********************************************************************
bool AssignerStateFactory::registerImpl(const boost::shared_ptr<AssignerStateImpl>& impl)
{ 
  impl_ = impl; 
  return true;
}

// **********************************************************************
// **********************************************************************
boost::shared_ptr<AssignerStateImpl>&  AssignerStateFactory::getImpl() 
{ 
 return impl_; 
}

} // end namespace SQL
