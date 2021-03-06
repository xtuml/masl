//
// File: MetaDataTranslator.java
//
// UK Crown Copyright (c) 2007. All Rights Reserved.
//
package org.xtuml.masl.translate.metadata;

import static org.xtuml.masl.translate.metadata.Architecture.addAttribute;
import static org.xtuml.masl.translate.metadata.Architecture.addEnumerate;
import static org.xtuml.masl.translate.metadata.Architecture.addEvent;
import static org.xtuml.masl.translate.metadata.Architecture.addLocalVar;
import static org.xtuml.masl.translate.metadata.Architecture.addObject;
import static org.xtuml.masl.translate.metadata.Architecture.addParameter;
import static org.xtuml.masl.translate.metadata.Architecture.addReferential;
import static org.xtuml.masl.translate.metadata.Architecture.addRelationship;
import static org.xtuml.masl.translate.metadata.Architecture.addService;
import static org.xtuml.masl.translate.metadata.Architecture.addState;
import static org.xtuml.masl.translate.metadata.Architecture.addStructure;
import static org.xtuml.masl.translate.metadata.Architecture.addSubObject;
import static org.xtuml.masl.translate.metadata.Architecture.addSuperSubtype;
import static org.xtuml.masl.translate.metadata.Architecture.addTerminator;
import static org.xtuml.masl.translate.metadata.Architecture.addValue;
import static org.xtuml.masl.translate.metadata.Architecture.assignerEventFlag;
import static org.xtuml.masl.translate.metadata.Architecture.assignerStateFlag;
import static org.xtuml.masl.translate.metadata.Architecture.attributeMetaData;
import static org.xtuml.masl.translate.metadata.Architecture.creationEventFlag;
import static org.xtuml.masl.translate.metadata.Architecture.creationStateFlag;
import static org.xtuml.masl.translate.metadata.Architecture.domainMetaData;
import static org.xtuml.masl.translate.metadata.Architecture.domainServiceFlag;
import static org.xtuml.masl.translate.metadata.Architecture.enumMetaData;
import static org.xtuml.masl.translate.metadata.Architecture.eventMetaData;
import static org.xtuml.masl.translate.metadata.Architecture.externalFlag;
import static org.xtuml.masl.translate.metadata.Architecture.instanceServiceFlag;
import static org.xtuml.masl.translate.metadata.Architecture.normalEventFlag;
import static org.xtuml.masl.translate.metadata.Architecture.normalStateFlag;
import static org.xtuml.masl.translate.metadata.Architecture.objRelMetaData;
import static org.xtuml.masl.translate.metadata.Architecture.objectMetaData;
import static org.xtuml.masl.translate.metadata.Architecture.objectServiceFlag;
import static org.xtuml.masl.translate.metadata.Architecture.processInstance;
import static org.xtuml.masl.translate.metadata.Architecture.relationshipMetaData;
import static org.xtuml.masl.translate.metadata.Architecture.scenarioFlag;
import static org.xtuml.masl.translate.metadata.Architecture.serviceMetaData;
import static org.xtuml.masl.translate.metadata.Architecture.startStateFlag;
import static org.xtuml.masl.translate.metadata.Architecture.stateMetaData;
import static org.xtuml.masl.translate.metadata.Architecture.structMetaData;
import static org.xtuml.masl.translate.metadata.Architecture.superSubtypeMetaData;
import static org.xtuml.masl.translate.metadata.Architecture.terminalStateFlag;
import static org.xtuml.masl.translate.metadata.Architecture.terminatorMetaData;
import static org.xtuml.masl.translate.metadata.Architecture.terminatorServiceFlag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xtuml.masl.cppgen.AggregateInitialiser;
import org.xtuml.masl.cppgen.BinaryExpression;
import org.xtuml.masl.cppgen.BinaryOperator;
import org.xtuml.masl.cppgen.CodeBlock;
import org.xtuml.masl.cppgen.CodeFile;
import org.xtuml.masl.cppgen.EnumerationType;
import org.xtuml.masl.cppgen.Expression;
import org.xtuml.masl.cppgen.Function;
import org.xtuml.masl.cppgen.FundamentalType;
import org.xtuml.masl.cppgen.InterfaceLibrary;
import org.xtuml.masl.cppgen.Library;
import org.xtuml.masl.cppgen.Literal;
import org.xtuml.masl.cppgen.Namespace;
import org.xtuml.masl.cppgen.ReturnStatement;
import org.xtuml.masl.cppgen.SharedLibrary;
import org.xtuml.masl.cppgen.StatementGroup;
import org.xtuml.masl.cppgen.Std;
import org.xtuml.masl.cppgen.TypeUsage;
import org.xtuml.masl.cppgen.Variable;
import org.xtuml.masl.metamodel.code.VariableDefinition;
import org.xtuml.masl.metamodel.common.ParameterDefinition;
import org.xtuml.masl.metamodel.common.Service;
import org.xtuml.masl.metamodel.domain.Domain;
import org.xtuml.masl.metamodel.domain.DomainService;
import org.xtuml.masl.metamodel.domain.DomainTerminator;
import org.xtuml.masl.metamodel.domain.DomainTerminatorService;
import org.xtuml.masl.metamodel.object.AttributeDeclaration;
import org.xtuml.masl.metamodel.object.ObjectDeclaration;
import org.xtuml.masl.metamodel.object.ObjectService;
import org.xtuml.masl.metamodel.object.ReferentialAttributeDefinition;
import org.xtuml.masl.metamodel.relationship.AssociativeRelationshipDeclaration;
import org.xtuml.masl.metamodel.relationship.MultiplicityType;
import org.xtuml.masl.metamodel.relationship.NormalRelationshipDeclaration;
import org.xtuml.masl.metamodel.relationship.RelationshipDeclaration;
import org.xtuml.masl.metamodel.relationship.RelationshipSpecification;
import org.xtuml.masl.metamodel.relationship.SubtypeRelationshipDeclaration;
import org.xtuml.masl.metamodel.statemodel.EventDeclaration;
import org.xtuml.masl.metamodel.statemodel.State;
import org.xtuml.masl.metamodel.type.EnumerateItem;
import org.xtuml.masl.metamodel.type.EnumerateType;
import org.xtuml.masl.metamodel.type.StructureElement;
import org.xtuml.masl.metamodel.type.StructureType;
import org.xtuml.masl.metamodel.type.TypeDeclaration;
import org.xtuml.masl.translate.Alias;
import org.xtuml.masl.translate.Default;
import org.xtuml.masl.translate.build.FileGroup;
import org.xtuml.masl.translate.main.Boost;
import org.xtuml.masl.translate.main.DomainNamespace;
import org.xtuml.masl.translate.main.EnumerationTranslator;
import org.xtuml.masl.translate.main.Mangler;
import org.xtuml.masl.translate.main.RelationshipTranslator;
import org.xtuml.masl.translate.main.TerminatorServiceTranslator;
import org.xtuml.masl.translate.main.TerminatorTranslator;
import org.xtuml.masl.translate.main.expression.ExpressionTranslator;
import org.xtuml.masl.translate.main.object.ObjectServiceTranslator;
import org.xtuml.masl.translate.main.object.ObjectTranslator;
import org.xtuml.masl.translate.main.object.StateActionTranslator;


@Alias("MetaData")
@Default
public class DomainTranslator extends org.xtuml.masl.translate.DomainTranslator

{

  private Library commonHeaders;

  public static DomainTranslator getInstance ( final Domain domain )
  {
    return getInstance(DomainTranslator.class, domain);
  }



  private DomainTranslator ( final Domain domain )
  {
    super(domain);
    mainDomainTranslator = org.xtuml.masl.translate.main.DomainTranslator.getInstance(domain);

    library = new SharedLibrary(mainDomainTranslator.getLibrary().getName() + "_metadata").withCCDefaultExtensions().withDefaultHeaderPath(domain.getName() + "_OOA").inBuildSet(mainDomainTranslator.getBuildSet());
    codeFile = library.createBodyFile("MetaData" + Mangler.mangleFile(domain));

    commonHeaders = new InterfaceLibrary(mainDomainTranslator.getLibrary().getName() + "_common_metadata").withCCDefaultExtensions().withDefaultHeaderPath(domain.getName() + "_OOA").inBuildSet(mainDomainTranslator.getBuildSet());

    interfaceLibrary = new SharedLibrary(mainDomainTranslator.getLibrary().getName() + "_if_metadata").withCCDefaultExtensions().withDefaultHeaderPath(domain.getName() + "_OOA").inBuildSet(mainDomainTranslator.getBuildSet());
    interfaceCodeFile = interfaceLibrary.createBodyFile("MetaData__if" + Mangler.mangleFile(domain));

    library.addDependency(Architecture.metaDataLib);
    interfaceLibrary.addDependency(Architecture.metaDataLib);

    headerFile = commonHeaders.createInterfaceHeader("MetaData" + Mangler.mangleFile(domain));

    typeIds = new EnumerationType("TypeIds", DomainNamespace.get(domain));
    headerFile.addEnumerateDeclaration(typeIds);

  }

  Expression getTypeId ( final TypeDeclaration type )
  {
    Expression typeId = typeIdLookup.get(type);
    if ( typeId == null )
    {
      typeId = typeIds.addEnumerator("typeId_" + Mangler.mangleName(type), null).asExpression();
      typeIdLookup.put(type, typeId);
    }
    return typeId;
  }

  private final EnumerationType                  typeIds;
  private final Map<TypeDeclaration, Expression> typeIdLookup = new HashMap<TypeDeclaration, Expression>();

  public CodeFile getCodeFile ()
  {
    return codeFile;
  }

  public CodeFile getHeaderFile ()
  {
    return headerFile;
  }


  public CodeFile getInterfaceCodeFile ()
  {
    return interfaceCodeFile;
  }

  public FileGroup getInterfaceLibrary ()
  {
    return interfaceLibrary;
  }


  public FileGroup getLibrary ()
  {
    return library;
  }

  @Override
  public Collection<org.xtuml.masl.translate.DomainTranslator> getPrerequisites ()
  {
    return Arrays.<org.xtuml.masl.translate.DomainTranslator>asList(mainDomainTranslator);
  }

  @Override
  public void translate ()
  {
    addRegistration();
    translateInterface();
  }


  private Expression getEnumMetaData ( final TypeDeclaration enumDeclaration, final CodeFile codeFile, final Namespace namespace )
  {
    final Function initMetaData = new Function("get_" + Mangler.mangleName(enumDeclaration) + "_MetaData", namespace);
    initMetaData.setReturnType(new TypeUsage(enumMetaData));

    codeFile.addFunctionDeclaration(initMetaData);
    codeFile.addFunctionDefinition(initMetaData);

    final CodeBlock objectBlock = initMetaData.getCode();

    final EnumerateType enumerate = (EnumerateType)enumDeclaration.getTypeDefinition();

    final Variable enumTemp = new Variable(new TypeUsage(enumMetaData),
                                           "enumeration",
                                           getTypeId(enumerate.getTypeDeclaration()),
                                           Literal
                                                  .createStringLiteral(enumDeclaration.getName()));
    objectBlock.appendStatement(enumTemp.asStatement());

    final EnumerationTranslator enumTranslator = mainDomainTranslator.getTypes()
                                                                     .getEnumerateTranslator(enumerate.getTypeDeclaration());

    for ( final EnumerateItem element : enumerate.getItems() )
    {
      objectBlock.appendStatement(addValue.asFunctionCall(enumTemp.asExpression(),
                                                          false,
                                                          getEnumeratorMetaData(element, enumTranslator)).asStatement());
    }
    objectBlock.appendStatement(new ReturnStatement(enumTemp.asExpression()));

    return initMetaData.asFunctionCall();
  }

  private Expression getStructureMetaData ( final TypeDeclaration structDeclaration,
                                            final CodeFile codeFile,
                                            final Namespace namespace )
  {
    final Function initMetaData = new Function("get_" + Mangler.mangleName(structDeclaration) + "_MetaData", namespace);
    initMetaData.setReturnType(new TypeUsage(structMetaData));

    codeFile.addFunctionDeclaration(initMetaData);
    codeFile.addFunctionDefinition(initMetaData);

    final CodeBlock objectBlock = initMetaData.getCode();


    final StructureType struct = (StructureType)structDeclaration.getTypeDefinition();

    final Variable structTemp = new Variable(new TypeUsage(structMetaData),
                                             "structure",
                                             getTypeId(struct.getTypeDeclaration()),
                                             Literal
                                                    .createStringLiteral(structDeclaration.getName()));
    objectBlock.appendStatement(structTemp.asStatement());

    for ( final StructureElement element : struct.getElements() )
    {
      objectBlock.appendStatement(addAttribute.asFunctionCall(structTemp.asExpression(), false, getElementMetaData(element))
                                              .asStatement());
    }

    objectBlock.appendStatement(new ReturnStatement(structTemp.asExpression()));

    return initMetaData.asFunctionCall();

  }

  private void translateInterface ()
  {

    final Namespace namespace = new Namespace("init_interface_" + Mangler.mangleName(domain), new Namespace(""));

    final Expression getId = mainDomainTranslator.getDomainId();

    final Function initMetaData = new Function("initDomainMetaData", namespace);
    initMetaData.setReturnType(new TypeUsage(domainMetaData));
    final Variable interfaceVar = new Variable(new TypeUsage(domainMetaData),
                                               "domain",
                                               getId,
                                               Literal.createStringLiteral(domain.getName()),
                                               Literal.TRUE);

    final StatementGroup initialisationCode = new StatementGroup();

    final Expression interfaceInstance = interfaceVar.asExpression();
    initMetaData.getCode().appendStatement(interfaceVar.asStatement());
    initMetaData.getCode().appendStatement(initialisationCode);

    initMetaData.getCode().appendStatement(new ReturnStatement(interfaceInstance));
    interfaceCodeFile.addFunctionDefinition(initMetaData);


    final Function getMetaData = new Function("getDomainMetaData", namespace);
    getMetaData.setReturnType(new TypeUsage(domainMetaData, TypeUsage.Reference));
    final Variable instance = new Variable(new TypeUsage(domainMetaData),
                                           "domain",
                                           initMetaData
                                                       .asFunctionCall());
    instance.setStatic(true);
    getMetaData.getCode().appendStatement(instance.asStatement());
    getMetaData.getCode().appendStatement(new ReturnStatement(instance.asExpression()));

    interfaceCodeFile.addFunctionDefinition(getMetaData);

    final Expression addDomain = new Function("addDomain").asFunctionCall(processInstance,
                                                                          false,
                                                                          getId,
                                                                          getMetaData.asFunctionPointer());

    final Variable registered = new Variable(new TypeUsage(FundamentalType.BOOL), "registered", namespace, addDomain);
    interfaceCodeFile.addVariableDefinition(registered);

    for ( final TypeDeclaration type : domain.getTypes() )
    {
      if ( type.getVisibility() == org.xtuml.masl.metamodel.common.Visibility.PUBLIC )
      {
        if ( type.getTypeDefinition() instanceof EnumerateType )
        {
          initialisationCode.appendStatement(addEnumerate.asFunctionCall(interfaceInstance,
                                                                         false,
                                                                         getEnumMetaData(type, interfaceCodeFile, namespace))
                                                         .asStatement());
        }
        else if ( type.getTypeDefinition() instanceof StructureType )
        {
          initialisationCode.appendStatement(addStructure.asFunctionCall(interfaceInstance,
                                                                         false,
                                                                         getStructureMetaData(type, interfaceCodeFile, namespace))
                                                         .asStatement());
        }
      }
    }
  }

  private void addRegistration ()
  {
    final Namespace namespace = new Namespace("init_" + Mangler.mangleName(domain), new Namespace(""));

    final Expression getId = mainDomainTranslator.getDomainId();

    final Function initMetaData = new Function("initDomainMetaData", namespace);
    initMetaData.setReturnType(new TypeUsage(domainMetaData));
    final Variable domainVar = new Variable(new TypeUsage(domainMetaData),
                                            "domain",
                                            getId,
                                            Literal.createStringLiteral(domain.getName()),
                                            Literal.FALSE);
    final StatementGroup initialisationCode = new StatementGroup();

    final Expression domainInstance = domainVar.asExpression();
    initMetaData.getCode().appendStatement(domainVar.asStatement());
    initMetaData.getCode().appendStatement(initialisationCode);
    initMetaData.getCode().appendStatement(new ReturnStatement(domainInstance));
    codeFile.addFunctionDefinition(initMetaData);

    final Function getMetaData = new Function("getDomainMetaData", namespace);
    getMetaData.setReturnType(new TypeUsage(domainMetaData, TypeUsage.Reference));
    final Variable instanceRef = new Variable(new TypeUsage(domainMetaData),
                                              "domain",
                                              initMetaData
                                                          .asFunctionCall());
    instanceRef.setStatic(true);
    getMetaData.getCode().appendStatement(instanceRef.asStatement());
    getMetaData.getCode().appendStatement(new ReturnStatement(instanceRef.asExpression()));

    codeFile.addFunctionDefinition(getMetaData);

    final Expression addDomain = new Function("addDomain").asFunctionCall(processInstance,
                                                                          false,
                                                                          getId,
                                                                          getMetaData.asFunctionPointer());

    final Variable registered = new Variable(new TypeUsage(FundamentalType.BOOL), "registered", namespace, addDomain);

    codeFile.addVariableDefinition(registered);

    for ( final TypeDeclaration type : domain.getTypes() )
    {
      if ( type.getTypeDefinition() instanceof EnumerateType )
      {
        initialisationCode.appendStatement(addEnumerate.asFunctionCall(domainInstance,
                                                                       false,
                                                                       getEnumMetaData(type, codeFile, namespace))
                                                       .asStatement());

      }
      else if ( type.getTypeDefinition() instanceof StructureType )
      {
        initialisationCode.appendStatement(addStructure.asFunctionCall(domainInstance,
                                                                       false,
                                                                       getStructureMetaData(type, codeFile, namespace))
                                                       .asStatement());
      }
    }

    for ( final DomainService service : domain.getServices() )
    {
      initialisationCode.appendStatement(addService.asFunctionCall(domainInstance,
                                                                   false,
                                                                   getDomainServiceMetaData(service, namespace))
                                                   .asStatement());
    }

    for ( final RelationshipDeclaration rel : domain.getRelationships() )
    {
      if ( rel instanceof SubtypeRelationshipDeclaration )
      {

        initialisationCode.appendStatement(addSuperSubtype.asFunctionCall(domainInstance,
                                                                          false,
                                                                          getDomainRelationshipMetaData(rel, namespace))
                                                          .asStatement());
      }
      else
      {
        initialisationCode.appendStatement(addRelationship.asFunctionCall(domainInstance,
                                                                          false,
                                                                          getDomainRelationshipMetaData(rel, namespace))
                                                          .asStatement());

      }
    }

    for ( final DomainTerminator terminator : domain.getTerminators() )
    {
      initialisationCode.appendStatement(addTerminator.asFunctionCall(domainInstance,
                                                                      false,
                                                                      getTerminatorMetaData(terminator, namespace))
                                                      .asStatement());

    }

    for ( final ObjectDeclaration object : domain.getObjects() )
    {
      initialisationCode.appendStatement(addObject.asFunctionCall(domainInstance, false, getObjectMetaData(object, namespace))
                                                  .asStatement());
    }

  }

  private Expression getAddObjRelationship ( final RelationshipSpecification spec, final Namespace namespace )
  {
    if ( spec.getRelationship() instanceof SubtypeRelationshipDeclaration )
    {
      return objRelMetaData.callConstructor(Literal.createStringLiteral(spec.getRelationship().getName()),
                                            spec
                                                .getConditional() ? Literal.TRUE : Literal.FALSE,
                                            mainDomainTranslator.getObjectTranslator(spec.getDestinationObject())
                                                                .getObjectId());

    }
    else
    {
      return objRelMetaData.callConstructor(Literal.createStringLiteral(spec.getRelationship().getName()),
                                            Literal
                                                   .createStringLiteral(spec.getRole()),
                                            spec.getCardinality() == MultiplicityType.MANY ? Literal.TRUE : Literal.FALSE,
                                            spec
                                                .getConditional() ? Literal.TRUE : Literal.FALSE,
                                            mainDomainTranslator.getObjectTranslator(spec.getDestinationObject())
                                                                .getObjectId());
    }
  }

  private Expression getAttributeMetaData ( final AttributeDeclaration att,
                                            final ObjectTranslator objectTranslator, final Namespace namespace )
  {
    final List<Expression> params = new ArrayList<Expression>();
    params.add(Literal.createStringLiteral(att.getName()));
    params.add(att.isIdentifier() ? Literal.TRUE : Literal.FALSE);

    params.add(Literal.createStringLiteral(att.getType().toString()));
    params.add(TypeTranslator.getTypeMetaData(att.getType()));

    if ( att.isReferential() )
    {
      params.add(RelationshipTranslator.getInstance(att.getRefAttDefs().get(0).getRelationship().getRelationship())
                                       .getRelationshipId());
    }

    if ( att.isReferential() )
    {
      final Function initMetaData = new Function("get" + Mangler.mangleName(att)
                                                 + "MetaData", namespace);
      initMetaData.setReturnType(new TypeUsage(attributeMetaData));

      codeFile.addFunctionDeclaration(initMetaData);
      codeFile.addFunctionDefinition(initMetaData);

      final CodeBlock block = initMetaData.getCode();

      final Variable attributeTemp = new Variable(new TypeUsage(attributeMetaData), "attribute", params);
      block.appendStatement(attributeTemp.asStatement());

      for ( final ReferentialAttributeDefinition refAtt : att.getRefAttDefs() )
      {
        block.appendStatement(addReferential.asFunctionCall(attributeTemp.asExpression(),
                                                            false,
                                                            getRefAttMetaData(refAtt)).asStatement());

      }

      block.appendStatement(new ReturnStatement(attributeTemp.asExpression()));

      return initMetaData.asFunctionCall();


    }
    else
    {
      if ( att.getDefault() != null )
      {
        final ExpressionTranslator exp = ExpressionTranslator.createTranslator(att.getDefault(), null);
        params.add(Boost.lexicalCast(new TypeUsage(Std.string), exp.getReadExpression()));
      }
      final Expression metaData = attributeMetaData.callConstructor(params);
      return metaData;
    }
  }

  private Expression getDomainRelationshipMetaData ( final RelationshipDeclaration relationship, final Namespace namespace )
  {
    final Expression id = RelationshipTranslator.getInstance(relationship).getRelationshipId();
    if ( relationship instanceof NormalRelationshipDeclaration )
    {
      final NormalRelationshipDeclaration rel = (NormalRelationshipDeclaration)relationship;
      final List<Expression> params = new ArrayList<Expression>();

      params.add(id);
      params.add(Literal.createStringLiteral(rel.getName()));

      params.add(mainDomainTranslator.getObjectTranslator(rel.getLeftObject()).getObjectId());
      params.add(Literal.createStringLiteral(rel.getRightRole()));
      params.add(rel.getRightMult() == MultiplicityType.MANY ? Literal.TRUE : Literal.FALSE);
      params.add(rel.getRightConditional() ? Literal.TRUE : Literal.FALSE);

      params.add(mainDomainTranslator.getObjectTranslator(rel.getRightObject()).getObjectId());
      params.add(Literal.createStringLiteral(rel.getLeftRole()));
      params.add(rel.getLeftMult() == MultiplicityType.MANY ? Literal.TRUE : Literal.FALSE);
      params.add(rel.getLeftConditional() ? Literal.TRUE : Literal.FALSE);

      return relationshipMetaData.callConstructor(params);
    }
    else if ( relationship instanceof AssociativeRelationshipDeclaration )
    {
      final AssociativeRelationshipDeclaration rel = (AssociativeRelationshipDeclaration)relationship;
      final List<Expression> params = new ArrayList<Expression>();

      params.add(id);
      params.add(Literal.createStringLiteral(rel.getName()));

      params.add(mainDomainTranslator.getObjectTranslator(rel.getLeftObject()).getObjectId());
      params.add(Literal.createStringLiteral(rel.getRightRole()));
      params.add(rel.getRightMult() == MultiplicityType.MANY ? Literal.TRUE : Literal.FALSE);
      params.add(rel.getRightConditional() ? Literal.TRUE : Literal.FALSE);

      params.add(mainDomainTranslator.getObjectTranslator(rel.getRightObject()).getObjectId());
      params.add(Literal.createStringLiteral(rel.getLeftRole()));
      params.add(rel.getLeftMult() == MultiplicityType.MANY ? Literal.TRUE : Literal.FALSE);
      params.add(rel.getLeftConditional() ? Literal.TRUE : Literal.FALSE);

      params.add(mainDomainTranslator.getObjectTranslator(rel.getAssocObject()).getObjectId());

      return relationshipMetaData.callConstructor(params);
    }
    else
    {
      final SubtypeRelationshipDeclaration rel = (SubtypeRelationshipDeclaration)relationship;

      final Function initMetaData = new Function("get_" + relationship.getName() + "_MetaData", namespace);
      initMetaData.setReturnType(new TypeUsage(superSubtypeMetaData));

      codeFile.addFunctionDeclaration(initMetaData);
      codeFile.addFunctionDefinition(initMetaData);

      final CodeBlock code = initMetaData.getCode();

      final Variable ssRel = new Variable(new TypeUsage(superSubtypeMetaData),
                                          "supersub",
                                          id,
                                          Literal.createStringLiteral(rel.getName()),
                                          mainDomainTranslator.getObjectTranslator(rel.getSupertype()).getObjectId());
      code.appendStatement(ssRel.asStatement());

      for ( final ObjectDeclaration subtype : rel.getSubtypes() )
      {
        code.appendExpression(addSubObject.asFunctionCall(ssRel.asExpression(),
                                                          false,
                                                          mainDomainTranslator
                                                                              .getObjectTranslator(subtype).getObjectId()));
      }

      code.appendStatement(new ReturnStatement(ssRel.asExpression()));

      return initMetaData.asFunctionCall();
    }
  }

  private Expression getDomainServiceMetaData ( final DomainService service, final Namespace namespace )
  {
    return getServiceMetaData(service,
                              mainDomainTranslator.getServiceTranslator(service).getServiceId(),
                              service.isExternal() ? externalFlag : (service.isScenario() ? scenarioFlag : domainServiceFlag),
                              namespace);
  }

  private Expression getElementMetaData ( final StructureElement att )
  {
    final List<Expression> params = new ArrayList<Expression>();
    params.add(Literal.createStringLiteral(att.getName()));
    params.add(Literal.FALSE);

    params.add(Literal.createStringLiteral(att.getType().toString()));
    params.add(TypeTranslator.getTypeMetaData(att.getType()));

    if ( att.getDefault() != null )
    {
      final ExpressionTranslator exp = ExpressionTranslator.createTranslator(att.getDefault(), null);
      params.add(Boost.lexicalCast(new TypeUsage(Std.string), exp.getReadExpression()));
    }

    return attributeMetaData.callConstructor(params);
  }


  private List<Expression> getEnumeratorMetaData ( final EnumerateItem enumerator,
                                                   final EnumerationTranslator enumTranslator )
  {
    final List<Expression> params = new ArrayList<Expression>();
    params.add(new Function("getValue").asFunctionCall(enumTranslator.getEnumerator(enumerator), false));
    params.add(Literal.createStringLiteral(enumerator.getName()));

    return params;
  }

  private Expression getEventMetaData ( final EventDeclaration event,
                                        final ObjectTranslator objectTranslator, final Namespace namespace )
  {

    final Function initMetaData = new Function("get_" + Mangler.mangleName(event)
                                               + "_MetaData", namespace);
    initMetaData.setReturnType(new TypeUsage(eventMetaData));

    codeFile.addFunctionDeclaration(initMetaData);
    codeFile.addFunctionDefinition(initMetaData);

    final CodeBlock eventBlock = initMetaData.getCode();

    Expression type;
    switch ( event.getType() )
    {
      case ASSIGNER:
        type = assignerEventFlag;
        break;
      case NORMAL:
        type = normalEventFlag;
        break;
      case CREATION:
        type = creationEventFlag;
        break;
      default:
        type = null;
    }

    final Expression parentObjectId = ObjectTranslator.getInstance(event.getParentObject()).getObjectId();

    final Variable eventTemp = new Variable(new TypeUsage(eventMetaData),
                                            "event",
                                            objectTranslator.getEventId(event),
                                            parentObjectId,
                                            type,
                                            Literal
                                                   .createStringLiteral(event.getName()));
    eventBlock.appendStatement(eventTemp.asStatement());

    for ( final ParameterDefinition param : event.getParameters() )
    {
      eventBlock.appendStatement(addParameter.asFunctionCall(eventTemp.asExpression(),
                                                             false,
                                                             TypeTranslator.getParameterMetaData(param)).asStatement());
    }

    eventBlock.appendStatement(new ReturnStatement(eventTemp.asExpression()));

    return initMetaData.asFunctionCall();

  }

  private Expression getObjectMetaData ( final ObjectDeclaration object, final Namespace namespace )
  {
    final Namespace objNamespace = new Namespace(Mangler.mangleName(object), namespace);

    final Function initMetaData = new Function("getMetaData", objNamespace);

    initMetaData.setReturnType(new TypeUsage(objectMetaData));

    codeFile.addFunctionDeclaration(initMetaData);
    codeFile.addFunctionDefinition(initMetaData);

    final CodeBlock objectBlock = initMetaData.getCode();

    final ObjectTranslator objectTranslator = mainDomainTranslator.getObjectTranslator(object);
    final Variable objectTemp = new Variable(new TypeUsage(objectMetaData),
                                             "object",
                                             objectTranslator.getObjectId(),
                                             Literal
                                                    .createStringLiteral(object.getName()),
                                             Literal.createStringLiteral(object.getKeyLetters()));
    objectBlock.appendStatement(objectTemp.asStatement());


    for ( final AttributeDeclaration att : object.getAttributes() )
    {
      objectBlock.appendStatement(addAttribute.asFunctionCall(objectTemp.asExpression(),
                                                              false,
                                                              getAttributeMetaData(att, objectTranslator, objNamespace))
                                              .asStatement());

    }

    for ( final RelationshipSpecification rel : object.getRelationships() )
    {
      objectBlock.appendStatement(addRelationship.asFunctionCall(objectTemp.asExpression(),
                                                                 false,
                                                                 getAddObjRelationship(rel, objNamespace))
                                                 .asStatement());
    }

    for ( final ObjectService service : object.getServices() )
    {
      objectBlock.appendStatement(addService.asFunctionCall(objectTemp.asExpression(),
                                                            false,
                                                            getObjectServiceMetaData(service, objNamespace))
                                            .asStatement());
    }

    for ( final State state : object.getStates() )
    {
      objectBlock.appendStatement(addState.asFunctionCall(objectTemp.asExpression(),
                                                          false,
                                                          getStateMetaData(state, objectTranslator, objNamespace))
                                          .asStatement());

    }

    for ( final EventDeclaration event : object.getAllEvents() )
    {
      objectBlock.appendStatement(addEvent.asFunctionCall(objectTemp.asExpression(),
                                                          false,
                                                          getEventMetaData(event, objectTranslator, objNamespace)).asStatement());
    }

    objectBlock.appendStatement(new ReturnStatement(objectTemp.asExpression()));

    return initMetaData.asFunctionCall();


  }

  private Expression getObjectServiceMetaData ( final ObjectService service, final Namespace namespace )
  {
    return getServiceMetaData(service,
                              ObjectServiceTranslator.getInstance(service).getServiceId(),
                              service.isInstance() ? instanceServiceFlag : objectServiceFlag,
                              namespace);
  }

  private Expression getRefAttMetaData ( final ReferentialAttributeDefinition refAtt )
  {
    return RelationshipTranslator.getInstance(refAtt.getRelationship().getRelationship())
                                 .getRelationshipId();
  }

  private void findCodeLines ( final List<Expression> lines, final org.xtuml.masl.metamodel.code.Statement statement )
  {
    // Cope with native services
    if ( statement == null )
    {
      return;
    }

    lines.add(new Literal(statement.getLineNumber()));
    for ( final org.xtuml.masl.metamodel.code.Statement child : statement.getChildStatements() )
    {
      findCodeLines(lines, child);
    }

  }

  private Expression getServiceMetaData ( final Service service,
                                          final Expression serviceId,
                                          final Expression serviceType,
                                          final Namespace namespace )
  {
    final Function initMetaData = new Function("get_" + Mangler.mangleName(service) + "_MetaData", namespace);
    initMetaData.setReturnType(new TypeUsage(serviceMetaData));

    codeFile.addFunctionDeclaration(initMetaData);
    codeFile.addFunctionDefinition(initMetaData);

    final CodeBlock serviceBlock = initMetaData.getCode();

    final List<Expression> params = new ArrayList<Expression>();
    params.add(serviceId);
    params.add(serviceType);
    params.add(Literal.createStringLiteral(service.getName()));
    if ( service.getReturnType() != null )
    {
      params.add(Literal.createStringLiteral(service.getReturnType().toString()));
      params.add(TypeTranslator.getTypeMetaData(service.getReturnType()));
    }

    final List<Expression> lines = new ArrayList<Expression>();
    findCodeLines(lines, service.getCode());
    final Variable linesVar = new Variable(new TypeUsage(FundamentalType.INT), "lines", new AggregateInitialiser(lines));
    linesVar.setArraySize(0);
    params.add(Std.vector(new TypeUsage(FundamentalType.INT)).callConstructor(linesVar.asExpression(),
                                                                              new BinaryExpression(linesVar.asExpression(),
                                                                                                   BinaryOperator.PLUS,
                                                                                                   new Literal(lines.size()))));
    serviceBlock.appendStatement(linesVar.asStatement());


    params.add(Literal.createStringLiteral(service.getFileName() == null ? "" : service.getFileName()));
    params.add(Literal.createStringLiteral(service.getFileHash() == null ? "" : service.getFileHash()));

    final Variable serviceTemp = new Variable(new TypeUsage(serviceMetaData), "service", params);

    serviceBlock.appendStatement(serviceTemp.asStatement());

    for ( final ParameterDefinition param : service.getParameters() )
    {
      serviceBlock.appendStatement(addParameter.asFunctionCall(serviceTemp.asExpression(),
                                                               false,
                                                               TypeTranslator.getParameterMetaData(param)).asStatement());
    }

    for ( final VariableDefinition variable : service.getLocalVariables() )
    {
      serviceBlock.appendStatement(addLocalVar.asFunctionCall(serviceTemp.asExpression(),
                                                              false,
                                                              TypeTranslator.getLocalVarMetaData(variable)).asStatement());
    }

    serviceBlock.appendStatement(new ReturnStatement(serviceTemp.asExpression()));

    return initMetaData.asFunctionCall();
  }

  private Expression getStateMetaData ( final State state, final ObjectTranslator objectTranslator, final Namespace namespace )
  {

    final Function initMetaData = new Function("get_" + Mangler.mangleName(state)
                                               + "_MetaData", namespace);
    initMetaData.setReturnType(new TypeUsage(stateMetaData));

    codeFile.addFunctionDeclaration(initMetaData);
    codeFile.addFunctionDefinition(initMetaData);

    final CodeBlock stateBlock = initMetaData.getCode();

    final StateActionTranslator mainActionTranslator = objectTranslator.getStateActionTranslator(state);

    Expression type;
    switch ( state.getType() )
    {
      case ASSIGNER:
        type = assignerStateFlag;
        break;
      case ASSIGNER_START:
        type = startStateFlag;
        break;
      case NORMAL:
        type = normalStateFlag;
        break;
      case CREATION:
        type = creationStateFlag;
        break;
      case TERMINAL:
        type = terminalStateFlag;
        break;
      default:
        type = null;
    }

    final List<Expression> params = new ArrayList<Expression>();
    params.add(mainActionTranslator.getStateId());
    params.add(type);
    params.add(Literal.createStringLiteral(state.getName()));

    final List<Expression> lines = new ArrayList<Expression>();
    findCodeLines(lines, state.getCode());
    final Variable linesVar = new Variable(new TypeUsage(FundamentalType.INT), "lines", new AggregateInitialiser(lines));
    linesVar.setArraySize(0);
    params.add(Std.vector(new TypeUsage(FundamentalType.INT)).callConstructor(linesVar.asExpression(),
                                                                              new BinaryExpression(linesVar.asExpression(),
                                                                                                   BinaryOperator.PLUS,
                                                                                                   new Literal(lines.size()))));
    stateBlock.appendStatement(linesVar.asStatement());

    params.add(Literal.createStringLiteral(state.getFileName() == null ? "" : state.getFileName()));

    params.add(Literal.createStringLiteral(state.getFileHash() == null ? "" : state.getFileHash()));

    final Variable stateTemp = new Variable(new TypeUsage(stateMetaData), "state", params);
    stateBlock.appendStatement(stateTemp.asStatement());

    for ( final ParameterDefinition param : state.getParameters() )
    {
      stateBlock.appendStatement(addParameter.asFunctionCall(stateTemp.asExpression(),
                                                             false,
                                                             TypeTranslator.getParameterMetaData(param)).asStatement());
    }

    for ( final VariableDefinition variable : state.getLocalVariables() )
    {
      stateBlock.appendStatement(addLocalVar.asFunctionCall(stateTemp.asExpression(),
                                                            false,
                                                            TypeTranslator.getLocalVarMetaData(variable)).asStatement());
    }

    stateBlock.appendStatement(new ReturnStatement(stateTemp.asExpression()));

    return initMetaData.asFunctionCall();


  }

  private Expression getTerminatorMetaData ( final DomainTerminator terminator, final Namespace namespace )
  {
    final Namespace termNamespace = new Namespace(Mangler.mangleName(terminator),
                                                  namespace);

    final Function initMetaData = new Function("getMetaData", termNamespace);
    initMetaData.setReturnType(new TypeUsage(terminatorMetaData));

    codeFile.addFunctionDeclaration(initMetaData);
    codeFile.addFunctionDefinition(initMetaData);

    final CodeBlock termBlock = initMetaData.getCode();


    final TerminatorTranslator termTranslator = mainDomainTranslator.getTerminatorTranslator(terminator);
    final Variable termTemp = new Variable(new TypeUsage(terminatorMetaData),
                                           "terminator",
                                           termTranslator.getTerminatorId(),
                                           Literal
                                                  .createStringLiteral(terminator.getName()),
                                           Literal.createStringLiteral(terminator.getKeyLetters()));
    termBlock.appendStatement(termTemp.asStatement());


    for ( final DomainTerminatorService service : terminator.getServices() )
    {
      termBlock.appendStatement(addService.asFunctionCall(termTemp.asExpression(),
                                                          false,
                                                          getTerminatorServiceMetaData(service, termNamespace))
                                          .asStatement());

    }

    termBlock.appendStatement(new ReturnStatement(termTemp.asExpression()));

    return initMetaData.asFunctionCall();

  }

  private Expression getTerminatorServiceMetaData ( final DomainTerminatorService service, final Namespace namespace )
  {
    return getServiceMetaData(service,
                              TerminatorServiceTranslator.getInstance(service).getServiceId(),
                              terminatorServiceFlag,
                              namespace);
  }

  private final org.xtuml.masl.translate.main.DomainTranslator mainDomainTranslator;

  private final Library                                     library;

  private final Library                                     interfaceLibrary;

  private final CodeFile                                      codeFile;

  private final CodeFile                                      headerFile;

  private final CodeFile                                      interfaceCodeFile;

}
