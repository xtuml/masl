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

#ifndef Logging_FilterChannel_HH
#define Logging_FilterChannel_HH

#include "Poco/Message.h"
#include "Poco/Channel.h"
#include <boost/function.hpp>
#include <iostream>
#include <limits>

namespace Logging
{

  class PriorityFilter
  {
    public:
      PriorityFilter ( int minPriority = std::numeric_limits<int>::max(), int maxPriority = std::numeric_limits<int>::min() )
        : minPriority(minPriority),
          maxPriority(maxPriority)
      {
      }
      
      bool operator() ( const Poco::Message& message ) const
      {
        return message.getPriority() <= minPriority && message.getPriority() >= maxPriority;
      }

    private:
      int minPriority;
      int maxPriority;

  };

  class FilterChannel: public Poco::Channel
  {
    public:
      typedef boost::function<bool(const Poco::Message&)> Filter;        


      FilterChannel();

      FilterChannel(const Filter& filter);

      FilterChannel(const Filter& filter, Poco::Channel* pChannel);

      void setFilter(const Filter& filter);

      const Filter& getFilter() const;

      void setChannel(Poco::Channel* pChannel);

      Poco::Channel* getChannel() const;

      void log(const Poco::Message& msg);

      void setProperty(const std::string& name, const std::string& value);

      void open();

      void close();

    protected:
	  ~FilterChannel();

    private:
	  Filter _filter;
	  Poco::Channel* _pChannel;
  };


}

#endif
