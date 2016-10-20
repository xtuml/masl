//
// File: MetaData.java
//
// UK Crown Copyright (c) 2009. All Rights Reserved.
//
package org.xtuml.masl.translate.metadata;

import org.xtuml.masl.cppgen.Class;
import org.xtuml.masl.cppgen.CodeFile;
import org.xtuml.masl.cppgen.Expression;
import org.xtuml.masl.cppgen.Function;
import org.xtuml.masl.cppgen.Library;
import org.xtuml.masl.cppgen.Namespace;


public class Architecture
{

  static public  final Library  metaDataLib                   = new Library("MetaData").inBuildSet(org.xtuml.masl.translate.main.Architecture.buildSet);

  static private final CodeFile  metaDataInc                  = metaDataLib.createInterfaceHeader("metadata/MetaData.hh");
  static private final Namespace metaDataNS                   = new Namespace("SWA");

  static final Class             domainMetaData               = new Class("DomainMetaData", metaDataNS, metaDataInc);
  static final Class             processMetaData              = new Class("ProcessMetaData", metaDataNS, metaDataInc);
  static final Class             relationshipMetaData         = new Class("RelationshipMetaData", metaDataNS, metaDataInc);
  static final Class             superSubtypeMetaData         = new Class("SuperSubtypeMetaData", metaDataNS, metaDataInc);
  static final Class             serviceMetaData              = new Class("ServiceMetaData", metaDataNS, metaDataInc);
  static final Class             typeMetaData                 = new Class("TypeMetaData", metaDataNS, metaDataInc);
  static final Class             objectMetaData               = new Class("ObjectMetaData", metaDataNS, metaDataInc);
  static final Class             terminatorMetaData           = new Class("TerminatorMetaData", metaDataNS, metaDataInc);
  static final Class             objRelMetaData               = new Class("ObjectRelMetaData", metaDataNS, metaDataInc);
  static final Class             attributeMetaData            = new Class("AttributeMetaData", metaDataNS, metaDataInc);
  static final Class             stateMetaData                = new Class("StateMetaData", metaDataNS, metaDataInc);
  static final Class             eventMetaData                = new Class("EventMetaData", metaDataNS, metaDataInc);
  static final Class             enumMetaData                 = new Class("EnumerateMetaData", metaDataNS, metaDataInc);
  static final Class             structMetaData               = new Class("StructureMetaData", metaDataNS, metaDataInc);
  static final Class             dictionaryMetaData           = new Class("DictionaryMetaData", metaDataNS, metaDataInc);
  static final Class             parameterMetaData            = new Class("ParameterMetaData", metaDataNS, metaDataInc);
  static final Class             localVarMetaData             = new Class("LocalVariableMetaData", metaDataNS, metaDataInc);

  static final Expression        processInstance              = processMetaData.callStaticFunction("getProcess");


  static final Function          addService                   = new Function("addService");
  static final Function          addStructure                 = new Function("addStructure");
  static final Function          addDictionary                = new Function("addDictionary");
  static final Function          addEnumerate                 = new Function("addEnumerate");
  static final Function          addRelationship              = new Function("addRelationship");
  static final Function          addSuperSubtype              = new Function("addSuperSubtype");
  static final Function          addObject                    = new Function("addObject");
  static final Function          addTerminator                = new Function("addTerminator");
  static final Function          addState                     = new Function("addState");
  static final Function          addAttribute                 = new Function("addAttribute");
  static final Function          addEvent                     = new Function("addEvent");
  static final Function          addSubObject                 = new Function("addSubObject");
  static final Function          addReferential               = new Function("addReferential");
  static final Function          addValue                     = new Function("addValue");
  static final Function          addParameter                 = new Function("addParameter");
  static final Function          addLocalVar                  = new Function("addLocalVariable");
  static final Function          overrideTerminatorService    = new Function("overrideTerminatorService");

  static final Expression        scenarioFlag                 = serviceMetaData.referenceStaticMember("Scenario");
  static final Expression        externalFlag                 = serviceMetaData.referenceStaticMember("External");
  static final Expression        domainServiceFlag            = serviceMetaData.referenceStaticMember("Domain");
  static final Expression        objectServiceFlag            = serviceMetaData.referenceStaticMember("Object");
  static final Expression        instanceServiceFlag          = serviceMetaData.referenceStaticMember("Instance");
  static final Expression        terminatorServiceFlag        = serviceMetaData.referenceStaticMember("Terminator");
  static final Expression        projectTerminatorServiceFlag = serviceMetaData.referenceStaticMember("ProjectTerminator");

  static final Expression        assignerStateFlag            = stateMetaData.referenceStaticMember("Assigner");
  static final Expression        startStateFlag               = stateMetaData.referenceStaticMember("Start");
  static final Expression        normalStateFlag              = stateMetaData.referenceStaticMember("Normal");
  static final Expression        creationStateFlag            = stateMetaData.referenceStaticMember("Creation");
  static final Expression        terminalStateFlag            = stateMetaData.referenceStaticMember("Terminal");
  static final Expression        assignerEventFlag            = eventMetaData.referenceStaticMember("Assigner");
  static final Expression        normalEventFlag              = eventMetaData.referenceStaticMember("Normal");
  static final Expression        creationEventFlag            = eventMetaData.referenceStaticMember("Creation");

  static final Expression        anyInstanceTypeFlag          = typeMetaData.referenceStaticMember("AnyInstance");
  static final Expression        booleanTypeFlag              = typeMetaData.referenceStaticMember("Boolean");
  static final Expression        byteTypeFlag                 = typeMetaData.referenceStaticMember("Byte");
  static final Expression        characterTypeFlag            = typeMetaData.referenceStaticMember("Character");
  static final Expression        deviceTypeFlag               = typeMetaData.referenceStaticMember("Device");
  static final Expression        eventTypeFlag                = typeMetaData.referenceStaticMember("Event");
  static final Expression        durationTypeFlag             = typeMetaData.referenceStaticMember("Duration");
  static final Expression        enumTypeFlag                 = typeMetaData.referenceStaticMember("Enumeration");
  static final Expression        instanceTypeFlag             = typeMetaData.referenceStaticMember("Instance");
  static final Expression        integerTypeFlag              = typeMetaData.referenceStaticMember("LongInteger");
  static final Expression        smallIntegerTypeFlag         = typeMetaData.referenceStaticMember("Integer");
  static final Expression        realTypeFlag                 = typeMetaData.referenceStaticMember("Real");
  static final Expression        stringTypeFlag               = typeMetaData.referenceStaticMember("String");
  static final Expression        structureTypeFlag            = typeMetaData.referenceStaticMember("Structure");
  static final Expression        timestampTypeFlag            = typeMetaData.referenceStaticMember("Timestamp");
  static final Expression        wcharacterTypeFlag           = typeMetaData.referenceStaticMember("WCharacter");
  static final Expression        wstringTypeFlag              = typeMetaData.referenceStaticMember("WString");
  static final Expression        timerTypeFlag                = typeMetaData.referenceStaticMember("Timer");
  static final Expression        dictionaryTypeFlag           = typeMetaData.referenceStaticMember("Dictionary");


}
