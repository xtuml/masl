//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
#ifndef Logging_FilterChannel_HH
#define Logging_FilterChannel_HH

#include "Poco/Message.h"
#include "Poco/Channel.h"
#include <boost/function.hpp>
#include <iostream>

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
