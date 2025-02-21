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

#include "inspector/BufferedIO.hh"
#include "metadata/MetaData.hh"
#include "swa/Stack.hh"
#include "swa/Process.hh"
#include "swa/EventTimer.hh"
#include "swa/PluginRegistry.hh"
#include "inspector/ProcessHandler.hh"
#include "inspector/DomainHandler.hh"
#include "inspector/ObjectHandler.hh"
#include "inspector/EventHandler.hh"

#include <iostream>

namespace Inspector
{
  template<>
  void BufferedOutputStream::write<SWA::TypeMetaData::BasicType> ( const SWA::TypeMetaData::BasicType& type )
  {
    write(static_cast<int32_t>(type));
  }

  template<>
  void BufferedOutputStream::write<SWA::TypeMetaData> ( const SWA::TypeMetaData& type )
  {
    write(type.getBasicType());
    write(type.getCollectionDepth());

    switch ( type.getBasicType() )
    {
      case SWA::TypeMetaData::Instance:
      case SWA::TypeMetaData::Structure:
      case SWA::TypeMetaData::Enumeration:
      case SWA::TypeMetaData::State:
        write(type.getTypeDomain());
        write(type.getTypeId());
        break;
      case SWA::TypeMetaData::Dictionary:
        write(type.getDictionaryKey());
        write(type.getDictionaryValue());
        break;
      default: break;
    }

  }

  template<>
  void BufferedOutputStream::write<SWA::ServiceMetaData::ServiceType> ( const SWA::ServiceMetaData::ServiceType& type )
  {
    write(static_cast<int32_t>(type));
  }

  template<>
  void BufferedOutputStream::write<SWA::ServiceMetaData> ( const SWA::ServiceMetaData& service )
  {
    write(service.getId());
    write(service.getType());
    write(service.getName());
    write(service.getParameters());
    write(service.getLocalVariables());
    write(service.isFunction());
    if ( service.isFunction() )
    {
      write(service.getReturnTypeName());
      write(service.getReturnType());
    }
    write(service.getFileName());
    write(service.getFileHash());
  }

  template<>
  void BufferedOutputStream::write<SWA::RelationshipMetaData> ( const SWA::RelationshipMetaData& rel )
  {
    write(rel.getId());
    write(rel.getName());
    write(rel.getLeftObject());
    write(rel.getRightRole());
    write(rel.getRightMany());
    write(rel.getRightConditional());
    write(rel.getRightObject());
    write(rel.getLeftRole());
    write(rel.getLeftMany());
    write(rel.getLeftConditional());
    write(rel.getIsAssoc());
    if ( rel.getIsAssoc() ) write(rel.getAssocObject());
  }

  template<>
  void BufferedOutputStream::write<SWA::SuperSubtypeMetaData> ( const SWA::SuperSubtypeMetaData& rel )
  {
    write(rel.getId());
    write(rel.getName());
    write(rel.getSuperObject());
    write(rel.getSubObjects());
  }


  template<>
  void BufferedOutputStream::write<SWA::AttributeMetaData> ( const SWA::AttributeMetaData& attribute )
  {
    write(attribute.getName());
    write(attribute.getIdentifier());
    write(attribute.getTypeName());
    write(attribute.getType());
    write(attribute.getDefaultValue());
    write(attribute.getReferentials());
  }

  template<>
  void BufferedOutputStream::write<SWA::ObjectRelMetaData> ( const SWA::ObjectRelMetaData& rel )
  {
    write(rel.getNumber());
    write(rel.getDestObject());
    write(rel.getConditional());
    write(rel.getSuperSub());

    if ( !rel.getSuperSub() )
    {
      write(rel.getRolePhrase());
      write(rel.getMultiple());
    }
  }

  template<>
  void BufferedOutputStream::write<SWA::StateMetaData::StateType> ( const SWA::StateMetaData::StateType& type )
  {
    write(static_cast<int32_t>(type));
  }

  template<>
  void BufferedOutputStream::write<SWA::StateMetaData> ( const SWA::StateMetaData& state )
  {
    write(state.getId());
    write(state.getType());
    write(state.getName());
    write(state.getParameters());
    write(state.getLocalVariables());
    write(state.getFileName());
    write(state.getFileHash());
  }

  template<>
  void BufferedOutputStream::write<SWA::EventMetaData::EventType> ( const SWA::EventMetaData::EventType& type )
  {
    write(static_cast<int32_t>(type));
  }

  template<>
  void BufferedOutputStream::write<SWA::EventMetaData> ( const SWA::EventMetaData& event )
  {
    write(event.getId());
    write(event.getParentObjectId());
    write(event.getType());
    write(event.getName());
    write(event.getParameters());
  }

  template<>
  void BufferedOutputStream::write<SWA::StructureMetaData> ( const SWA::StructureMetaData& structure )
  {
    write(structure.getId());
    write(structure.getName());
    write(structure.getAttributes());
  }


  template<>
  void BufferedOutputStream::write<SWA::EnumerateMetaData> ( const SWA::EnumerateMetaData& enumerate )
  {
    write(enumerate.getId());
    write(enumerate.getName());
    write(enumerate.getValues());
  }

  template<>
  void BufferedOutputStream::write<SWA::ParameterMetaData> ( const SWA::ParameterMetaData& parameter )
  {
    write(parameter.getName());
    write(parameter.getTypeName());
    write(parameter.getType());
    write(parameter.isOut());
  }

  template<>
  void BufferedOutputStream::write<SWA::LocalVariableMetaData> ( const SWA::LocalVariableMetaData& variable )
  {
    write(variable.getName());
    write(variable.getTypeName());
    write(variable.getType());
  }

  template<>
  void BufferedOutputStream::write<SWA::ObjectMetaData> ( const SWA::ObjectMetaData& object )
  {
    write(object.getId());
    write(object.getName());
    write(object.getKeyLetters());
    write(object.getAttributes());
    write(object.getRelationships());
    write(object.getServices());
    write(object.getStates());
    write(object.getEvents());
  }

  template<>
  void BufferedOutputStream::write<SWA::TerminatorMetaData> ( const SWA::TerminatorMetaData& object )
  {
    write(object.getId());
    write(object.getName());
    write(object.getKeyLetters());
    write(object.getServices());
  }

  template<>
  void BufferedOutputStream::write<SWA::DomainMetaData> ( const SWA::DomainMetaData& domain )
  {
    write(domain.getId());
    write(domain.getName());
    write(domain.getIsInterface());
    write(domain.getServices());
    write(domain.getTerminators());
    write(domain.getObjects());
    write(domain.getRelationships());
    write(domain.getSuperSubtypes());
    write(domain.getStructures());
    write(domain.getEnumerates());
  }

  template<>
  void BufferedOutputStream::write<SWA::PluginRegistry> ( const SWA::PluginRegistry& pluginRegistry )
  {
    const std::vector<std::string>& pluginNames = pluginRegistry.getPlugins();
    write<int>(pluginNames.size());
    for (  std::vector<std::string>::const_iterator pluginIt = pluginNames.begin(), end = pluginNames.end(); pluginIt != end; ++pluginIt )
    {
      write(*pluginIt);
      write(pluginRegistry.getActions(*pluginIt));


      const std::vector<std::string>& flagNames = pluginRegistry.getFlags(*pluginIt);
      write<int>(flagNames.size());
      for (  std::vector<std::string>::const_iterator flagIt = flagNames.begin(), end = flagNames.end(); flagIt != end; ++flagIt )
      {
        write(*flagIt);
        write(pluginRegistry.isReadableFlag(*pluginIt,*flagIt));
        write(pluginRegistry.isWriteableFlag(*pluginIt,*flagIt));
      }

      const std::vector<std::string>& propertyNames = pluginRegistry.getProperties(*pluginIt);
      write<int>(propertyNames.size());
      for (  std::vector<std::string>::const_iterator propIt = propertyNames.begin(), end = propertyNames.end(); propIt != end; ++propIt )
      {
        write(*propIt);
        write(pluginRegistry.isReadableProperty(*pluginIt,*propIt));
        write(pluginRegistry.isWriteableProperty(*pluginIt,*propIt));
      }

    }
  }

  template<>
  void BufferedOutputStream::write<SWA::ProcessMetaData> ( const SWA::ProcessMetaData& process )
  {
    write(process.getName());
    write(SWA::PluginRegistry::getInstance());
 
    const SWA::ProcessMetaData::DomainLookup& domains = process.getDomainLookup();

    write<int>(domains.size());

    for ( SWA::ProcessMetaData::DomainLookup::const_iterator it = domains.begin(),
          end = domains.end();
          it != end; ++it )
    {
      write(it->second());
    }
  }

  template<>
  void BufferedOutputStream::write<SWA::StackFrame::ActionType> ( const SWA::StackFrame::ActionType& type )
  {
    write(static_cast<int32_t>(type));
  }


  template<>
  void BufferedOutputStream::write<SWA::StackFrame> ( const SWA::StackFrame& frame )
  {
    write(frame.getType());
    write(frame.getDomainId());
    write(frame.getObjectId());
    write(frame.getActionId());
    write(frame.getLine());
  }

  template<>
  void BufferedOutputStream::write<SWA::Event> ( const SWA::Event& event )
  {
    write(event.getDomainId());
    write(event.getObjectId());
    write(event.getEventId());
    
    write(event.getHasDest());
    if ( event.getHasDest() )
    {
      write(event.getDestInstanceId());
    }

    write(event.getHasSource());
    if ( event.getHasSource() )
    {
      write(event.getSourceObjectId());
      write(event.getHasSource());
      write(event.getSourceInstanceId());
    }

    ProcessHandler::getInstance().getDomainHandler(event.getDomainId()).getGenericObjectHandler(event.getObjectId()).getEventHandler(event.getEventId()).writeParameters(event,*this);

  }

  template<>
  void BufferedOutputStream::write<std::shared_ptr<SWA::Event> > ( const std::shared_ptr<SWA::Event>& event )
  {
    write(*event);
  }

  template<>
  void BufferedOutputStream::write<SWA::EventTimer> ( const SWA::EventTimer& timer )
  {
    write(timer.getId());
    write(timer.isScheduled());
    if ( timer.isScheduled() )
    {
      write(timer.getExpiryTime());
      write(timer.getPeriod());
      write(timer.getEvent());
    }
  }

  template<>
  void BufferedOutputStream::write<std::shared_ptr<SWA::EventTimer> > ( const std::shared_ptr<SWA::EventTimer>& timer )
  {
    write(*timer);
  }

};

