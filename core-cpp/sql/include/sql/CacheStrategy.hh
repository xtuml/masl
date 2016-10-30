//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
#ifndef Sql_CacheStrategy_HH
#define Sql_CacheStrategy_HH

#include <map>
#include <stdint.h>
#include "boost/shared_ptr.hpp"

namespace SQL {

// *************************************************
// *************************************************
class CacheStrategy
{
   public:
       virtual ~CacheStrategy(){}

       virtual std::string  getName             () const = 0;
       virtual uint32_t     getOperationalCount (const uint32_t population) const = 0;

       virtual bool allowLinearFind  (const uint32_t population) const = 0;
       virtual bool allowFullCaching (const uint32_t population) const = 0;

       virtual CacheStrategy* clone()  const = 0;

   protected:
       CacheStrategy(){}
};

// *************************************************
// *************************************************
class CacheStrategyFactory
{
    public:
       static CacheStrategyFactory& singleton();

       bool registerStrategy(const std::string& domain, const std::string& object, const boost::shared_ptr<CacheStrategy> CacheStrategy);
       
       boost::shared_ptr<CacheStrategy> getStrategy(const std::string& domain, const std::string& object);

    private:
       CacheStrategyFactory();
      ~CacheStrategyFactory();

       CacheStrategyFactory(const CacheStrategyFactory& rhs);
       CacheStrategyFactory& operator =(const CacheStrategyFactory& rhs);

    private:
        typedef std::map<std::string,  boost::shared_ptr<CacheStrategy> > DefinedStratergyDbType;

        DefinedStratergyDbType definedStratergyDb_;

        bool disabled_;
};

} // end namepsace SQL
#endif
