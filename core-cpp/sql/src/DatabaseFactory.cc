//============================================================================//
// UK Crown Copyright (c) 2008. All rights reserved.
//
// File:   DatabaseFactory.cc
//
//============================================================================//

#include "sql/DatabaseFactory.hh"

namespace SQL {

// ********************************************************
// ********************************************************
DatabaseFactory& DatabaseFactory::singleton()
{
  static DatabaseFactory instance;
  return instance;
}

// ********************************************************
// ********************************************************
DatabaseFactory::DatabaseFactory():
  impl_(0)
{

}

// ********************************************************
// ********************************************************
DatabaseFactory::~DatabaseFactory()
{

}

} // end namespace SQL
