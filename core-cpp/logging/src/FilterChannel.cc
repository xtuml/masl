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

#include "FilterChannel.hh"
#include "Poco/Message.h"
#include "Poco/LoggingRegistry.h"


namespace Logging {


  FilterChannel::FilterChannel()
    : _filter(PriorityFilter()), 
      _pChannel(0)
  {
  }


  FilterChannel::FilterChannel(const Filter& filter): 
    _filter(filter), 
    _pChannel(0)
  {
  }


  FilterChannel::FilterChannel(const Filter& filter, Poco::Channel* pChannel): 
    _filter(filter), 
    _pChannel(pChannel)
  {
    if (_pChannel)   _pChannel->duplicate();
  }


  FilterChannel::~FilterChannel()
  {
    if (_pChannel)   _pChannel->release();
  }


  void FilterChannel::setFilter(const Filter& filter)
  {
    _filter = filter;
  }


  const FilterChannel::Filter& FilterChannel::getFilter() const
  {
    return _filter;
  }


  void FilterChannel::setChannel(Poco::Channel* pChannel)
  {
    if (_pChannel) _pChannel->release();
    _pChannel = pChannel;
    if (_pChannel) _pChannel->duplicate();
  }


  Poco::Channel* FilterChannel::getChannel() const
  {
    return _pChannel;
  }


  void FilterChannel::log(const Poco::Message& msg)
  {
    if (_pChannel)
    {
      if (_filter(msg))
      {
        _pChannel->log(msg);
      }
    }
  };


  void FilterChannel::setProperty(const std::string& name, const std::string& value)
  {
    if (name == "channel")
      setChannel(Poco::LoggingRegistry::defaultRegistry().channelForName(value));
    else if (_pChannel)
      _pChannel->setProperty(name, value);
  }


  void FilterChannel::open()
  {
    if (_pChannel)
      _pChannel->open();
  }


  void FilterChannel::close()
  {
    if (_pChannel)
      _pChannel->close();
  }


} // namespace Poco
