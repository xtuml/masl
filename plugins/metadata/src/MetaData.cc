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

#include "metadata/MetaData.hh"
#include "swa/NameFormatter.hh"
#include "swa/Process.hh"
#include "swa/ProgramError.hh"
#include <iostream>

namespace SWA {

    ParameterMetaData::ParameterMetaData(
        const std::string &name, const std::string &typeName, const TypeMetaData &type, bool out
    )
        : name(name), typeName(typeName), type(type), out(out) {}

    LocalVariableMetaData::LocalVariableMetaData(
        const std::string &name, const std::string &typeName, const TypeMetaData &type
    )
        : name(name), typeName(typeName), type(type) {}

    ServiceMetaData::ServiceMetaData(
        int id,
        ServiceType type,
        const std::string &name,
        const std::vector<int> &lines,
        const std::string &fileName,
        const std::string &fileHash
    )
        : id(id),
          type(type),
          name(name),
          function(false),
          returnTypeName(),
          returnType(),
          lines(lines),
          fileName(fileName),
          fileHash(fileHash) {}

    ServiceMetaData::ServiceMetaData(
        int id,
        ServiceType type,
        const std::string &name,
        const std::string &returnTypeName,
        const TypeMetaData &returnType,
        const std::vector<int> &lines,
        const std::string &fileName,
        const std::string &fileHash
    )
        : id(id),
          type(type),
          name(name),
          function(true),
          returnTypeName(returnTypeName),
          returnType(returnType),
          lines(lines),
          fileName(fileName),
          fileHash(fileHash) {}

    StateMetaData::StateMetaData(
        int id,
        StateType type,
        const std::string &name,
        const std::vector<int> &lines,
        const std::string &fileName,
        const std::string &fileHash
    )
        : id(id), type(type), name(name), lines(lines), fileName(fileName), fileHash(fileHash) {}

    EventMetaData::EventMetaData(int id, int parentObjectId, EventType type, const std::string &name)
        : id(id), parentObjectId(parentObjectId), type(type), name(name) {}

    TypeMetaData::TypeMetaData(BasicType basicType, int collectionDepth)
        : basicType(basicType), typeDomain(-1), typeId(-1), collectionDepth(collectionDepth), dictKey(), dictValue() {}

    TypeMetaData::TypeMetaData(
        BasicType basicType, const TypeMetaData &keyType, const TypeMetaData &valueType, int collectionDepth
    )
        : basicType(basicType),
          typeDomain(-1),
          typeId(-1),
          collectionDepth(collectionDepth),
          dictKey(std::shared_ptr<TypeMetaData>(new TypeMetaData(keyType))),
          dictValue(std::shared_ptr<TypeMetaData>(new TypeMetaData(valueType))) {}

    TypeMetaData::TypeMetaData(BasicType basicType, int typeDomain, int typeId, int collectionDepth)
        : basicType(basicType),
          typeDomain(typeDomain),
          typeId(typeId),
          collectionDepth(collectionDepth),
          dictKey(),
          dictValue() {}

    AttributeMetaData::AttributeMetaData(
        const std::string &name,
        bool identifier,
        const std::string &typeName,
        const TypeMetaData &type,
        const std::string &defaultValue
    )
        : name(name),
          identifier(identifier),
          typeName(typeName),
          type(type),
          defaultValue(defaultValue),
          referentials() {}

    AttributeMetaData::AttributeMetaData(
        const std::string &name, bool identifier, const std::string &typeName, const TypeMetaData &type, int referential
    )
        : name(name), identifier(identifier), typeName(typeName), type(type), defaultValue(), referentials() {
        referentials.push_back(referential);
    }

    void AttributeMetaData::addReferential(int relationship) {
        referentials.push_back(relationship);
    }

    ObjectRelMetaData::ObjectRelMetaData(
        const std::string &number, const std::string &rolePhrase, bool multiple, bool conditional, int destObject
    )
        : number(number),
          rolePhrase(rolePhrase),
          multiple(multiple),
          conditional(conditional),
          supersub(false),
          destObject(destObject) {}

    ObjectRelMetaData::ObjectRelMetaData(const std::string &number, bool conditional, int destObject)
        : number(number),
          rolePhrase(),
          multiple(false),
          conditional(conditional),
          supersub(true),
          destObject(destObject) {}

    RelationshipMetaData::RelationshipMetaData(
        int id,
        const std::string &name,
        int leftObject,
        const std::string &rightRole,
        bool rightMany,
        bool rightConditional,
        int rightObject,
        const std::string &leftRole,
        bool leftMany,
        bool leftConditional
    )
        : id(id),
          name(name),
          leftObject(leftObject),
          rightRole(rightRole),
          rightMany(rightMany),
          rightConditional(rightConditional),
          rightObject(rightObject),
          leftRole(leftRole),
          leftMany(leftMany),
          leftConditional(leftConditional),
          isAssoc(false),
          assocObject() {}

    RelationshipMetaData::RelationshipMetaData(
        int id,
        const std::string &name,
        int leftObject,
        const std::string &rightRole,
        bool rightMany,
        bool rightConditional,
        int rightObject,
        const std::string &leftRole,
        bool leftMany,
        bool leftConditional,
        int assocObject
    )
        : id(id),
          name(name),
          leftObject(leftObject),
          rightRole(rightRole),
          rightMany(rightMany),
          rightConditional(rightConditional),
          rightObject(rightObject),
          leftRole(leftRole),
          leftMany(leftMany),
          leftConditional(leftConditional),
          isAssoc(true),
          assocObject(assocObject) {}

    SuperSubtypeMetaData::SuperSubtypeMetaData(int id, const std::string &name, int superObject)
        : id(id), name(name), superObject(superObject) {}

    ObjectMetaData::ObjectMetaData(int id, const std::string &name, const std::string &keyLetters)
        : id(id), name(name), keyLetters(keyLetters) {}

    TerminatorMetaData::TerminatorMetaData(int id, const std::string &name, const std::string &keyLetters)
        : id(id), name(name), keyLetters(keyLetters) {}

    StructureMetaData::StructureMetaData(int id, const std::string &name)
        : id(id), name(name) {}

    EnumerateMetaData::EnumerateMetaData(int id, const std::string &name)
        : id(id), name(name) {}

    DomainMetaData::DomainMetaData(int id, const std::string &name, bool isInterface)
        : id(id), name(name), isInterface(isInterface) {}

    namespace {
        // Load up the process metadata once the process has initialised and domains are known.
        bool loadLibs() {
            SWA::Process::getInstance().loadDynamicProjectLibrary("metadata");
            return true;
        }

        bool initialise() {
            SWA::Process::getInstance().registerInitialisedListener(&loadLibs);
            return true;
        }

        bool initialised = initialise();
    } // namespace

    ProcessMetaData::ProcessMetaData() {}

    ProcessMetaData::~ProcessMetaData() {}

    ProcessMetaData &ProcessMetaData::getProcess() {
        static ProcessMetaData instance;
        return instance;
    }

    bool ProcessMetaData::addDomain(int id, const DomainFunction &getMetaDataFunction) {
        domainLookup.insert(std::make_pair(id, getMetaDataFunction));
        return true;
    }

    const DomainMetaData &ProcessMetaData::getDomain(int id) const {
        DomainFunction getter = getDomainGetter(id);
        if (!getter) {
            throw ProgramError("No MetaData present for " + Process::getInstance().getDomain(id).getName() + ".");
        }
        return getter();
    }

    ProcessMetaData::DomainFunction ProcessMetaData::getDomainGetter(int domainId) const {
        DomainLookup::const_iterator it = domainLookup.find(domainId);

        if (it == domainLookup.end()) {
            throw SWA::ProgramError(
                "No Meta Data present for " + SWA::Process::getInstance().getDomain(domainId).getName() + "."
            );
        }

        return it->second;
    }

    void ProcessMetaData::overrideTerminatorService(int domainId, int terminatorId, const ServiceMetaData &service) {
        DomainFunction getter = getDomainGetter(domainId);
        // Don't attempt to add override if no meta data present for domain
        if (getter) {
            getter().getTerminator(terminatorId).overrideService(service);
        }
    }

    class FullNameFormatter : public NameFormatter {
      private:
        std::string getDomainServiceName(int domainId, int serviceId) const {
            return ProcessMetaData::getProcess().getDomain(domainId).getService(serviceId).getName();
        }

        std::string getTerminatorName(int domainId, int terminatorId) const {
            return ProcessMetaData::getProcess().getDomain(domainId).getTerminator(terminatorId).getName();
        }

        std::string getTerminatorServiceName(int domainId, int terminatorId, int serviceId) const {
            return ProcessMetaData::getProcess()
                .getDomain(domainId)
                .getTerminator(terminatorId)
                .getService(serviceId)
                .getName();
        }

        std::string getObjectName(int domainId, int objectId) const {
            return ProcessMetaData::getProcess().getDomain(domainId).getObject(objectId).getName();
        }

        std::string getObjectServiceName(int domainId, int objectId, int serviceId) const {
            return ProcessMetaData::getProcess().getDomain(domainId).getObject(objectId).getService(serviceId).getName(
            );
        }

        std::string getStateName(int domainId, int objectId, int stateId) const {
            return ProcessMetaData::getProcess().getDomain(domainId).getObject(objectId).getState(stateId).getName();
        }

        std::string getEventName(int domainId, int objectId, int eventId) const {
            return ProcessMetaData::getProcess().getDomain(domainId).getObject(objectId).getEvent(eventId).getName();
        }

        std::string getDomainServiceFileName(int domainId, int serviceId) const {
            return ProcessMetaData::getProcess().getDomain(domainId).getService(serviceId).getFileName();
        }

        std::string getTerminatorServiceFileName(int domainId, int terminatorId, int serviceId) const {
            return ProcessMetaData::getProcess()
                .getDomain(domainId)
                .getTerminator(terminatorId)
                .getService(serviceId)
                .getFileName();
        }

        std::string getObjectServiceFileName(int domainId, int objectId, int serviceId) const {
            return ProcessMetaData::getProcess()
                .getDomain(domainId)
                .getObject(objectId)
                .getService(serviceId)
                .getFileName();
        }

        std::string getStateFileName(int domainId, int objectId, int stateId) const {
            return ProcessMetaData::getProcess().getDomain(domainId).getObject(objectId).getState(stateId).getFileName(
            );
        }

        int getEventParentObjectId(int domainId, int objectId, int eventId) const {
            return ProcessMetaData::getProcess()
                .getDomain(domainId)
                .getObject(objectId)
                .getEvent(eventId)
                .getParentObjectId();
        }
    };

    bool formatterInit = NameFormatter::overrideFormatter(std::shared_ptr<NameFormatter>(new FullNameFormatter()));
} // namespace SWA
