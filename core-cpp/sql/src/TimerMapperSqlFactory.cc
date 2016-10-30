//============================================================================//
// UK Crown Copyright (c) 2008. All rights reserved.
//
// File:   TimerMapperSqlFactory.cc
//
//============================================================================//

#include "sql/TimerMapperSqlFactory.hh"
namespace SQL {

// **********************************************************************
// **********************************************************************
TimerMapperSqlFactory& TimerMapperSqlFactory::singleton()
{
  static TimerMapperSqlFactory instance;
  return  instance;
}

// **********************************************************************
// **********************************************************************
TimerMapperSqlFactory::TimerMapperSqlFactory()
{

}

// **********************************************************************
// **********************************************************************
TimerMapperSqlFactory::~TimerMapperSqlFactory()
{

}
     
// **********************************************************************
// **********************************************************************
bool TimerMapperSqlFactory::registerImpl(const boost::shared_ptr<TimerMapperSql>& impl)
{ 
  impl_ = impl; 
  return true;
}

// **********************************************************************
// **********************************************************************
boost::shared_ptr<TimerMapperSql>&  TimerMapperSqlFactory::getImpl() 
{ 
 return impl_; 
}

} // end namespace SQL
