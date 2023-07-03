/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ----------------------------------------------------------------------------
 * Classification: UK OFFICIAL
 * ----------------------------------------------------------------------------
 */

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
