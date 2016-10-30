//============================================================================//
// UK Crown Copyright (c) 2007. All rights reserved.
//
// File:   CriteriaFactory.cc
//
//============================================================================//
#include "sql/CriteriaFactory.hh"

namespace SQL {

// *****************************************************************
// *****************************************************************
CriteriaFactory& CriteriaFactory::singleton()
{
   static CriteriaFactory instance;
   return instance;
}

// *****************************************************************
// *****************************************************************
CriteriaFactory::CriteriaFactory()
{

}

// *****************************************************************
// *****************************************************************
CriteriaFactory::~CriteriaFactory()
{

}

// *****************************************************************
// *****************************************************************
bool CriteriaFactory::registerImpl(const boost::shared_ptr<CloneableCriteria>& impl)
{ 
  impl_ = impl; 
  return true; 
}

// *****************************************************************
// *****************************************************************
boost::shared_ptr<CriteriaImpl> CriteriaFactory::newInstance() 
{ 
  return impl_->clone(); 
}


}  // end namepsace SQL
