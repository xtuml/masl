//============================================================================//
// UK Crown Copyright (c) 2007. All rights reserved.
//
// File:   AssignerStateMapper.cc
//
//============================================================================//

#include <map>
#include <vector>
#include <algorithm>
#include <functional>

#include "sql/Util.hh"
#include "sql/AssignerStateMapper.hh"
#include "sql/AssignerStateFactory.hh"

#include "boost/bind.hpp"

namespace SQL {

// *****************************************************************
// *****************************************************************
AssignerStateMapper& AssignerStateMapper::singleton()
{
    static AssignerStateMapper instance;
    return instance;
}

// *****************************************************************
// *****************************************************************
AssignerStateMapper::AssignerStateMapper():
         impl_(AssignerStateFactory::singleton().getImpl())
{
   typedef std::pair<std::string,int32_t> StatePairType;

   std::vector< StatePairType > stateValues = impl_->initialise();
   std::for_each(stateValues.begin(),stateValues.end(), 
                     boost::bind(&AssignerStateMapper::cacheAssignerState,this,
                        ::boost::bind(PairFirst <StatePairType>(),_1), 
                        ::boost::bind(PairSecond<StatePairType>(),_1)
                     )
                 );
}

// *****************************************************************
// *****************************************************************
AssignerStateMapper::~AssignerStateMapper()
{

}

// *****************************************************************
// *****************************************************************
bool AssignerStateMapper::isAssignerSet (const std::string& objectKey)
{
    return assignerStates_.find(objectKey) != assignerStates_.end();
}

// *****************************************************************
// *****************************************************************
void AssignerStateMapper::cacheAssignerState(const std::string& objectKey, const int32_t currentState)
{
   assignerStates_[objectKey] = currentState;
}

// *****************************************************************
// *****************************************************************
void AssignerStateMapper::setAssignerState(const std::string& objectKey, const int32_t currentState)
{
   if (isAssignerSet(objectKey)){
      impl_->updateState(objectKey,currentState);
   }
   else{
      impl_->insertState(objectKey,currentState);
   }
   cacheAssignerState(objectKey,currentState);
}

} // send SQL namespace
