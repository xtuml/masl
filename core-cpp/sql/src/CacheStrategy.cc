//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
#include <limits>
#include "sql/Exception.hh"
#include "sql/CacheStrategy.hh"

#include "swa/CommandLine.hh"

#include "boost/tuple/tuple.hpp"

namespace SQL {

namespace {

const std::string SQL_CACHE_DISABLE  ("-sqlcache-disable");

bool registerCommandLine()
{       
    SWA::CommandLine::getInstance().registerOption (SWA::NamedOption(SQL_CACHE_DISABLE,  "disable sql object caching",false));
    return true;
}
bool registerCmdLine = registerCommandLine();

// *************************************************
// *************************************************
class DisableCacheStrategy : public CacheStrategy
{

   public:
     DisableCacheStrategy(){}
    ~DisableCacheStrategy(){}

     virtual std::string  getName             () const {  return "DisableCacheStrategy"; }
     virtual uint32_t     getOperationalCount (const uint32_t population) const {  return 0; } 

     virtual bool allowLinearFind  (const uint32_t population) const { return false; }
     virtual bool allowFullCaching (const uint32_t population) const { return false; }

     DisableCacheStrategy* clone()  const { return new DisableCacheStrategy(*this); }
};

// *************************************************
// *************************************************
class DefaultCacheStrategy : public CacheStrategy
{

   public:
     DefaultCacheStrategy(){}
    ~DefaultCacheStrategy(){}

     virtual std::string  getName             () const {  return "DefaultCacheStrategy"; }
     virtual uint32_t     getOperationalCount (const uint32_t population) const {  return population; } 

     virtual bool allowLinearFind  (const uint32_t population) const { return true; }
     virtual bool allowFullCaching (const uint32_t population) const { return true; }

     DefaultCacheStrategy* clone()  const { return new DefaultCacheStrategy(*this); }
};

}

// *************************************************
// *************************************************
CacheStrategyFactory::CacheStrategyFactory(): 
   disabled_(false)
{
}

// *************************************************
// *************************************************
CacheStrategyFactory::~CacheStrategyFactory()
{

}

// *************************************************
// *************************************************
CacheStrategyFactory& CacheStrategyFactory::singleton()
{
   static CacheStrategyFactory instance;
   return instance;
}

// *************************************************
// *************************************************
bool CacheStrategyFactory::registerStrategy(const std::string& domain, const std::string& object, const boost::shared_ptr<CacheStrategy> loadStratergy)
{
   std::string key(domain + "::" + object);   
   definedStratergyDb_[key] = loadStratergy;
   return true;
}
       
// *************************************************
// *************************************************
boost::shared_ptr<CacheStrategy> CacheStrategyFactory::getStrategy(const std::string& domain, const std::string& object)
{
   boost::shared_ptr<CacheStrategy> requiredStrategy(new DefaultCacheStrategy);
   if(disabled_ == true || SWA::CommandLine::getInstance().optionPresent(SQL_CACHE_DISABLE)){
      disabled_ = true;
      requiredStrategy  = boost::shared_ptr<CacheStrategy>(new DisableCacheStrategy);   
   }
   else{      
      // check and see if a specific stratergy has 
      // been associated with the specified  object.
      std::string key(domain + "::" + object); 
      DefinedStratergyDbType::iterator  definedItr = definedStratergyDb_.find(key);
      if (definedItr !=  definedStratergyDb_.end()){
          requiredStrategy  = boost::shared_ptr<CacheStrategy>(definedItr->second->clone());
      }
   }
   return requiredStrategy;
}

} // end namepsace SQL
