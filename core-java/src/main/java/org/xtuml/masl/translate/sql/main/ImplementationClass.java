/*
 * Filename : ImplementationClass.java
 *
 * UK Crown Copyright (c) 2008. All Rights Reserved
 */
package org.xtuml.masl.translate.sql.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.xtuml.masl.cppgen.BinaryExpression;
import org.xtuml.masl.cppgen.BinaryOperator;
import org.xtuml.masl.cppgen.Class;
import org.xtuml.masl.cppgen.CodeBlock;
import org.xtuml.masl.cppgen.CodeFile;
import org.xtuml.masl.cppgen.DeclarationGroup;
import org.xtuml.masl.cppgen.Expression;
import org.xtuml.masl.cppgen.ExpressionStatement;
import org.xtuml.masl.cppgen.Function;
import org.xtuml.masl.cppgen.FundamentalType;
import org.xtuml.masl.cppgen.IfStatement;
import org.xtuml.masl.cppgen.Literal;
import org.xtuml.masl.cppgen.Namespace;
import org.xtuml.masl.cppgen.ReturnStatement;
import org.xtuml.masl.cppgen.Statement;
import org.xtuml.masl.cppgen.Std;
import org.xtuml.masl.cppgen.ThrowStatement;
import org.xtuml.masl.cppgen.TypeUsage;
import org.xtuml.masl.cppgen.TypedefType;
import org.xtuml.masl.cppgen.Variable;
import org.xtuml.masl.cppgen.Visibility;
import org.xtuml.masl.metamodel.object.AttributeDeclaration;
import org.xtuml.masl.metamodel.object.IdentifierDeclaration;
import org.xtuml.masl.metamodel.object.ObjectDeclaration;
import org.xtuml.masl.metamodel.relationship.AssociativeRelationshipDeclaration;
import org.xtuml.masl.metamodel.relationship.NormalRelationshipDeclaration;
import org.xtuml.masl.metamodel.relationship.SubtypeRelationshipDeclaration;
import org.xtuml.masl.metamodel.statemodel.EventDeclaration;
import org.xtuml.masl.translate.main.Architecture;
import org.xtuml.masl.translate.main.Boost;
import org.xtuml.masl.translate.main.Mangler;
import org.xtuml.masl.translate.main.object.StateMachineTranslator;


public class ImplementationClass
    implements GeneratedClass
{

  static public final String                                KEY_NAME               = "ImplementationClass";

  private final String                                      className;
  private final Namespace                                   namespace;
  private final ObjectDeclaration                           objectDeclaration;
  private final ObjectTranslator                            objectTranslator;
  private final HashMap<AttributeDeclaration, Variable>     attributeMap           = new HashMap<AttributeDeclaration, Variable>();
  private final HashMap<AttributeDeclaration, Function>     setterMethods          = new HashMap<AttributeDeclaration, Function>();

  private final HashMap<IdentifierDeclaration, Function>    identifierKeyGetterFns = new HashMap<IdentifierDeclaration, Function>();
  private final HashMap<IdentifierDeclaration, TypedefType> identifierKeyTypes     = new HashMap<IdentifierDeclaration, TypedefType>();

  private CodeFile                                          bodyFile;
  private CodeFile                                          headerFile;
  private final Class                                       implementationClass;

  TypedefType                                               keyTypeDef;

  private Function                                          constructor;
  private Function                                          archIdConstructor;

  private DeclarationGroup                                  typedefs;
  private DeclarationGroup                                  rdbmsFunctions;
  private DeclarationGroup                                  rdbmsDataMembers;
  private DeclarationGroup                                  constructors;
  private DeclarationGroup                                  getters;
  private DeclarationGroup                                  setters;
  private DeclarationGroup                                  relationshipCounts;
  private DeclarationGroup                                  relationshipLinkers;
  private DeclarationGroup                                  relationshipNavs;
  private DeclarationGroup                                  attributes;

  private Variable                                          archId;
  private Variable                                          dirtyVariable;
  private Variable                                          constructFromDbVar;

  public ImplementationClass ( final ObjectTranslator parent, final ObjectDeclaration declaration, final Namespace topLevelNamespace )
  {
    objectTranslator = parent;
    objectDeclaration = declaration;

    className = Mangler.mangleName(objectDeclaration);
    namespace = new Namespace(Mangler.mangleName(objectDeclaration.getDomain()), topLevelNamespace);

    implementationClass = new Class(className, namespace);
    implementationClass.addSuperclass(getMainObjectTranslator().getMainClass(), Visibility.PUBLIC);
  }

  @Override
  public Class getCppClass ()
  {
    return implementationClass;
  }

  @Override
  public String getClassName ()
  {
    return KEY_NAME;
  }

  public Function getSetterMethod ( final AttributeDeclaration attribute )
  {
    return setterMethods.get(attribute);
  }

  public TypedefType getKeyType ( final IdentifierDeclaration identifier )
  {
    return identifierKeyTypes.get(identifier);
  }

  public Function getKeyGetterFn ( final IdentifierDeclaration identifier )
  {
    return identifierKeyGetterFns.get(identifier);
  }

  @Override
  public void translateAttributes ()
  {
    initialise();

    for ( final AttributeDeclaration attributeDecl : objectDeclaration.getAttributes() )
    {

      if ( attributeDecl.isIdentifier() || !attributeDecl.isReferential() )
      {
        // get the getter/setter declarations for this attribute from the main
        // ObjectTranslator, and provide implementations for this class.
        final Function getter = implementationClass.redefineFunction(getters,
                                                                     getMainObjectTranslator().getAttributeGetter(attributeDecl),
                                                                     Visibility.PUBLIC);
        Function setter = null;
        if ( attributeDecl.isIdentifier() )
        {
          // By default the base class does not produce setter functions for any
          // identifier
          // types as it provides a createInstance method with the identifier
          // attributes passed
          // as parameters. In the Sqlite implementation the de-serialisation of
          // a row into the
          // required object instance needs setters for every object attribute
          // so it can set the
          // required values.
          final TypeUsage type = getMainDomainTranslator().getTypes().getType(attributeDecl.getType());
          setter = implementationClass.createMemberFunction(setters, "set_" + Mangler.mangleName(attributeDecl), Visibility.PUBLIC);
          setter.createParameter(type.getOptimalParameterType(), "value");
        }
        else
        {
          setter = implementationClass.redefineFunction(setters,
                                                        getMainObjectTranslator().getAttributeSetter(attributeDecl),
                                                        Visibility.PUBLIC);
        }

        // Create member variable and required cpp code for inline getter.
        final TypeUsage type = getMainDomainTranslator().getTypes().getType(attributeDecl.getType());
        final Variable attribute = implementationClass.createMemberVariable(attributes,
                                                                            Mangler.mangleName(attributeDecl),
                                                                            type,
                                                                            Visibility.PRIVATE);
        attributeMap.put(attributeDecl, attribute);
        getter.getCode().appendStatement(new ReturnStatement(attribute.asExpression()));
        getter.declareInClass(true);

        // Add the attribute to the constructor parameter list and initialise as
        // required.
        final Variable constructorParam = constructor.createParameter(type.getOptimalParameterType(),
                                                                      Mangler.mangleName(attributeDecl));
        constructor.setInitialValue(attribute, constructorParam.asExpression());

        if ( setter != null )
        {
          setter.getCode().appendExpression(new BinaryExpression(new BinaryExpression(implementationClass.getThis().asExpression(),
                                                                                      BinaryOperator.PTR_REF,
                                                                                      attribute.asExpression()),
                                                                 BinaryOperator.ASSIGN,
                                                                 (setter.getParameters().get(0)).asExpression()));
          setter.getCode().appendExpression(new Function("markAsModified").asFunctionCall());
          setter.declareInClass(true);

          setterMethods.put(attributeDecl, setter);
        }
      }
    }

    // Need to persist the current state of any active object
    // so add the required current state variable.
    addCurrentState();
    addPrimaryKeyType();
    addObjectKey();
  }

  @Override
  public void translateFind ()
  {

  }

  @Override
  public void translateRelationships ()
  {
    for ( final NormalRelationshipDeclaration normalRelationshipDecl : objectTranslator.getNormalRelationships() )
    {
      defineNormalLinkerFns(normalRelationshipDecl);
      defineNormalNavFns(normalRelationshipDecl);
    }

    for ( final AssociativeRelationshipDeclaration assocRelationshipDecl : objectTranslator.getAssociativeRelationships() )
    {
      defineAssociativeLinkerFns(assocRelationshipDecl);
      defineAssociativeNavFns(assocRelationshipDecl);
    }

    for ( final SubtypeRelationshipDeclaration subtypeRelationshipDecl : objectTranslator.getSubTypeRelationships() )
    {
      defineIsSuperSubLinkedFn(subtypeRelationshipDecl);
      defineSuptypeLinkerFns(subtypeRelationshipDecl);
      defineSuperSubTypeNavFns(subtypeRelationshipDecl);
    }
  }

  @Override
  public void translateNavigations ()
  {
    // Implement any kind of dynamic navigations that have taken place in the
    // parsed MASL. These will have been
    // abstracted into interface methods on the base class rather than being
    // implemnetated at the call site. This
    // will cover any kind of nested navigations.
  }

  @Override
  public void translateEvents ()
  {
    // The MASL Timer implementation enables generated events to be delivered
    // at a pre-determined time. This feature needs to be able to persist when
    // an application is stopped and warm started. Therefore the parameters of
    // delayed events need to be persisted. The generated interface code
    // provides
    // a series of interfaces to enable a persistent implementation to store
    // parameters
    // associated with a delayedevent.
    //
    // example methods that need to be implemented:
    //
    // virtual ::boost::shared_ptr< ::SWA::Event>
    // create_delayed_maslo_Active_Job_maslev_task_is_complete ( const uint32_t
    // timerId,
    // const int32_t maslp_id_of_task,
    // const int sourceObj = -1,
    // const ::SWA::IdType sourceInstance = 0 ) = 0;
    //
    // virtual void generate_delayed_maslo_Active_Job_maslev_task_is_complete (
    // const uint32_t timerId,
    // const int srcObj,
    // const ::SWA::IdType srcInstance ) = 0;

    for ( final EventDeclaration event : objectDeclaration.getAllEvents() )
    {
      objectTranslator.getDatabase().getDatabaseTraits().addEventCode(namespace, bodyFile, objectDeclaration, event);
    }
  }

  private void initialise ()
  {
    bodyFile = objectTranslator.getFrameworkTranslator().getLibrary().createBodyFile(objectTranslator.getDatabase().getDatabaseTraits().getName() + Mangler.mangleFile(objectDeclaration));
    headerFile = objectTranslator.getFrameworkTranslator().getLibrary().createPrivateHeader(objectTranslator.getDatabase().getDatabaseTraits().getName() + Mangler.mangleFile(objectDeclaration));

    headerFile.addClassDeclaration(implementationClass);
    typedefs = implementationClass.createDeclarationGroup("Type definitions");
    constructors = implementationClass.createDeclarationGroup("Constructors and Destructors");
    setters = implementationClass.createDeclarationGroup("Setters for each object attribute");
    getters = implementationClass.createDeclarationGroup("Getters for each object attribute");
    relationshipNavs = implementationClass.createDeclarationGroup("Relationship Navigators");
    relationshipCounts = implementationClass.createDeclarationGroup("Relationship Counts");
    relationshipLinkers = implementationClass.createDeclarationGroup("Relationship Linkers");
    rdbmsFunctions = implementationClass.createDeclarationGroup("Rdbms required functions");
    attributes = implementationClass.createDeclarationGroup("Storage for each object attribute");
    rdbmsDataMembers = implementationClass.createDeclarationGroup("Rdbms required data members");

    // Add the constructor
    archIdConstructor = implementationClass.createConstructor(constructors, Visibility.PUBLIC);
    constructor = implementationClass.createConstructor(constructors, Visibility.PUBLIC);
    bodyFile.addFunctionDefinition(constructor);
    bodyFile.addFunctionDefinition(archIdConstructor);

    rdbmsSpecifics();
  }

  private void defineNormalLinkerFns ( final NormalRelationshipDeclaration normalRelationshipDecl )
  {
    final ObjectDeclaration leftObjectDecl = normalRelationshipDecl.getLeftObject();
    final ObjectDeclaration rightObjectDecl = normalRelationshipDecl.getRightObject();

    if ( objectDeclaration == leftObjectDecl )
    {
      final Function linkRhsFunction = getMainObjectTranslator().getRelationshipTranslator(normalRelationshipDecl.getLeftToRightSpec())
                                                                .getSubclassOverrides()
                                                                .getSingleLinkFunction();
      final Function unlinkRhsFunction = getMainObjectTranslator().getRelationshipTranslator(normalRelationshipDecl.getLeftToRightSpec())
                                                                  .getSubclassOverrides()
                                                                  .getSingleUnlinkFunction();
      final ObjectTranslator relatedObjTranslator = objectTranslator.getFrameworkTranslator().getObjectTranslator(rightObjectDecl);
      implementNormalLinkerFns(linkRhsFunction, unlinkRhsFunction, relatedObjTranslator);
    }

    if ( objectDeclaration == rightObjectDecl )
    {
      final Function linkLhsFunction = getMainObjectTranslator().getRelationshipTranslator(normalRelationshipDecl.getRightToLeftSpec())
                                                                .getSubclassOverrides()
                                                                .getSingleLinkFunction();
      final Function unlinkLhsFunction = getMainObjectTranslator().getRelationshipTranslator(normalRelationshipDecl.getRightToLeftSpec())
                                                                  .getSubclassOverrides()
                                                                  .getSingleUnlinkFunction();
      final ObjectTranslator relatedObjTranslator = objectTranslator.getFrameworkTranslator().getObjectTranslator(leftObjectDecl);
      implementNormalLinkerFns(linkLhsFunction, unlinkLhsFunction, relatedObjTranslator);
    }
  }

  private void defineNormalNavFns ( final NormalRelationshipDeclaration normalRelationshipDecl )
  {
    final ObjectDeclaration leftObjectDecl = normalRelationshipDecl.getLeftObject();
    final ObjectDeclaration rightObjectDecl = normalRelationshipDecl.getRightObject();

    if ( objectDeclaration == leftObjectDecl )
    {
      final Function countRhsFunction = getMainObjectTranslator().getRelationshipTranslator(normalRelationshipDecl.getLeftToRightSpec())
                                                                 .getSubclassOverrides()
                                                                 .getCountFunction();
      final Function navigateRhsFunction = getMainObjectTranslator().getRelationshipTranslator(normalRelationshipDecl.getLeftToRightSpec())
                                                                    .getSubclassOverrides()
                                                                    .getNavigateFunction();
      implementNormalNavFns(navigateRhsFunction, countRhsFunction);
    }

    if ( objectDeclaration == rightObjectDecl )
    {
      final Function countLhsFunction = getMainObjectTranslator().getRelationshipTranslator(normalRelationshipDecl.getRightToLeftSpec())
                                                                 .getSubclassOverrides()
                                                                 .getCountFunction();
      final Function navigateLhsFunction = getMainObjectTranslator().getRelationshipTranslator(normalRelationshipDecl.getRightToLeftSpec())
                                                                    .getSubclassOverrides()
                                                                    .getNavigateFunction();
      implementNormalNavFns(navigateLhsFunction, countLhsFunction);
    }
  }

  private void implementNormalLinkerFns ( final Function linkFunction,
                                          final Function unlinkFunction,
                                          final ObjectTranslator relatedObjectTranslator )
  {
    final Function baseClassLinkFn = implementationClass.redefineFunction(relationshipLinkers, linkFunction, Visibility.PUBLIC);
    final Function baseClassUnlinkFn = implementationClass.redefineFunction(relationshipLinkers, unlinkFunction, Visibility.PUBLIC);

    createLinkFnBody(baseClassLinkFn, relatedObjectTranslator);
    createLinkFnBody(baseClassUnlinkFn, relatedObjectTranslator);
    bodyFile.addFunctionDefinition(baseClassLinkFn);
    bodyFile.addFunctionDefinition(baseClassUnlinkFn);
  }

  private void implementNormalNavFns ( final Function navigateFunction, final Function countFunction )
  {
    final Function baseClassNavigateFn = implementationClass.redefineFunction(relationshipNavs, navigateFunction, Visibility.PUBLIC);
    createNavigateFnBody(baseClassNavigateFn);
    bodyFile.addFunctionDefinition(baseClassNavigateFn);

    final Function baseClassCountFn = implementationClass.redefineFunction(relationshipCounts, countFunction, Visibility.PUBLIC);
    createCountFnBody(baseClassCountFn);
    bodyFile.addFunctionDefinition(baseClassCountFn);
  }

  private void defineAssociativeLinkerFns ( final AssociativeRelationshipDeclaration assocRelationshipDecl )
  {
    final ObjectDeclaration lhsObjectDecl = assocRelationshipDecl.getLeftObject();
    final ObjectDeclaration rhsObjectDecl = assocRelationshipDecl.getRightObject();
    final ObjectDeclaration assObjectDecl = assocRelationshipDecl.getAssocObject();

    if ( objectDeclaration == lhsObjectDecl )
    {
      final Function linkRhsFunction = getMainObjectTranslator().getRelationshipTranslator(assocRelationshipDecl.getLeftToRightSpec())
                                                                .getSubclassOverrides()
                                                                .getSingleLinkFunction();
      final Function unlinkRhsFunction = getMainObjectTranslator().getRelationshipTranslator(assocRelationshipDecl.getLeftToRightSpec())
                                                                  .getSubclassOverrides()
                                                                  .getSingleUnlinkFunction();
      final ObjectTranslator implRhsTranslator = objectTranslator.getFrameworkTranslator().getObjectTranslator(rhsObjectDecl);
      final ObjectTranslator implAssocTranslator = objectTranslator.getFrameworkTranslator().getObjectTranslator(assObjectDecl);
      implementAssociativeLinkerFns(linkRhsFunction, unlinkRhsFunction, implRhsTranslator, implAssocTranslator);
    }

    if ( objectDeclaration == rhsObjectDecl )
    {
      final Function linkLhsFunction = getMainObjectTranslator().getRelationshipTranslator(assocRelationshipDecl.getRightToLeftSpec())
                                                                .getSubclassOverrides()
                                                                .getSingleLinkFunction();
      final Function unlinkLhsFunction = getMainObjectTranslator().getRelationshipTranslator(assocRelationshipDecl.getRightToLeftSpec())
                                                                  .getSubclassOverrides()
                                                                  .getSingleUnlinkFunction();
      final ObjectTranslator implLhsTranslator = objectTranslator.getFrameworkTranslator().getObjectTranslator(lhsObjectDecl);
      final ObjectTranslator implAssocTranslator = objectTranslator.getFrameworkTranslator().getObjectTranslator(assObjectDecl);
      implementAssociativeLinkerFns(linkLhsFunction, unlinkLhsFunction, implLhsTranslator, implAssocTranslator);
    }

    if ( objectDeclaration == assObjectDecl )
    {
      // Associative objects are only linked through their parent objects
      // so will not have link/unlink methods to implements for associative
      // objects.
    }
  }

  private void defineAssociativeNavFns ( final AssociativeRelationshipDeclaration assocRelationshipDecl )
  {
    final ObjectDeclaration lhsObjectDecl = assocRelationshipDecl.getLeftObject();
    final ObjectDeclaration rhsObjectDecl = assocRelationshipDecl.getRightObject();
    final ObjectDeclaration assObjectDecl = assocRelationshipDecl.getAssocObject();
    if ( objectDeclaration == lhsObjectDecl )
    {
      final Function countRhsFunction = getMainObjectTranslator().getRelationshipTranslator(assocRelationshipDecl.getLeftToRightSpec())
                                                                 .getSubclassOverrides()
                                                                 .getCountFunction();
      final Function countAssFunction = getMainObjectTranslator().getRelationshipTranslator(assocRelationshipDecl.getLeftToAssocSpec())
                                                                 .getSubclassOverrides()
                                                                 .getCountFunction();
      final Function navigateRhsFunction = getMainObjectTranslator().getRelationshipTranslator(assocRelationshipDecl.getLeftToRightSpec())
                                                                    .getSubclassOverrides()
                                                                    .getNavigateFunction();
      final Function navigateAssFunction = getMainObjectTranslator().getRelationshipTranslator(assocRelationshipDecl.getLeftToAssocSpec())
                                                                    .getSubclassOverrides()
                                                                    .getNavigateFunction();
      final Function correlateFunction = getMainObjectTranslator().getRelationshipTranslator(assocRelationshipDecl.getLeftToRightSpec())
                                                                  .getSubclassOverrides()
                                                                  .getSingleCorrelateFunction();
      implementAssociativeNavFns(navigateRhsFunction, navigateAssFunction, countRhsFunction, countAssFunction);
      implementCorrelateFunction(correlateFunction, lhsObjectDecl, rhsObjectDecl);
    }

    if ( objectDeclaration == rhsObjectDecl )
    {
      final Function countLhsFunction = getMainObjectTranslator().getRelationshipTranslator(assocRelationshipDecl.getRightToLeftSpec())
                                                                 .getSubclassOverrides()
                                                                 .getCountFunction();
      final Function countAssFunction = getMainObjectTranslator().getRelationshipTranslator(assocRelationshipDecl.getRightToAssocSpec())
                                                                 .getSubclassOverrides()
                                                                 .getCountFunction();
      final Function navigateLhsFunction = getMainObjectTranslator().getRelationshipTranslator(assocRelationshipDecl.getRightToLeftSpec())
                                                                    .getSubclassOverrides()
                                                                    .getNavigateFunction();
      final Function navigateAssFunction = getMainObjectTranslator().getRelationshipTranslator(assocRelationshipDecl.getRightToAssocSpec())
                                                                    .getSubclassOverrides()
                                                                    .getNavigateFunction();
      final Function correlateFunction = getMainObjectTranslator().getRelationshipTranslator(assocRelationshipDecl.getRightToLeftSpec())
                                                                  .getSubclassOverrides()
                                                                  .getSingleCorrelateFunction();
      implementAssociativeNavFns(navigateLhsFunction, navigateAssFunction, countLhsFunction, countAssFunction);
      implementCorrelateFunction(correlateFunction, rhsObjectDecl, lhsObjectDecl);
    }

    if ( objectDeclaration == assObjectDecl )
    {
      final Function countLhsFunction = getMainObjectTranslator().getRelationshipTranslator(assocRelationshipDecl.getAssocToLeftSpec())
                                                                 .getSubclassOverrides()
                                                                 .getCountFunction();
      final Function countRhsFunction = getMainObjectTranslator().getRelationshipTranslator(assocRelationshipDecl.getAssocToRightSpec())
                                                                 .getSubclassOverrides()
                                                                 .getCountFunction();
      final Function navigateLhsFunction = getMainObjectTranslator().getRelationshipTranslator(assocRelationshipDecl.getAssocToLeftSpec())
                                                                    .getSubclassOverrides()
                                                                    .getNavigateFunction();
      final Function navigateRhsFunction = getMainObjectTranslator().getRelationshipTranslator(assocRelationshipDecl.getAssocToRightSpec())
                                                                    .getSubclassOverrides()
                                                                    .getNavigateFunction();
      implementAssociativeNavFns(navigateLhsFunction, navigateRhsFunction, countLhsFunction, countRhsFunction);
    }
  }

  private void implementAssociativeLinkerFns ( final Function linkFunction,
                                               final Function unlinkFunction,
                                               final ObjectTranslator implObjTranslator,
                                               final ObjectTranslator implAssocTranslator )
  {
    final Function assocClassLinkFn = implementationClass.redefineFunction(relationshipLinkers, linkFunction, Visibility.PUBLIC);
    final Function assocClassUnlinkFn = implementationClass.redefineFunction(relationshipLinkers, unlinkFunction, Visibility.PUBLIC);

    createAssociativeLinkFnBody(assocClassLinkFn, implObjTranslator, implAssocTranslator);
    createAssociativeLinkFnBody(assocClassUnlinkFn, implObjTranslator, implAssocTranslator);

    bodyFile.addFunctionDefinition(assocClassLinkFn);
    bodyFile.addFunctionDefinition(assocClassUnlinkFn);
  }

  private void implementAssociativeNavFns ( final Function navigateAFn,
                                            final Function navigateBFn,
                                            final Function countAFn,
                                            final Function countBFn )
  {
    final Function lhsClassNavigateFn = implementationClass.redefineFunction(relationshipNavs, navigateAFn, Visibility.PUBLIC);
    final Function rhsClassNavigateFn = implementationClass.redefineFunction(relationshipNavs, navigateBFn, Visibility.PUBLIC);
    createNavigateFnBody(lhsClassNavigateFn);
    createNavigateFnBody(rhsClassNavigateFn);

    bodyFile.addFunctionDefinition(lhsClassNavigateFn);
    bodyFile.addFunctionDefinition(rhsClassNavigateFn);

    final Function rhsClassCountFn = implementationClass.redefineFunction(relationshipCounts, countAFn, Visibility.PUBLIC);
    final Function lhsClassCountFn = implementationClass.redefineFunction(relationshipCounts, countBFn, Visibility.PUBLIC);
    createCountFnBody(rhsClassCountFn);
    createCountFnBody(lhsClassCountFn);
    bodyFile.addFunctionDefinition(rhsClassCountFn);
    bodyFile.addFunctionDefinition(lhsClassCountFn);
  }

  private void createAssociativeLinkFnBody ( final Function linkerFunction,
                                             final ObjectTranslator implObjTranslator,
                                             final ObjectTranslator implAssocTranslator )
  {
    final List<Variable> linkFnParameters = linkerFunction.getParameters();
    final String objParamName = linkFnParameters.get(0).getName();
    final String derviedObjVarName = "derived" + objParamName;

    final String assocParamName = linkFnParameters.get(1).getName();
    final String derviedAssocVarName = "derived" + assocParamName;

    // Create cpp line:
    // ::SWA::ObjectPtr<maslo_Find_Test_Object_G>
    // derviedRhs(rhs.downcast<maslo_Find_Test_Object_G>());
    final Function derivedObjDowncastFn = new Function("downcast");
    derivedObjDowncastFn.addTemplateSpecialisation(new TypeUsage(implObjTranslator.getClass(ImplementationClass.KEY_NAME)));
    final Expression downcastObjFnCall = derivedObjDowncastFn.asFunctionCall(linkFnParameters.get(0).asExpression(), false);
    final TypeUsage relatedObjType = new TypeUsage(implObjTranslator.getClass("ImplementationClass"));
    final Variable derivedObjVar = new Variable(new TypeUsage(Architecture.objectPtr(relatedObjType)),
                                                derviedObjVarName,
                                                new Expression[]
                                                  { downcastObjFnCall });
    linkerFunction.getCode().appendStatement(derivedObjVar.asStatement());

    // Create cpp line:
    // ::SWA::ObjectPtr<maslo_Find_Test_Object_H>
    // derviedAssoc(assoc.downcast<maslo_Find_Test_Object_H>());
    final Function derivedAssocDowncastFn = new Function("downcast");
    derivedAssocDowncastFn.addTemplateSpecialisation(new TypeUsage(implAssocTranslator.getClass(ImplementationClass.KEY_NAME)));
    final Expression downcastAssocFnCall = derivedAssocDowncastFn.asFunctionCall(linkFnParameters.get(1).asExpression(), false);
    final TypeUsage relatedAssocType = new TypeUsage(implAssocTranslator.getClass(ImplementationClass.KEY_NAME));
    final Variable derivedAssocVar = new Variable(new TypeUsage(Architecture.objectPtr(relatedAssocType)),
                                                  derviedAssocVarName,
                                                  new Expression[]
                                                    { downcastAssocFnCall });
    linkerFunction.getCode().appendStatement(derivedAssocVar.asStatement());

    // Create cpp line:
    // maslo_Find_Test_Object_HPopulation::getPopulation().link_R1_has_parent_Find_Test_Object_G(::SWA::ObjectPtr<maslo_Find_Test_Object_H>(this),
    // derviedRhs, derviedAssoc);
    final Expression getPopulationFnCall = objectTranslator.getClass(PopulationClass.KEY_NAME).callStaticFunction("getPopulation");
    final Expression derviedThis = Architecture.objectPtr(new TypeUsage(implementationClass)).callConstructor(new Literal("this"));
    final Function populationLinkFn = new Function(linkerFunction.getName());
    final Expression populationLinkFnCall = populationLinkFn.asFunctionCall(getPopulationFnCall, false, new Expression[]
      { derviedThis, derivedObjVar.asExpression(), derivedAssocVar.asExpression() });
    linkerFunction.getCode().appendExpression(populationLinkFnCall);

  }

  private void implementCorrelateFunction ( final Function correlateFunction,
                                            final ObjectDeclaration lhsObjectDecl,
                                            final ObjectDeclaration rhsObjectDecl )
  {
    final Function classCorrelateFn = implementationClass.redefineFunction(relationshipNavs, correlateFunction, Visibility.PUBLIC);
    bodyFile.addFunctionDefinition(classCorrelateFn);

    // Create cpp line:
    // ::SWA::ObjectPtr< maslo_Find_Test_Object_H>
    // self(const_cast<maslo_Find_Test_Object_H*>(this));
    final Expression constCastFnCall = Std.const_cast(new TypeUsage(implementationClass, TypeUsage.Pointer))
                                          .asFunctionCall(new Literal("this"));
    final TypeUsage relatedObjType = new TypeUsage(implementationClass);
    final Variable self = new Variable(new TypeUsage(Architecture.objectPtr(relatedObjType)), "self", new Expression[]
      { constCastFnCall });
    classCorrelateFn.getCode().appendStatement(self.asStatement());

    final ObjectTranslator derivedRhsObjTrans = objectTranslator.getFrameworkTranslator().getObjectTranslator(rhsObjectDecl);

    // Create cpp line:
    // ::SWA::ObjectPtr<maslo_Find_Test_Object_G>
    // derviedRhs(rhs.downcast<maslo_Find_Test_Object_G>());
    final Function downcastFn = new Function("downcast");
    final TypeUsage rhsObjType = new TypeUsage(derivedRhsObjTrans.getClass(ImplementationClass.KEY_NAME));
    downcastFn.addTemplateSpecialisation(rhsObjType);
    final Expression downcastFnCall = downcastFn.asFunctionCall(correlateFunction.getParameters().get(0).asExpression(), false);
    final Variable derivedRhs = new Variable(new TypeUsage(Architecture.objectPtr(rhsObjType)), "derivedRhs", new Expression[]
      { downcastFnCall });
    classCorrelateFn.getCode().appendStatement(derivedRhs.asStatement());

    // Create cpp line:
    // return
    // maslo_Find_Test_Object_HPopulation::getPopulation().correlate_R8_master_One_To_One_Link_Test_Object_L(self,rhs);
    final Expression getPopulationFnCall = objectTranslator.getClass(PopulationClass.KEY_NAME).callStaticFunction("getPopulation");
    final Function populationNavigateFn = new Function(classCorrelateFn.getName());

    final Expression populationCorrelateFnCall = populationNavigateFn.asFunctionCall(getPopulationFnCall, false, new Expression[]
      { self.asExpression(), derivedRhs.asExpression() });
    classCorrelateFn.getCode().appendStatement(new ReturnStatement(populationCorrelateFnCall));
  }

  private void defineIsSuperSubLinkedFn ( final SubtypeRelationshipDeclaration subtypeRelationshipDecl )
  {
    // For the base type of a supertype/subtype relationship, a flag needs to be
    // maintained to
    // indicate whether the supertype object has already been linked to a
    // subtype. This needs
    // to be maintained so that he same supertype is not linked to multiple base
    // types; this can
    // happen in the SQLITE implementation as each subtype to supertype
    // association is held in
    // a seperate relationship container.
    final boolean isBaseTypeObject = subtypeRelationshipDecl.getSupertype() == objectDeclaration;
    if ( isBaseTypeObject )
    {
      final Variable isLinkedVar = implementationClass.createMemberVariable(rdbmsDataMembers,
                                                                            "is" + subtypeRelationshipDecl.getName() + "Linked",
                                                                            new TypeUsage(org.xtuml.masl.cppgen.FundamentalType.BOOL),
                                                                            Visibility.PRIVATE);
      constructor.setInitialValue(isLinkedVar, Literal.FALSE);
      archIdConstructor.setInitialValue(isLinkedVar, Literal.FALSE);

      final Function isLinkedFn = implementationClass.createMemberFunction(rdbmsFunctions,
                                                                           "isLinked" + subtypeRelationshipDecl.getName(),
                                                                           Visibility.PUBLIC);
      isLinkedFn.setReturnType(new TypeUsage(FundamentalType.BOOL));
      isLinkedFn.getCode().appendStatement(new ReturnStatement(isLinkedVar.asExpression()));
      bodyFile.addFunctionDefinition(isLinkedFn);

      final Function setLinkedFn = implementationClass.createMemberFunction(rdbmsFunctions,
                                                                            "setLinked" + subtypeRelationshipDecl.getName(),
                                                                            Visibility.PUBLIC);
      setLinkedFn.getCode().appendExpression(new BinaryExpression(isLinkedVar.asExpression(), BinaryOperator.ASSIGN, Literal.TRUE));
      bodyFile.addFunctionDefinition(setLinkedFn);

      final Function setUnLinkedFn = implementationClass.createMemberFunction(rdbmsFunctions,
                                                                              "setUnLinked" + subtypeRelationshipDecl.getName(),
                                                                              Visibility.PUBLIC);
      setUnLinkedFn.getCode().appendExpression(new BinaryExpression(isLinkedVar.asExpression(),
                                                                    BinaryOperator.ASSIGN,
                                                                    Literal.FALSE));
      bodyFile.addFunctionDefinition(setUnLinkedFn);
    }
  }

  private void defineSuptypeLinkerFns ( final SubtypeRelationshipDeclaration subtypeRelationshipDecl )
  {
    final boolean isBaseTypeObject = subtypeRelationshipDecl.getSupertype() == objectDeclaration;
    final boolean isSubTypeObject = subtypeRelationshipDecl.getSubtypes().contains(objectDeclaration);
    if ( isBaseTypeObject )
    {
      final Function isLinkedFn = new Function("isLinked" + subtypeRelationshipDecl.getName());
      final Variable isLinkedVar = new Variable("is" + subtypeRelationshipDecl.getName() + "Linked");
      for ( final ObjectDeclaration derivedObjDecl : subtypeRelationshipDecl.getSubtypes() )
      {
        final ObjectTranslator relatedDerviedTranslator = objectTranslator.getFrameworkTranslator()
                                                                          .getObjectTranslator(derivedObjDecl);
        final Function linkSubtypeFunction = getMainObjectTranslator().getRelationshipTranslator(subtypeRelationshipDecl.getSuperToSubSpec(derivedObjDecl))
                                                                      .getSubclassOverrides()
                                                                      .getSingleLinkFunction();
        final Function unlinkSubtypeFunction = getMainObjectTranslator().getRelationshipTranslator(subtypeRelationshipDecl.getSuperToSubSpec(derivedObjDecl))
                                                                        .getSubclassOverrides()
                                                                        .getSingleUnlinkFunction();
        final Function baseClassLinkFn = implementationClass.redefineFunction(relationshipLinkers,
                                                                              linkSubtypeFunction,
                                                                              Visibility.PUBLIC);
        final Function baseClassUnlinkFn = implementationClass.redefineFunction(relationshipLinkers,
                                                                                unlinkSubtypeFunction,
                                                                                Visibility.PUBLIC);

        implementSupertypeLinkerFns(baseClassLinkFn, baseClassUnlinkFn, relatedDerviedTranslator);

        final Statement linkErrorThrow = new ThrowStatement(Architecture.programError.callConstructor(Literal.createStringLiteral("Supertype object " + objectDeclaration.getName()
                                                                                                                                  + " already linked using "
                                                                                                                                  + subtypeRelationshipDecl.getName())));
        final IfStatement isLinkedIf = new IfStatement(isLinkedFn.asFunctionCall(), linkErrorThrow);
        baseClassLinkFn.getCode().prependStatement(isLinkedIf);

        final Expression setlinkedExpr = new BinaryExpression(isLinkedVar.asExpression(), BinaryOperator.ASSIGN, Literal.TRUE);
        baseClassLinkFn.getCode().appendExpression(setlinkedExpr);

        final Expression unsetlinkedExpr = new BinaryExpression(isLinkedVar.asExpression(), BinaryOperator.ASSIGN, Literal.FALSE);
        baseClassUnlinkFn.getCode().appendExpression(unsetlinkedExpr);
      }
    }
    else if ( isSubTypeObject )
    {
      final Function linkSupertypeFunction = getMainObjectTranslator().getRelationshipTranslator(subtypeRelationshipDecl.getSubToSuperSpec(objectDeclaration))
                                                                      .getSubclassOverrides()
                                                                      .getSingleLinkFunction();
      final Function unlinkSupertypeFunction = getMainObjectTranslator().getRelationshipTranslator(subtypeRelationshipDecl.getSubToSuperSpec(objectDeclaration))
                                                                        .getSubclassOverrides()
                                                                        .getSingleUnlinkFunction();
      final ObjectTranslator relatedSuperTranslator = objectTranslator.getFrameworkTranslator()
                                                                      .getObjectTranslator(subtypeRelationshipDecl.getSupertype());
      final Function baseClassLinkFn = implementationClass.redefineFunction(relationshipLinkers,
                                                                            linkSupertypeFunction,
                                                                            Visibility.PUBLIC);
      final Function baseClassUnlinkFn = implementationClass.redefineFunction(relationshipLinkers,
                                                                              unlinkSupertypeFunction,
                                                                              Visibility.PUBLIC);
      implementSupertypeLinkerFns(baseClassLinkFn, baseClassUnlinkFn, relatedSuperTranslator);

      // create cpp line:
      // if (rhs.downcast<maslo_A_supsub>().isLinkedR15()) throw
      // ProgramError("Supertype object A_supsub already linked using R15");
      final Function downcastFn = new Function("downcast");
      downcastFn.addTemplateSpecialisation(new TypeUsage(relatedSuperTranslator.getClass(ImplementationClass.KEY_NAME)));
      final Expression downcastFnCall = downcastFn.asFunctionCall(baseClassLinkFn.getParameters().get(0).asExpression(), false);
      final Expression supertypeIsLinkedFnCall = new Function("isLinked" + subtypeRelationshipDecl.getName()).asFunctionCall(downcastFnCall,
                                                                                                                             true);
      final Statement linkErrorThrow = new ThrowStatement(Architecture.programError.callConstructor(Literal.createStringLiteral("Derived type object " + objectDeclaration.getName()
                                                                                                                                + " link "
                                                                                                                                + subtypeRelationshipDecl.getName()
                                                                                                                                + " failed because supertype already linked")));
      final IfStatement isLinkedIf = new IfStatement(supertypeIsLinkedFnCall, linkErrorThrow);
      baseClassLinkFn.getCode().prependStatement(isLinkedIf);

      final Variable derivedrhs = new Variable("derivedrhs");
      final Expression setLinkedfnCall = new Function("setLinked" + subtypeRelationshipDecl.getName()).asFunctionCall(derivedrhs.asExpression(),
                                                                                                                      true);
      baseClassLinkFn.getCode().appendExpression(setLinkedfnCall);

      final Expression setUnLinkedfnCall = new Function("setUnLinked" + subtypeRelationshipDecl.getName()).asFunctionCall(derivedrhs.asExpression(),
                                                                                                                          true);
      baseClassUnlinkFn.getCode().appendExpression(setUnLinkedfnCall);
    }
  }

  private void defineSuperSubTypeNavFns ( final SubtypeRelationshipDeclaration subtypeRelationshipDecl )
  {
    final boolean isBaseTypeObject = subtypeRelationshipDecl.getSupertype() == objectDeclaration;
    final boolean isSubTypeObject = subtypeRelationshipDecl.getSubtypes().contains(objectDeclaration);
    if ( isBaseTypeObject )
    {
      for ( final ObjectDeclaration derivedObjDecl : subtypeRelationshipDecl.getSubtypes() )
      {
        final Function navigateToSuperFunction = getMainObjectTranslator().getRelationshipTranslator(subtypeRelationshipDecl.getSuperToSubSpec(derivedObjDecl))
                                                                          .getSubclassOverrides()
                                                                          .getNavigateFunction();
        final Function countToSuperFunction = getMainObjectTranslator().getRelationshipTranslator(subtypeRelationshipDecl.getSuperToSubSpec(derivedObjDecl))
                                                                       .getSubclassOverrides()
                                                                       .getCountFunction();
        implementSuperSubTypeNavFns(navigateToSuperFunction, countToSuperFunction);
      }
    }
    else if ( isSubTypeObject )
    {
      final Function navigateToBaseFunction = getMainObjectTranslator().getRelationshipTranslator(subtypeRelationshipDecl.getSubToSuperSpec(objectDeclaration))
                                                                       .getSubclassOverrides()
                                                                       .getNavigateFunction();
      final Function countToBaseFunction = getMainObjectTranslator().getRelationshipTranslator(subtypeRelationshipDecl.getSubToSuperSpec(objectDeclaration))
                                                                    .getSubclassOverrides()
                                                                    .getCountFunction();
      implementSuperSubTypeNavFns(navigateToBaseFunction, countToBaseFunction);
    }
  }

  private void implementSupertypeLinkerFns ( final Function baseClassLinkFn,
                                             final Function baseClassUnlinkFn,
                                             final ObjectTranslator relatedObjectTranslator )
  {
    createLinkFnBody(baseClassLinkFn, relatedObjectTranslator);
    createLinkFnBody(baseClassUnlinkFn, relatedObjectTranslator);
    bodyFile.addFunctionDefinition(baseClassLinkFn);
    bodyFile.addFunctionDefinition(baseClassUnlinkFn);
  }

  private void implementSuperSubTypeNavFns ( final Function navigateFunction, final Function countFunction )
  {
    final Function baseClassNavigateFn = implementationClass.redefineFunction(relationshipNavs, navigateFunction, Visibility.PUBLIC);
    createNavigateFnBody(baseClassNavigateFn);
    bodyFile.addFunctionDefinition(baseClassNavigateFn);

    final Function baseClassCountFn = implementationClass.redefineFunction(relationshipCounts, countFunction, Visibility.PUBLIC);
    createCountFnBody(baseClassCountFn);
    bodyFile.addFunctionDefinition(baseClassCountFn);
  }

  private void addCurrentState ()
  {
    if ( objectDeclaration.hasCurrentState() )
    {
      final StateMachineTranslator mainStateMachine = getMainObjectTranslator().getNormalFsm();
      final TypeUsage csType = new TypeUsage(mainStateMachine.getStateEnum());

      final Function getter = implementationClass.redefineFunction(getters,
                                                                   getMainObjectTranslator().getNormalFsm().getGetCurrentState(),
                                                                   Visibility.PUBLIC);
      final Function setter = implementationClass.redefineFunction(setters,
                                                                   getMainObjectTranslator().getNormalFsm().getSetCurrentState(),
                                                                   Visibility.PUBLIC);

      final Variable currentState = implementationClass.createMemberVariable(attributes, "currentState", csType, Visibility.PRIVATE);

      final Variable currentStateParam = constructor.createParameter(csType.getOptimalParameterType(), "currentState");
      constructor.setInitialValue(currentState, currentStateParam.asExpression());
      getter.getCode().appendStatement(new ReturnStatement(currentState.asExpression()));
      getter.declareInClass(true);

      final BinaryExpression currentStateAssignment = new BinaryExpression(currentState.asExpression(),
                                                                           BinaryOperator.ASSIGN,
                                                                           (setter.getParameters().get(0)).asExpression());
      setter.getCode().appendStatement(new ExpressionStatement(currentStateAssignment));
      setter.getCode().appendStatement(new ExpressionStatement(new Function("markAsModified").asFunctionCall()));
      setter.declareInClass(true);
    }
  }

  private void rdbmsSpecifics ()
  {
    dirtyVariable = implementationClass.createMemberVariable(rdbmsDataMembers,
                                                             "dirty",
                                                             new TypeUsage(org.xtuml.masl.cppgen.FundamentalType.BOOL),
                                                             Visibility.PRIVATE);
    constructor.setInitialValue(dirtyVariable, Literal.TRUE);
    archIdConstructor.setInitialValue(dirtyVariable, Literal.TRUE);

    constructFromDbVar = implementationClass.createMemberVariable(rdbmsDataMembers,
                                                                  "constructFromDb",
                                                                  new TypeUsage(org.xtuml.masl.cppgen.FundamentalType.BOOL),
                                                                  Visibility.PRIVATE);
    constructor.setInitialValue(constructFromDbVar, Literal.FALSE);
    archIdConstructor.setInitialValue(constructFromDbVar, Literal.TRUE);

    addArchitectureId();
    addMarkAsClean();
    addMarkAsModified();
  }

  private void addArchitectureId ()
  {
    // Add the architecture Id data member.
    final Variable param = constructor.createParameter(new TypeUsage(org.xtuml.masl.translate.main.Architecture.ID_TYPE).getOptimalParameterType(),
                                                       "architectureId");
    archId = implementationClass.createMemberVariable(attributes,
                                                      "architectureId",
                                                      getMainObjectTranslator().getIdType(),
                                                      Visibility.PRIVATE);
    constructor.setInitialValue(archId, param.asExpression());

    final Variable archIdParam = archIdConstructor.createParameter(new TypeUsage(org.xtuml.masl.translate.main.Architecture.ID_TYPE).getOptimalParameterType(),
                                                                   "architectureId");
    archIdConstructor.setInitialValue(archId, archIdParam.asExpression());

    // Add the architecture Id inline getter
    final Function archIdGetter = implementationClass.redefineFunction(getters,
                                                                       getMainObjectTranslator().getGetId(),
                                                                       Visibility.PUBLIC);
    archIdGetter.getCode().appendStatement(new ReturnStatement(archId.asExpression()));
    archIdGetter.declareInClass(true);
  }

  private void createLinkFnBody ( final Function linkFn, final ObjectTranslator relatedObjTranslator )
  {
    final List<Variable> linkFnParameters = linkFn.getParameters();
    final String paramName = linkFnParameters.get(0).getName();
    final String derviedVarName = "derived" + paramName;

    // Create cpp line:
    // ::SWA::ObjectPtr<maslo_Find_Test_Object_G>
    // derviedRhs(rhs.downcast<maslo_Find_Test_Object_G>());
    final Function downcastFn = new Function("downcast");
    downcastFn.addTemplateSpecialisation(new TypeUsage(relatedObjTranslator.getClass(ImplementationClass.KEY_NAME)));
    final Expression downcastFnCall = downcastFn.asFunctionCall(linkFnParameters.get(0).asExpression(), false);
    final TypeUsage relatedObjType = new TypeUsage(relatedObjTranslator.getClass(ImplementationClass.KEY_NAME));
    final Variable derived = new Variable(new TypeUsage(Architecture.objectPtr(relatedObjType)), derviedVarName, new Expression[]
      { downcastFnCall });
    linkFn.getCode().appendStatement(derived.asStatement());

    // Create cpp line:
    // maslo_Find_Test_Object_HPopulation::getPopulation().link_R1_has_parent_Find_Test_Object_G(::SWA::ObjectPtr<maslo_Find_Test_Object_H>(this),
    // derviedRhs);
    final Expression getPopulationFnCall = objectTranslator.getClass(PopulationClass.KEY_NAME).callStaticFunction("getPopulation");
    final Expression derviedThis = Architecture.objectPtr(new TypeUsage(implementationClass)).callConstructor(new Literal("this"));
    final Function populationLinkFn = new Function(linkFn.getName());
    final Expression populationLinkFnCall = populationLinkFn.asFunctionCall(getPopulationFnCall, false, new Expression[]
      { derviedThis, derived.asExpression() });
    linkFn.getCode().appendExpression(populationLinkFnCall);
  }

  private void createCountFnBody ( final Function countFn )
  {
    // Create cpp line:
    // ::SWA::ObjectPtr< maslo_Find_Test_Object_H>
    // self(const_cast<maslo_Find_Test_Object_H*>(this));
    final Expression constCastFnCall = Std.const_cast(new TypeUsage(implementationClass, TypeUsage.Pointer))
                                          .asFunctionCall(new Literal("this"));
    final TypeUsage relatedObjType = new TypeUsage(implementationClass);
    final Variable self = new Variable(new TypeUsage(Architecture.objectPtr(relatedObjType)), "self", new Expression[]
      { constCastFnCall });
    countFn.getCode().appendStatement(self.asStatement());

    // Create cpp line:
    // return
    // maslo_Find_Test_Object_HPopulation::getPopulation().count_R1_has_parent_Find_Test_Object_G();
    final Expression getPopulationFnCall = objectTranslator.getClass(PopulationClass.KEY_NAME).callStaticFunction("getPopulation");
    final Function populationLinkFn = new Function(countFn.getName());
    final Expression populationCountFnCall = populationLinkFn.asFunctionCall(getPopulationFnCall, false, new Expression[]
      { self.asExpression() });
    countFn.getCode().appendStatement(new ReturnStatement(populationCountFnCall));
  }

  private void createNavigateFnBody ( final Function navigateFn )
  {
    // Forward on the navigate function call to the population class.

    // Create cpp line:
    // ::SWA::ObjectPtr< maslo_Find_Test_Object_H>
    // self(const_cast<maslo_Find_Test_Object_H*>(this));
    final Expression constCastFnCall = Std.const_cast(new TypeUsage(implementationClass, TypeUsage.Pointer))
                                          .asFunctionCall(new Literal("this"));
    final TypeUsage relatedObjType = new TypeUsage(implementationClass);
    final Variable self = new Variable(new TypeUsage(Architecture.objectPtr(relatedObjType)), "self", new Expression[]
      { constCastFnCall });
    navigateFn.getCode().appendStatement(self.asStatement());

    // Create cpp line:
    // return
    // maslo_Find_Test_Object_HPopulation::getPopulation().navigate_R1_has_children_Find_Test_Object_G(self);
    final Expression getPopulationFnCall = objectTranslator.getClass(PopulationClass.KEY_NAME).callStaticFunction("getPopulation");
    final Function populationNavigateFn = new Function(navigateFn.getName());

    final Expression populationNavigateFnCall = populationNavigateFn.asFunctionCall(getPopulationFnCall, false, new Expression[]
      { self.asExpression() });
    navigateFn.getCode().appendStatement(new ReturnStatement(populationNavigateFnCall));
  }

  private void addPrimaryKeyType ()
  {
    final List<TypeUsage> identiferTupleTypes = new ArrayList<TypeUsage>();
    final List<Expression> identiferConstructorList = new ArrayList<Expression>();
    for ( final AttributeDeclaration attributeDecl : objectDeclaration.getAttributes() )
    {
      if ( attributeDecl.isPreferredIdentifier() )
      {
        final TypeUsage attributeType = getMainDomainTranslator().getTypes().getType(attributeDecl.getType());
        identiferTupleTypes.add(attributeType);
        identiferConstructorList.add(attributeMap.get(attributeDecl).asExpression());
      }
    }

    final Class PrimaryKeyTupleClass = Boost.getTupleType(identiferTupleTypes);
    final TypedefType identKeyTypeDef = new TypedefType("PrimaryKeyType", namespace, new TypeUsage(PrimaryKeyTupleClass));
    implementationClass.addTypedef(typedefs, identKeyTypeDef, Visibility.PUBLIC);

    // Add accessor to get the key value for this object.
    final Function primaryKeyGetter = implementationClass.createMemberFunction(getters, "getPrimaryKey", Visibility.PUBLIC);
    primaryKeyGetter.setReturnType(new TypeUsage(identKeyTypeDef, TypeUsage.Const));

    // Create cpp line:
    // return PrimaryKeyType(masla_dropName,masla_dropSource);
    final ReturnStatement identReturnStat = new ReturnStatement(identKeyTypeDef.callConstructor(identiferConstructorList));
    primaryKeyGetter.getCode().appendStatement(identReturnStat);
    bodyFile.addFunctionDefinition(primaryKeyGetter);
  }

  private void addObjectKey ()
  {
    // As well as the identifers defined by the object attributes, additional
    // identifiers
    // can be specified in identifier blocks within the object definition.
    // Therefore loop
    // around these and create additional key specifications.
    int identiferIndex = 0;
    for ( final IdentifierDeclaration identifierSpec : objectDeclaration.getIdentifiers() )
    {
      if ( identifierSpec.getAttributes().size() > 0 )
      {
        addIdentifierKeyType(identifierSpec, ++identiferIndex);
      }
    }
  }

  private void addIdentifierKeyType ( final IdentifierDeclaration identifierSpec, final int identifierIndex )
  {
    final List<TypeUsage> identTupleTypes = new ArrayList<TypeUsage>();
    final List<Expression> identConstructorList = new ArrayList<Expression>();
    // Define the key type for the object.
    for ( final AttributeDeclaration attributeDecl : identifierSpec.getAttributes() )
    {
      final TypeUsage attributeType = getMainDomainTranslator().getTypes().getType(attributeDecl.getType());
      identTupleTypes.add(attributeType);
      identConstructorList.add(attributeMap.get(attributeDecl).asExpression());
    }

    final Class identifierTupleClass = Boost.getTupleType(identTupleTypes);
    final TypedefType identKeyTypeDef = new TypedefType("IndexKeyType_" + identifierIndex,
                                                        namespace,
                                                        new TypeUsage(identifierTupleClass));
    implementationClass.addTypedef(typedefs, identKeyTypeDef, Visibility.PUBLIC);

    // Add accessor to get the key value for this object.
    final Function identKeyGetter = implementationClass.createMemberFunction(getters,
                                                                             "get_index_" + identifierIndex,
                                                                             Visibility.PUBLIC);
    identKeyGetter.setReturnType(new TypeUsage(identKeyTypeDef, TypeUsage.Const));

    // Create cpp line:
    // return PrimaryKeyType(masla_dropName,masla_dropSource);
    final ReturnStatement identReturnStat = new ReturnStatement(identKeyTypeDef.callConstructor(identConstructorList));
    identKeyGetter.getCode().appendStatement(identReturnStat);
    bodyFile.addFunctionDefinition(identKeyGetter);

    identifierKeyGetterFns.put(identifierSpec, identKeyGetter);
    identifierKeyTypes.put(identifierSpec, identKeyTypeDef);
  }

  private void addMarkAsClean ()
  {
    final Function markAsCleanFunc = implementationClass.createMemberFunction(rdbmsFunctions, "markAsClean", Visibility.PUBLIC);
    final CodeBlock markAsCleanCode = markAsCleanFunc.getCode();
    markAsCleanCode.appendStatement(new ExpressionStatement(new BinaryExpression(dirtyVariable.asExpression(),
                                                                                 BinaryOperator.ASSIGN,
                                                                                 Literal.FALSE)));
    markAsCleanCode.appendStatement(new ExpressionStatement(new BinaryExpression(constructFromDbVar.asExpression(),
                                                                                 BinaryOperator.ASSIGN,
                                                                                 Literal.FALSE)));

    bodyFile.addFunctionDefinition(markAsCleanFunc);
  }

  private void addMarkAsModified ()
  {
    final Function markAsModified = implementationClass.createMemberFunction(rdbmsFunctions, "markAsModified", Visibility.PUBLIC);
    bodyFile.addFunctionDefinition(markAsModified);

    // Create cpp lines:
    // if (constructFromDb == false && dirty == false && isDeleted()){
    // dirty = true;
    // maslo_Find_Test_Object_BPopulation::getPopulation().markAsDirty(architectureId);
    // }

    final CodeBlock actionModifiedBlock = new CodeBlock();
    final BinaryExpression constructFromDbCondition = new BinaryExpression(constructFromDbVar.asExpression(),
                                                                           BinaryOperator.EQUAL,
                                                                           Literal.FALSE);
    final BinaryExpression dirtyCondition = new BinaryExpression(dirtyVariable.asExpression(), BinaryOperator.EQUAL, Literal.FALSE);
    final BinaryExpression deletedCondition = new BinaryExpression(new Function("isDeleted").asFunctionCall(),
                                                                   BinaryOperator.EQUAL,
                                                                   Literal.FALSE);
    final BinaryExpression dirtyAndDeleted = new BinaryExpression(dirtyCondition, BinaryOperator.AND, deletedCondition);
    final BinaryExpression modifiedCondition = new BinaryExpression(constructFromDbCondition, BinaryOperator.AND, dirtyAndDeleted);
    final IfStatement ifModifiedStatement = new IfStatement(modifiedCondition, actionModifiedBlock);
    markAsModified.getCode().appendStatement(ifModifiedStatement);

    // Create cpp line:
    // dirty = true;
    actionModifiedBlock.appendExpression(new BinaryExpression(dirtyVariable.asExpression(), BinaryOperator.ASSIGN, Literal.TRUE));

    // Create cpp lines:
    // maslo_Find_Test_Object_BPopulation::getPopulation().markAsDirty(architectureId);
    final Expression getPopulationFnCall = objectTranslator.getClass(PopulationClass.KEY_NAME).callStaticFunction("getPopulation");
    final Expression markAsDirtyFnCall = new Function("markAsDirty").asFunctionCall(getPopulationFnCall,
                                                                                    false,
                                                                                    archId.asExpression());
    actionModifiedBlock.appendExpression(markAsDirtyFnCall);
  }

  private org.xtuml.masl.translate.main.object.ObjectTranslator getMainObjectTranslator ()
  {
    return objectTranslator.getFrameworkTranslator().getMainObjectTranslator(objectDeclaration);
  }

  private org.xtuml.masl.translate.main.DomainTranslator getMainDomainTranslator ()
  {
    return objectTranslator.getFrameworkTranslator().getMainDomainTranslator();
  }

}
