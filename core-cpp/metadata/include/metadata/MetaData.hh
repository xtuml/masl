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

#ifndef SWA_MetaData_HH
#define SWA_MetaData_HH

#include <string>
#include <map>
#include <vector>
#include <memory>

#include "boost/function.hpp"
#include "boost/shared_ptr.hpp"

namespace SWA {

  class TypeMetaData
  {
    public:
      enum BasicType { AnyInstance, Boolean, Byte, Character, Device, Duration, Enumeration, Event, Instance, Integer, LongInteger, 
                       LongNatural, Natural, Real, State, String, Structure, Timestamp, WCharacter, WString, Timer, Dictionary, Void };

      TypeMetaData ( BasicType basicType = Void, int collectionDepth = 0 );
      TypeMetaData ( BasicType basicType, const TypeMetaData& keyType, const TypeMetaData& valueType, int collectionDepth = 0 );
      TypeMetaData ( BasicType basicType, int typeDomain, int typeId, int collectionDepth = 0 );

      BasicType getBasicType() const { return basicType; }
      int getTypeDomain() const      { return typeDomain; }
      int getTypeId() const          { return typeId; }
      int getCollectionDepth() const { return collectionDepth; }
      const TypeMetaData& getDictionaryKey() const { return *dictKey; }
      const TypeMetaData& getDictionaryValue() const { return *dictValue; }

    private:
      BasicType basicType;
      int typeDomain;
      int typeId;
      int collectionDepth;
      boost::shared_ptr<TypeMetaData> dictKey;
      boost::shared_ptr<TypeMetaData> dictValue;
  }; 


  class LocalVariableMetaData
  {
    public:
      LocalVariableMetaData ( const std::string& name, 
                              const std::string& typeName,
                              const TypeMetaData& type );

      const std::string& getName() const { return name; }
      const std::string& getTypeName() const { return typeName; }
      const TypeMetaData& getType() const { return type; }

    private:
      std::string name;
      std::string typeName;
      TypeMetaData type;
  };

  class ParameterMetaData
  {
    public:
      ParameterMetaData ( const std::string& name, 
                          const std::string& typeName,
                          const TypeMetaData& type,
                          bool out );

      const std::string& getName() const { return name; }
      const std::string& getTypeName() const { return typeName; }
      const TypeMetaData& getType() const { return type; }
      bool isOut() const { return out; }

    private:
      std::string name;
      std::string typeName;
      TypeMetaData type;
      bool out;
  };

  class ServiceMetaData
  {
    public:
      enum ServiceType { Scenario, External, Domain, Terminator, Object, Instance, ProjectTerminator };

      ServiceMetaData ( int id, ServiceType type, const std::string& name, const std::vector<int>& lines, const std::string& fileName, const std::string& fileHash );
      ServiceMetaData ( int id, ServiceType type, const std::string& name, const std::string& returnTypeName, const TypeMetaData& returnType, const std::vector<int>& lines, const std::string& fileName, const std::string& fileHash );

      int getId() const { return id; }
      const ServiceType getType() const { return type; }
      const std::string& getName() const { return name; }

      void addParameter(const ParameterMetaData& parameter ) { parameters.push_back(parameter); }
      const std::vector<ParameterMetaData>& getParameters() const { return parameters; } 

      void addLocalVariable(const LocalVariableMetaData& var ) { localVars.push_back(var); }
      const std::vector<LocalVariableMetaData>& getLocalVariables() const { return localVars; } 

      bool isFunction() const { return function; }
      const std::string& getReturnTypeName() const { return returnTypeName; }
      const TypeMetaData& getReturnType() const { return returnType; }

      const std::vector<int>& getLines() const { return lines; } 

      const std::string& getFileName() const { return fileName; }
      const std::string& getFileHash() const { return fileHash; }


    private:
      int id; 
      ServiceType type;
      std::string name;
      std::vector<ParameterMetaData> parameters;
      std::vector<LocalVariableMetaData> localVars;
      bool function;
      std::string returnTypeName;
      TypeMetaData returnType;
      std::vector<int> lines;
      std::string fileName;
      std::string fileHash;
  };

  class StateMetaData
  {
    public:
      enum StateType { Assigner, Start, Normal, Creation, Terminal };

      StateMetaData ( int id, StateType type, const std::string& name, const std::vector<int>& lines, const std::string& fileName, const std::string& fileHash );

      int getId() const { return id; }
      const StateType getType() const { return type; }
      const std::string& getName() const { return name; }

      void addParameter(const ParameterMetaData& parameter ) { parameters.push_back(parameter); }
      const std::vector<ParameterMetaData>& getParameters() const { return parameters; } 

      void addLocalVariable(const LocalVariableMetaData& var ) { localVars.push_back(var); }
      const std::vector<LocalVariableMetaData>& getLocalVariables() const { return localVars; } 

      const std::vector<int>& getLines() const { return lines; } 

      const std::string& getFileName() const { return fileName; }
      const std::string& getFileHash() const { return fileHash; }

    private:
      int id; 
      StateType type;
      std::string name;
      std::vector<ParameterMetaData> parameters;
      std::vector<LocalVariableMetaData> localVars;
      std::vector<int> lines;
      std::string fileName;
      std::string fileHash;
  };

  class EventMetaData
  {
    public:
      enum EventType { Assigner, Creation, Normal };

      EventMetaData ( int id, int parentObjectId, EventType type, const std::string& name );

      int getId() const { return id; }
      int getParentObjectId() const { return parentObjectId; }
      const EventType getType() const { return type; }
      const std::string& getName() const { return name; }
 
      void addParameter(const ParameterMetaData& parameter ) { parameters.push_back(parameter); }
      const std::vector<ParameterMetaData>& getParameters() const { return parameters; } 

    private:
      int id; 
      int parentObjectId; 
      EventType type;
      std::string name;
      std::vector<ParameterMetaData> parameters;
  };

  class AttributeMetaData
  {
    public:
      AttributeMetaData ( const std::string& name, 
                          bool identifier, 
                          const std::string& typeName,
                          const TypeMetaData& type,
                          const std::string& defaultValue = "" );

      AttributeMetaData ( const std::string& name, 
                          bool identifier, 
                          const std::string& typeName,
                          const TypeMetaData& type,
                          int referential );

      void addReferential ( int relationship );

      const std::string& getName() const { return name; }
      bool getIdentifier() const { return identifier; }
      const std::string& getTypeName() const { return typeName; }
      const TypeMetaData& getType() const { return type; }
      const std::string& getDefaultValue() const { return defaultValue; }
      const std::vector<int>& getReferentials() const { return referentials; } 

    private:
      std::string name;
      bool identifier;
      std::string typeName;
      TypeMetaData type;
      std::string defaultValue;
      std::vector<int> referentials;
  };

  class ObjectRelMetaData
  {
    public:
      ObjectRelMetaData ( const std::string& number, 
                          const std::string& rolePhrase, 
                          bool multiple,
                          bool conditional,
                          int destObjectId ); 

      ObjectRelMetaData ( const std::string& number, 
                          bool conditional,
                          int destObjectId ); 

      const std::string& getNumber() const { return number; } 
      const std::string& getRolePhrase() const { return rolePhrase; } 
      bool getMultiple() const { return multiple; }
      bool getConditional() const { return conditional; }
      bool getSuperSub() const { return supersub; }
      int getDestObject() const { return destObject; } 

    private:
      std::string number;
      std::string rolePhrase;
      bool multiple;
      bool conditional;
      bool supersub;
      int destObject;
  };

  class ObjectMetaData
  {
    public:
      ObjectMetaData ( int id, const std::string& name, const std::string& keyLetters );

      int getId() const { return id; }
      const std::string& getName() const { return name; }
      const std::string& getKeyLetters() const { return keyLetters; }

      void addAttribute(const AttributeMetaData& attribute ) { attributes.push_back(attribute); }
      const std::vector<AttributeMetaData>& getAttributes() const { return attributes; } 

      void addRelationship(const ObjectRelMetaData& relationship ) { relationships.push_back(relationship); }
      const std::vector<ObjectRelMetaData>& getRelationships() const { return relationships; } 

      void addService(const ServiceMetaData& service ) { serviceLookup.insert(std::make_pair(service.getId(),services.size())); services.push_back(service);}
      const std::vector<ServiceMetaData>& getServices() const { return services; } 
      const ServiceMetaData& getService(int i) const { return services[serviceLookup.find(i)->second]; } 


      void addState(const StateMetaData& state ) { stateLookup.insert(std::make_pair(state.getId(),states.size())); states.push_back(state);}
      const std::vector<StateMetaData>& getStates() const { return states; } 
      const StateMetaData& getState(int i) const { return states[stateLookup.find(i)->second]; } 


      void addEvent(const EventMetaData& event ) { eventLookup.insert(std::make_pair(event.getId(),events.size())); events.push_back(event);}
      const std::vector<EventMetaData>& getEvents() const { return events; } 
      const EventMetaData& getEvent(int i) const { return events[eventLookup.find(i)->second]; } 


    private:
      int id; 
      std::string name;
      std::string keyLetters;
      std::vector<AttributeMetaData> attributes;
      std::vector<ObjectRelMetaData> relationships;

      std::vector<ServiceMetaData> services;
      std::vector<StateMetaData> states;
      std::vector<EventMetaData> events;

      std::map<int,int> serviceLookup;
      std::map<int,int> stateLookup;
      std::map<int,int> eventLookup;
  };


  class TerminatorMetaData
  {
    public:
      TerminatorMetaData ( int id, const std::string& name, const std::string& keyLetters );

      int getId() const { return id; }
      const std::string& getName() const { return name; }
      const std::string& getKeyLetters() const { return keyLetters; }

      void addService(const ServiceMetaData& service ) { serviceLookup.insert(std::make_pair(service.getId(),services.size())); services.push_back(service);}
      const std::vector<ServiceMetaData>& getServices() const { return services; } 
      const ServiceMetaData& getService(int i) const { return services[serviceLookup.find(i)->second]; } 
      ServiceMetaData& getService(int i) { return services[serviceLookup.find(i)->second]; } 

      void overrideService(const ServiceMetaData& override )
      {
        getService(override.getId()) = override;
      }

    private:
      int id; 
      std::string name;
      std::string keyLetters;

      std::vector<ServiceMetaData> services;

      std::map<int,int> serviceLookup;
  };


  class RelationshipMetaData
  {
    public:
      RelationshipMetaData ( int id, const std::string& name,
                             int leftObject,  const std::string& rightRole, bool rightMany, bool rightConditional,
                             int rightObject, const std::string& leftRole,  bool leftMany,  bool leftConditional );

      RelationshipMetaData ( int id, const std::string& name, 
                             int leftObject,  const std::string& rightRole, bool rightMany, bool rightConditional,
                             int rightObject, const std::string& leftRole,  bool leftMany,  bool leftConditional,
                             int assocObject );

      int getId() const { return id; }
      const std::string& getName() const { return name; }
      int getLeftObject() const { return leftObject; }
      const std::string& getRightRole() const { return rightRole; }
      bool getRightMany() const { return rightMany; }
      bool getRightConditional() const { return rightConditional; }
      int getRightObject() const { return rightObject; }
      const std::string& getLeftRole() const { return leftRole; }
      bool getLeftMany() const { return leftMany; }
      bool getLeftConditional() const { return leftConditional; }
      int getAssocObject() const { return assocObject; }
      bool getIsAssoc() const { return isAssoc; }

    private:
      int id;
      std::string name;
      int leftObject;
      std::string rightRole;
      bool rightMany;
      bool rightConditional;
      int rightObject;
      std::string leftRole;
      bool leftMany;
      bool leftConditional;
      bool isAssoc;
      int assocObject;
  };

  class SuperSubtypeMetaData
  {
    public:
      SuperSubtypeMetaData ( int id, const std::string& name, int superObject );

      int getId() const { return id; }
      const std::string& getName() const { return name; }
      int getSuperObject() const { return superObject; }

      void addSubObject( int subObject ) { subObjects.push_back(subObject);}
      const std::vector<int>& getSubObjects() const { return subObjects; } 

    private:
      int id;
      std::string name;
      int superObject;
      std::vector<int> subObjects;
  };


  class StructureMetaData
  {
    public:
      StructureMetaData ( int id, const std::string& name );

      int getId() const { return id; }
      const std::string& getName() const { return name; }

      void addAttribute(const AttributeMetaData& attribute ) { attributes.push_back(attribute); }
      const std::vector<AttributeMetaData>& getAttributes() const { return attributes; } 

    private:
      int id;
      std::string name;
      std::vector<AttributeMetaData> attributes;
  };

  class EnumerateMetaData
  {
    public:
      EnumerateMetaData ( int id, const std::string& name );

      int getId() const { return id; }
      const std::string& getName() const { return name; }

      void addValue(int value, const std::string& name ) { values.push_back(std::make_pair(value,name)); }
      const std::vector<std::pair<int,std::string> >& getValues() const { return values; } 

    private:
      int id;
      std::string name;
      std::vector<std::pair<int,std::string> > values;
  };



  class DomainMetaData
  {
    public:
      DomainMetaData ( int id, const std::string& name, bool isInterface );

      int getId() const { return id; }
      const std::string& getName() const { return name; }
      bool getIsInterface() const { return isInterface; }

      void addService(const ServiceMetaData& service ) { serviceLookup.insert(std::make_pair(service.getId(),services.size())); services.push_back(service);}
      const std::vector<ServiceMetaData>& getServices() const { return services; } 
      const ServiceMetaData& getService(int i) const { return services[serviceLookup.find(i)->second]; } 

      void addObject(const ObjectMetaData& object ) { objectLookup.insert(std::make_pair(object.getId(),objects.size())); objects.push_back(object); }
      const std::vector<ObjectMetaData>& getObjects() const { return objects; } 
      const ObjectMetaData& getObject(int i) const { return objects[objectLookup.find(i)->second]; } 

      void addTerminator(const TerminatorMetaData& terminator ) { terminatorLookup.insert(std::make_pair(terminator.getId(),terminators.size())); terminators.push_back(terminator); }
      const std::vector<TerminatorMetaData>& getTerminators() const { return terminators; } 
      const TerminatorMetaData& getTerminator(int i) const { return terminators[terminatorLookup.find(i)->second]; } 
      TerminatorMetaData& getTerminator(int i) { return terminators[terminatorLookup.find(i)->second]; } 

      void addRelationship(const RelationshipMetaData& relationship ) { relationshipLookup.insert(std::make_pair(relationship.getId(),relationships.size())); relationships.push_back(relationship); }
      const std::vector<RelationshipMetaData>& getRelationships() const { return relationships; } 
      const RelationshipMetaData& getRelationship(int i) const { return relationships[relationshipLookup.find(i)->second]; } 

      void addSuperSubtype(const SuperSubtypeMetaData& superSubtype ) { superSubtypeLookup.insert(std::make_pair(superSubtype.getId(),superSubtypes.size())); superSubtypes.push_back(superSubtype); }
      const std::vector<SuperSubtypeMetaData>& getSuperSubtypes() const { return superSubtypes; } 
      const SuperSubtypeMetaData& getSuperSubtype(int i) const { return superSubtypes[superSubtypeLookup.find(i)->second]; } 

      void addStructure(const StructureMetaData& structure ) { structureLookup.insert(std::make_pair(structure.getId(),structures.size())); structures.push_back(structure); }
      const std::vector<StructureMetaData>& getStructures() const { return structures; } 
      const StructureMetaData& getStructure(int i) const { return structures[structureLookup.find(i)->second]; } 

      void addEnumerate(const EnumerateMetaData& enumerate ) { enumerateLookup.insert(std::make_pair(enumerate.getId(),enumerates.size())); enumerates.push_back(enumerate); }
      const std::vector<EnumerateMetaData>& getEnumerates() const { return enumerates; } 
      const EnumerateMetaData& getEnumerate(int i) const { return enumerates[enumerateLookup.find(i)->second]; } 

    private:
      int id;
      std::string name;
      bool isInterface;

      std::vector<ServiceMetaData>      services;
      std::vector<ObjectMetaData>       objects;
      std::vector<TerminatorMetaData>   terminators;
      std::vector<RelationshipMetaData> relationships;
      std::vector<SuperSubtypeMetaData> superSubtypes;
      std::vector<StructureMetaData>    structures;
      std::vector<EnumerateMetaData>    enumerates;

      std::map<int,int> serviceLookup;
      std::map<int,int> objectLookup;
      std::map<int,int> terminatorLookup;
      std::map<int,int> relationshipLookup;
      std::map<int,int> superSubtypeLookup;
      std::map<int,int> structureLookup;
      std::map<int,int> enumerateLookup;
  };

  class ProcessMetaData
  {
    public:
      typedef boost::function<DomainMetaData& ()>    DomainFunction;

      static ProcessMetaData& getProcess();

      bool addDomain ( int id, const DomainFunction& getMetaDataFunction );
      const SWA::DomainMetaData& getDomain ( int id ) const;

      typedef std::map<int,DomainFunction>    DomainLookup;
      const DomainLookup& getDomainLookup() const { return domainLookup;}

      bool setName ( const std::string& name ) { this->name = name; return true;}
      const std::string& getName() const { return name; }

      void overrideTerminatorService ( int domainId, int terminatorId, const ServiceMetaData& service );

    private:
      ProcessMetaData();    
      ~ProcessMetaData();    

      // Prevent copying
      ProcessMetaData(const ProcessMetaData&);    
      ProcessMetaData& operator=(const ProcessMetaData&);    

      DomainFunction getDomainGetter ( int id ) const;

      mutable DomainLookup    domainLookup; // Mutable for lazy load

      std::string name;   
  };

} // end namespace SWA

#endif
