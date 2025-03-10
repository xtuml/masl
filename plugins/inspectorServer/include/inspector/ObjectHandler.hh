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

#ifndef Inspector_ObjectHandler_HH
#define Inspector_ObjectHandler_HH

#include "GenericObjectHandler.hh"
#include "CommunicationChannel.hh"
#include <iostream>
#include <vector>
#include "swa/Set.hh"
#include "swa/ObjectPtr.hh"

namespace Inspector
{

  template<class Object>
  class ObjectHandler : public GenericObjectHandler
  {
    public:
      virtual void writePopulation ( CommunicationChannel& channel ) const
      {
        writeInstances( channel, Object::findAll(), Object::getPopulationSize() );
      }

      virtual void writeRelatedInstances ( CommunicationChannel& channel, SWA::IdType id, int relId ) const
      {
        writeRelatedInstances(channel,Object::getInstance(id),relId);
      }

      virtual void writeSelectedInstances ( CommunicationChannel& channel, const std::vector<SWA::IdType>& ids) const
      {
        writeInstances(channel, ids);
      }

      virtual void writeInstance ( CommunicationChannel& channel, ::SWA::IdType id ) const
      {
        writeInstance(channel, Object::getInstance(id));
      }

      virtual void createInstance ( CommunicationChannel& channel ) const = 0;

      virtual void deleteInstance ( CommunicationChannel& channel, ::SWA::IdType id ) const
      {
        Object::getInstance(id)->deleteInstance();
      }

      virtual void writeRelatedInstances ( CommunicationChannel& channel, ::SWA::ObjectPtr<Object> instance,  int relId ) const = 0;

      void writeInstance ( CommunicationChannel& channel, ::SWA::ObjectPtr<Object> instance ) const;

      void writeInstances ( CommunicationChannel& channel, ::SWA::ObjectPtr<Object> instance ) const;

      template<class Collection>
      void writeInstances ( CommunicationChannel& channel, const Collection& instances ) const
      {
        writeInstances(channel,instances,instances.size());
      }

      template<class Collection>
      void writeInstances ( CommunicationChannel& channel, const Collection& instances, int collectionSize ) const;

      virtual int getCardinality() const
      {
        return Object::getPopulationSize();
      }

      virtual std::string getIdentifierText ( ::SWA::IdType instanceId ) const
      {
        SWA::ObjectPtr<Object> instance = Object::getInstance(instanceId);

        if ( instance ) return getIdentifierText(instance);
        else return "!! Deleted !!";
      }

      virtual std::string getIdentifierText ( ::SWA::ObjectPtr<Object> instance ) const = 0;
  };

  template<class Object>
  template<class Collection>
  void ObjectHandler<Object>::writeInstances ( CommunicationChannel& channel, const Collection& instances, int collectionSize ) const
  {
    channel << collectionSize;
    channel.flush();

    bool cancel = false;
    for ( typename Collection::const_iterator it = instances.begin(), end = instances.end();
         !cancel && it != end;
          ++it )
    {
      channel << true; // More to come
      writeInstance(channel, *it);

      if ( channel.ready() ) 
        channel >> cancel;
    }

    channel << false; // No more
    channel.flush();

    // Wait for cancel requests to stop
    cancel = true;
    while ( cancel ) channel >> cancel;
  }

  template<class Object>
  void ObjectHandler<Object>::writeInstances ( CommunicationChannel& channel, ::SWA::ObjectPtr<Object> instance ) const
  {
    std::vector< ::SWA::ObjectPtr<Object> > instances;
    if ( instance ) instances.push_back(instance);
    writeInstances(channel,instances);
  }


  template<class Object>
  void ObjectHandler<Object>::writeInstance ( CommunicationChannel& channel, ::SWA::ObjectPtr<Object> instance ) const
  {
    if ( instance )
    {
      channel << true; // valid instance
      channel << *instance;
    }
    else
    {
      channel << false; // invalid instance
    }
  }

}

#endif
