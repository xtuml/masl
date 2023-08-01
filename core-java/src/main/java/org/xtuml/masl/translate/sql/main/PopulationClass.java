/*
 * Filename : PopulationClass.java
 *
 * UK Crown Copyright (c) 2008. All Rights Reserved
 */
package org.xtuml.masl.translate.sql.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.xtuml.masl.cppgen.BinaryExpression;
import org.xtuml.masl.cppgen.BinaryOperator;
import org.xtuml.masl.cppgen.BlankLine;
import org.xtuml.masl.cppgen.Class;
import org.xtuml.masl.cppgen.CodeBlock;
import org.xtuml.masl.cppgen.CodeFile;
import org.xtuml.masl.cppgen.DeclarationGroup;
import org.xtuml.masl.cppgen.Expression;
import org.xtuml.masl.cppgen.ExpressionStatement;
import org.xtuml.masl.cppgen.ForStatement;
import org.xtuml.masl.cppgen.Function;
import org.xtuml.masl.cppgen.FundamentalType;
import org.xtuml.masl.cppgen.IfStatement;
import org.xtuml.masl.cppgen.Literal;
import org.xtuml.masl.cppgen.Namespace;
import org.xtuml.masl.cppgen.NewExpression;
import org.xtuml.masl.cppgen.ReturnStatement;
import org.xtuml.masl.cppgen.Statement;
import org.xtuml.masl.cppgen.StatementGroup;
import org.xtuml.masl.cppgen.Std;
import org.xtuml.masl.cppgen.TypeUsage;
import org.xtuml.masl.cppgen.TypedefType;
import org.xtuml.masl.cppgen.UnaryExpression;
import org.xtuml.masl.cppgen.UnaryOperator;
import org.xtuml.masl.cppgen.Variable;
import org.xtuml.masl.cppgen.VariableDefinitionStatement;
import org.xtuml.masl.cppgen.Visibility;
import org.xtuml.masl.metamodel.expression.FindExpression;
import org.xtuml.masl.metamodel.object.AttributeDeclaration;
import org.xtuml.masl.metamodel.object.IdentifierDeclaration;
import org.xtuml.masl.metamodel.object.ObjectDeclaration;
import org.xtuml.masl.metamodel.relationship.AssociativeRelationshipDeclaration;
import org.xtuml.masl.metamodel.relationship.NormalRelationshipDeclaration;
import org.xtuml.masl.metamodel.relationship.RelationshipSpecification;
import org.xtuml.masl.metamodel.relationship.SubtypeRelationshipDeclaration;
import org.xtuml.masl.metamodel.statemodel.State;
import org.xtuml.masl.translate.main.Architecture;
import org.xtuml.masl.translate.main.Boost;
import org.xtuml.masl.translate.main.Mangler;
import org.xtuml.masl.translate.main.object.Population;
import org.xtuml.masl.translate.main.object.StateMachineTranslator;


public class PopulationClass
    implements GeneratedClass
{

  static public final String                         KEY_NAME                   = "PopulationClass";

  private final String                               className;
  private final Namespace                            namespace;

  private final ObjectTranslator                     objectTranslator;
  private final ObjectDeclaration                    objectDeclaration;

  private CodeBlock                                  deleterCodeBlock;
  private Function                                   creatorFn;
  private Function                                   deleterFn;
  private Function                                   constructor;
  private Function                                   initialiseMethod;
  private final Map<IdentifierDeclaration, Function> identifierFindFns          = new HashMap<IdentifierDeclaration, Function>();
  private final Map<IdentifierDeclaration, Variable> identifierFindAtts         = new LinkedHashMap<IdentifierDeclaration, Variable>();

  private CodeFile                                   bodyFile;
  private CodeFile                                   headerFile;

  private CodeBlock                                  cacheInitLoopBlock;
  private final Map<String, Variable>                relationshipDataMemberList = new HashMap<String, Variable>();

  private Class                                      baseClass;
  private final Class                                populationClass;

  private DeclarationGroup                           findRoutines;
  private DeclarationGroup                           intialisation;
  private DeclarationGroup                           instanceCreation;
  private DeclarationGroup                           singletonRegistration;
  private DeclarationGroup                           constructionDestruction;
  private DeclarationGroup                           populationAttributes;
  private DeclarationGroup                           relationshipCounts;
  private DeclarationGroup                           relationshipLinkers;
  private DeclarationGroup                           relationshipNavs;
  private DeclarationGroup                           findObjectRoutines;
  private DeclarationGroup                           assignerStateRoutines;
  private DeclarationGroup                           identiferFindRoutines;

  private final DatabaseTraits                       databaseTraits;

  public PopulationClass ( final ObjectTranslator parent, final ObjectDeclaration declaration, final Namespace topLevelNamespace )
  {
    objectTranslator = parent;
    objectDeclaration = declaration;
    databaseTraits = objectTranslator.getDatabase().getDatabaseTraits();
    namespace = new Namespace(Mangler.mangleName(objectDeclaration.getDomain()), topLevelNamespace);
    className = Mangler.mangleName(objectDeclaration) + "Population";
    populationClass = new Class(className,
                                namespace,
                                parent.getFrameworkTranslator().getLibrary().createPrivateHeader(databaseTraits.getName() + Mangler.mangleFile(objectDeclaration)
                                                          + "Population"));
  }

  @Override
  public Class getCppClass ()
  {
    return populationClass;
  }

  @Override
  public String getClassName ()
  {
    return KEY_NAME;
  }

  @Override
  public void translateAttributes ()
  {
    initialise();

    // The MASl create unique statement will only compile if the object has been
    // tagged with the unique tag. Under this situation the population class
    // needs to provide accessor methods to get the next unqiue id for the
    // associated unique attribute.
    for ( final AttributeDeclaration attributeDecl : objectDeclaration.getAttributes() )
    {
      if ( attributeDecl.isIdentifier() || !attributeDecl.isReferential() )
      {
        if ( attributeDecl.isUnique() )
        {
          final Function uniqueIdGetter = populationClass.redefineFunction(instanceCreation,
                                                                           getMainObjectTranslator().getPopulation()
                                                                                                    .getGetUniqueId(attributeDecl),
                                                                           Visibility.PUBLIC);
          final TypeUsage type = getMainDomainTranslator().getTypes().getType(attributeDecl.getType());
          final Variable nextUniqueId = populationClass.createMemberVariable(populationAttributes,
                                                                             "nextUniqueId_" + attributeDecl.getName(),
                                                                             type,
                                                                             Visibility.PRIVATE);
          uniqueIdGetter.getCode().appendStatement(new ReturnStatement(nextUniqueId.asExpression()));
          uniqueIdGetter.declareInClass(true);

          final Function uniqueIdUser = populationClass.redefineFunction(instanceCreation,
                                                                         getMainObjectTranslator().getPopulation()
                                                                                                  .getUseUniqueId(attributeDecl),
                                                                         Visibility.PUBLIC);
          uniqueIdUser.declareInClass(true);
          final Expression usedId = uniqueIdUser.getParameters().get(0).asExpression();
          final Expression newId = new BinaryExpression(Std.max(usedId, nextUniqueId.asExpression()),
                                                        BinaryOperator.PLUS,
                                                        Literal.ONE);
          uniqueIdUser.getCode()
                      .appendStatement(new BinaryExpression(nextUniqueId.asExpression(), BinaryOperator.ASSIGN, newId).asStatement());

          // Add code to the population initialise method to determine
          // the value of the nextUniqueId variable after a warm start.
          final Function getMaxUniqueIdFn = new Function("get_max_" + attributeDecl.getName());
          final Expression getMaxUniqueIdFnCall = getMaxUniqueIdFn.asFunctionCall(Database.mapperInstance.asExpression(), true);
          final Expression uniqueIdValue = new BinaryExpression(getMaxUniqueIdFnCall, BinaryOperator.PLUS, new Literal(1));
          final Expression uniqueIdAssignment = new BinaryExpression(nextUniqueId.asExpression(),
                                                                     BinaryOperator.ASSIGN,
                                                                     uniqueIdValue);
          initialiseMethod.getCode().appendExpression(uniqueIdAssignment);
        }
      }
    }
    // Add the functionality required to support assigner state models.
    addCurrentState();
  }

  @Override
  public void translateEvents ()
  {
  }

  @Override
  public void translateRelationships ()
  {
    // Loop around and create the relationship data members for all the normal
    // relationships
    // used by the current object under translation.
    for ( final NormalRelationshipDeclaration normalRelationshipDecl : objectTranslator.getNormalRelationships() )
    {
      defineNormalLinkerFns(normalRelationshipDecl);
    }

    for ( final AssociativeRelationshipDeclaration assocRelationshipDecl : objectTranslator.getAssociativeRelationships() )
    {
      defineAssociativeLinkerFns(assocRelationshipDecl);
      defineAssociativeNavFns(assocRelationshipDecl);
    }

    final StatementGroup forStatements = new StatementGroup();
    for ( final SubtypeRelationshipDeclaration subtypeRelationshipDecl : objectTranslator.getSubTypeRelationships() )
    {
      defineSubtypeLinkerFns(subtypeRelationshipDecl);
      defineSubtypeNavFns(subtypeRelationshipDecl);
      defineSubtypeRelationshipInit(subtypeRelationshipDecl, forStatements);
    }

    if ( forStatements.size() > 0 )
    {
      // This object is a supertype in one or more generalisations, so
      // add required init code to handle warm start of the process.

      // Create cpp lines:
      // ::SWA::Set< ObjectPtr > superTypeSet = findAll();
      // for(::SWA::Set< ObjectPtr >::iterator superItr = superTypeSet.begin();
      // superItr != superTypeSet.end(); ++superItr)
      // ::SWA::ObjectPtr<maslo_TEST_A> dervied
      // ((*superItr).downcast<maslo_TEST_A>());

      final Class objPtrType = Architecture.objectPtr(new TypeUsage(getMainObjectTranslator().getMainClass()));
      final Class objSetClass = Architecture.set(new TypeUsage(objPtrType));
      final Variable superTypeSetVar = new Variable(new TypeUsage(objSetClass),
                                                    "superTypeSet",
                                                    new Function("findAll").asFunctionCall());
      initialiseMethod.getCode().appendStatement(superTypeSetVar.asStatement());

      final Class setIterator = objSetClass.referenceNestedType("iterator");
      final Variable setItrVar = new Variable(new TypeUsage(setIterator),
                                              "superItr",
                                              new Function("begin").asFunctionCall(superTypeSetVar.asExpression(), false));
      final org.xtuml.masl.cppgen.Expression forTest = new org.xtuml.masl.cppgen.BinaryExpression(setItrVar.asExpression(),
                                                                                                org.xtuml.masl.cppgen.BinaryOperator.NOT_EQUAL,
                                                                                                new Function("end").asFunctionCall(superTypeSetVar.asExpression(),
                                                                                                                                   false));
      final org.xtuml.masl.cppgen.Expression incrementExpr = new org.xtuml.masl.cppgen.UnaryExpression(org.xtuml.masl.cppgen.UnaryOperator.PREINCREMENT,
                                                                                                     setItrVar.asExpression());

      // Create cpp lines:
      // ::SWA::ObjectPtr<maslo_TEST_A> dervied
      // ((*superItr).downcast<maslo_TEST_A>());
      final Function downcastFn = new Function("downcast");
      downcastFn.addTemplateSpecialisation(new TypeUsage(objectTranslator.getClass(ImplementationClass.KEY_NAME)));
      final Expression ptrDeRef = new UnaryExpression(UnaryOperator.DEREFERENCE, setItrVar.asExpression());
      final Expression downcastFnCall = downcastFn.asFunctionCall(ptrDeRef, false);
      final Variable derivedvar = new Variable(new TypeUsage(Database.psObjectPtrClass), "derived", downcastFnCall);
      forStatements.prependStatement(derivedvar.asStatement());

      final ForStatement setItrFor = new ForStatement(new VariableDefinitionStatement(setItrVar),
                                                      forTest,
                                                      incrementExpr,
                                                      forStatements);
      initialiseMethod.getCode().appendStatement(setItrFor);
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
  public void translateFind ()
  {
    for ( final Population.FindFunction func : getMainObjectTranslator().getPopulation().getFindFunctions() )
    {
      // Null predicate finds are implemented in the Population superclass
      if ( func.predicate != null )
      {
        final Function findFn = populationClass.redefineFunction(findRoutines, func.function, Visibility.PUBLIC);
        findFn.setComment("MASL find: " + func.predicate.toString());
        bodyFile.addFunctionDefinition(findFn);

        final List<Expression> params = new ArrayList<Expression>();
        for ( final Variable param : findFn.getParameters() )
        {
          params.add(param.asExpression());
        }

        final IdentifierDeclaration identifier = getMainObjectTranslator().getFindIdentifier(func.predicate);
        if ( identifier == null )
        {
          switch ( func.type )
          {
            case FIND_ONE:
            case FIND_ONLY:
              // Create cpp similar to:
              // return mapper->findOne_OPmasl_attribute_1_maslEQp1CP( p1 );
              findFn.getCode().appendStatement(new ReturnStatement(findFn.asFunctionCall(Database.mapperInstance.asExpression(),
                                                                                         true,
                                                                                         params)));
              break;

            default:
              // Create cpp similar to:
              // ::SWA::Set< ::SWA::ObjectPtr< maslo_Find_Test_Object_A> >
              // instanceSet =
              // mapper->find_OPOPOPmasl_attribute_1_maslEQp1CPOROPmasl_attribute_1_maslEQp2CPCPOROPmasl_attribute_1_maslEQp3CPCP(p1,p2,p3);
              final Class instanceSetType = Architecture.set(new TypeUsage(Architecture.objectPtr(new TypeUsage(getMainObjectTranslator().getMainClass()))));
              final TypeUsage instanceSetTypeUsage = new TypeUsage(instanceSetType);
              final Expression initialValue = findFn.asFunctionCall(Database.mapperInstance.asExpression(), true, params);
              final Variable instanceSet = new Variable(instanceSetTypeUsage, "instanceSet", new Expression[]
                { initialValue });
              findFn.getCode().appendStatement(instanceSet.asStatement());

              // Create cpp similar to:
              // return instanceSet
              findFn.getCode().appendStatement(new ReturnStatement(instanceSet.asExpression()));
              break;
          }
        }
        else
        {
          // The find predicate is only using the object identifiers. Therefore
          // can undertake a fast find using a map of cached objects keyed
          // against
          // the object identifiers.
          createIdentifierFind(identifier, func, findFn);
        }
      }
    }

    // The implementation of the create and delete methods can only be done
    // after the processing of the find functions as this determines if a find
    // on the primary key(s) is being undertaken and if so then a cache of the
    // objects is stored in the population class.
    addCreateMethodBody();
    addDeleteMethodBody();
  }

  private void createIdentifierFind ( final IdentifierDeclaration identifier,
                                        final Population.FindFunction findFunction,
                                        final Function functionImpl )
  {
    final Function identifierLookup = addIdentifierLookup(identifier);

    final Iterator<Variable> param = findFunction.function.getParameters().iterator();

    final Map<AttributeDeclaration, Expression> paramLookup = new HashMap<AttributeDeclaration, Expression>();
    for ( final AttributeDeclaration att : findFunction.predicate.getFindEqualAttributes() )
    {
      paramLookup.put(att, param.next().asExpression());
    }

    final List<Expression> orderedParams = new ArrayList<Expression>();

    for ( final AttributeDeclaration identAttribute : identifier.getAttributes() )
    {
      orderedParams.add(paramLookup.get(identAttribute));
    }

    final Expression fullCachingCondition = new Function("fullCachingEnabled").asFunctionCall(Database.mapperInstance.asExpression(),
                                                                                              true);
    final CodeBlock cachingTrueStatements = new CodeBlock();
    Expression identifierResult = identifierLookup.asFunctionCall(orderedParams);
    if ( findFunction.type == FindExpression.Type.FIND )
    {
      identifierResult = findFunction.function.getReturnType().getType().callConstructor(identifierResult);
    }
    cachingTrueStatements.appendStatement(new ReturnStatement(identifierResult));
    final CodeBlock cachingFalseStatements = new CodeBlock();
    final IfStatement cachingIfBlock = new IfStatement(fullCachingCondition, cachingTrueStatements, cachingFalseStatements);

    // cachingFalseStatements
    final List<Expression> mapperFnCallParamList = new ArrayList<Expression>();
    for ( final Variable functionImplVar : functionImpl.getParameters() )
    {
      mapperFnCallParamList.add(functionImplVar.asExpression());
    }

    final Expression mapperFindFnCall = new Function(functionImpl.getName()).asFunctionCall(Database.mapperInstance.asExpression(),
                                                                                            true,
                                                                                            mapperFnCallParamList);
    cachingFalseStatements.appendStatement(new ReturnStatement(mapperFindFnCall));

    functionImpl.getCode().appendStatement(cachingIfBlock);
  }

  private Function addIdentifierLookup ( final IdentifierDeclaration identifier )
  {
    // If the identifer find function has already been created by
    // another type of find then just re-use the implementation.
    Function identifierFindFn = identifierFindFns.get(identifier);
    if ( identifierFindFn == null )
    {

      // The implementation of the identifier find look up consists of a
      // hash_map
      // of all the created objects keyed on a hash of the unique attributes and
      // a private find function that undertakes the lookup.

      String name = "";
      for ( final AttributeDeclaration attribute : identifier.getAttributes() )
      {
        name = name + Mangler.mangleName(attribute);
      }
      final String lookupName = name;

      // Define the private identifier find function.
      identifierFindFn = populationClass.createMemberFunction(identiferFindRoutines, "find_" + lookupName, Visibility.PRIVATE);
      identifierFindFn.setConst(true);
      identifierFindFn.setReturnType(new TypeUsage(Architecture.objectPtr(new TypeUsage(getMainObjectTranslator().getMainClass()))));
      bodyFile.addFunctionDefinition(identifierFindFn);
      identifierFindFns.put(identifier, identifierFindFn);

      final List<Expression> params = new ArrayList<Expression>();
      for ( final AttributeDeclaration attDec : identifier.getAttributes() )
      {
        final Variable param = identifierFindFn.createParameter(getMainDomainTranslator().getTypes()
                                                                                         .getType(attDec.getType())
                                                                                         .getOptimalParameterType(),
                                                                Mangler.mangleName(attDec));
        params.add(param.asExpression());
      }

      // Define the hash_map used to store the keyed objects
      final TypedefType objectKeyType = objectTranslator.getKeyType(identifier);
      final Function objectKeyGetter = objectTranslator.getKeyGetterFn(identifier);
      final Class objectPtr = Architecture.objectPtr(new TypeUsage(getMainObjectTranslator().getMainClass()));
      final Class lookupContainerType = Boost.unordered_map(new TypeUsage(objectKeyType), new TypeUsage(objectPtr));
      final Variable identifierFindAtt = populationClass.createMemberVariable(populationAttributes,
                                                                              lookupName + "_cache",
                                                                              new TypeUsage(lookupContainerType),
                                                                              Visibility.PRIVATE);
      identifierFindAtts.put(identifier, identifierFindAtt);

      // Implement the find function
      final Expression find = new Function("find").asFunctionCall(identifierFindAtt.asExpression(),
                                                                  false,
                                                                  objectKeyType.callConstructor(params));
      final Variable found = new Variable(new TypeUsage(lookupContainerType.referenceNestedType("const_iterator")), "result", find);
      final Statement failReturn = new ReturnStatement(objectPtr.callConstructor());
      final Statement foundReturn = new ReturnStatement(new Variable("second").asMemberReference(found.asExpression(), true));
      final Statement check = new IfStatement(new BinaryExpression(found.asExpression(),
                                                                   BinaryOperator.EQUAL,
                                                                   new Function("end").asFunctionCall(identifierFindAtt.asExpression(),
                                                                                                      false)),
                                              failReturn,
                                              foundReturn);
      identifierFindFn.getCode().appendStatement(found.asStatement());
      identifierFindFn.getCode().appendStatement(check);

      // Modifiy the initialise method so that the hash_map is initialised.
      // Some objects have multiple identifiers. Therefore make sure that
      // the findAll is only called once.
      final Class objPtrType = Architecture.objectPtr(new TypeUsage(getMainObjectTranslator().getMainClass()));
      final Class objSetClass = Architecture.set(new TypeUsage(objPtrType));
      final Variable allObjsVar = new Variable(new TypeUsage(objSetClass), "allObjs");
      if ( identifierFindFns.size() == 1 )
      {

        final Expression fullCachingCondition = new Function("fullCachingEnabled").asFunctionCall(Database.mapperInstance.asExpression(),
                                                                                                  true);
        final CodeBlock fullCachingBlock = new CodeBlock();
        final IfStatement cachingIfBlock = new IfStatement(fullCachingCondition, fullCachingBlock);
        initialiseMethod.getCode().appendStatement(cachingIfBlock);

        // Create cpp line:
        // ::SWA::Set< SWA::ObjectPtr< ::masld_SE::maslo_SelectorDropLocation> >
        // allObjs;
        fullCachingBlock.appendStatement(allObjsVar.asStatement());

        // Create cpp line:
        // mapper->findAll(allObjs);
        final List<Expression> findArgs = new ArrayList<Expression>();
        findArgs.add(allObjsVar.asExpression());
        final Expression findAllFnCall = new Function("findAll").asFunctionCall(Database.mapperInstance.asExpression(),
                                                                                true,
                                                                                findArgs);
        fullCachingBlock.appendExpression(findAllFnCall);

        // Create cpp line:
        // ::SWA::Set< SWA::ObjectPtr< ::masld_SE::maslo_SelectorDropLocation>
        // >::iterator objEnd = allObjs.end();
        // for(::SWA::Set< SWA::ObjectPtr<
        // ::masld_SE::maslo_SelectorDropLocation> >::iterator objItr =
        // allObjs.begin();
        // objItr != objEnd; ++objItr){
        // SWA::ObjectPtr<maslo_SelectorDropLocation>
        // implObj((*objItr).downcast<maslo_SelectorDropLocation>());
        // lookupCache.insert(MapLookupType::value_type(objItr->get_object_key(),*objItr));
        // }
        final Class iterType = objSetClass.referenceNestedType("const_iterator");
        final Variable objItrVar = new Variable(new TypeUsage(iterType),
                                                "objItr",
                                                new Function("begin").asFunctionCall(allObjsVar.asExpression(), false));
        final Variable objEndVar = new Variable(new TypeUsage(iterType),
                                                "objEnd",
                                                new Function("end").asFunctionCall(allObjsVar.asExpression(), false));

        final Expression ValueExpr = new UnaryExpression(UnaryOperator.DEREFERENCE, objItrVar.asExpression());
        final Class impObjPtrType = Architecture.objectPtr(new TypeUsage(objectTranslator.getClass(ImplementationClass.KEY_NAME)));

        final Function downcastFn = new Function("downcast");
        downcastFn.addTemplateSpecialisation(new TypeUsage(objectTranslator.getClass(ImplementationClass.KEY_NAME)));
        final Expression downcastFnCall = downcastFn.asFunctionCall(ValueExpr, false);

        final Variable implValueDef = new Variable(new TypeUsage(impObjPtrType), "implObj", new Expression[]
          { downcastFnCall });
        final Expression keyExpr = objectKeyGetter.asFunctionCall(implValueDef.asExpression(), true);
        final Expression valueTypeExpr = lookupContainerType.referenceNestedType("value_type").callConstructor(new Expression[]
          { keyExpr, ValueExpr });
        final Expression insertFnCall = new Function("insert").asFunctionCall(identifierFindAtt.asExpression(),
                                                                              false,
                                                                              valueTypeExpr);

        final BinaryExpression forTestCondition = new BinaryExpression(objItrVar.asExpression(),
                                                                       BinaryOperator.NOT_EQUAL,
                                                                       objEndVar.asExpression());
        final UnaryExpression itrIncrement = new UnaryExpression(UnaryOperator.PREINCREMENT, objItrVar.asExpression());

        cacheInitLoopBlock = new CodeBlock();
        cacheInitLoopBlock.appendStatement(implValueDef.asStatement());
        cacheInitLoopBlock.appendExpression(insertFnCall);

        final ForStatement forLoop = new ForStatement(objItrVar.asStatement(), forTestCondition, itrIncrement, cacheInitLoopBlock);
        fullCachingBlock.appendStatement(objEndVar.asStatement());
        fullCachingBlock.appendStatement(forLoop);
      }
      else
      {
        final Variable objItrVar = new Variable("objItr");
        final Variable implObj = new Variable("implObj");
        final Expression ValueExpr = new UnaryExpression(UnaryOperator.DEREFERENCE, objItrVar.asExpression());
        final Expression keyExpr = objectKeyGetter.asFunctionCall(implObj.asExpression(), true);
        final Expression valueTypeExpr = lookupContainerType.referenceNestedType("value_type").callConstructor(new Expression[]
          { keyExpr, ValueExpr });
        final Expression insertFnCall = new Function("insert").asFunctionCall(identifierFindAtt.asExpression(),
                                                                              false,
                                                                              valueTypeExpr);
        cacheInitLoopBlock.appendExpression(insertFnCall);
      }
    }
    return identifierFindFn;
  }

  private void initialise ()
  {
    baseClass = objectTranslator.getDatabase().getPopulationClass();
    baseClass.addTemplateSpecialisation(new TypeUsage(getMainObjectTranslator().getMainClass()));
    baseClass.addTemplateSpecialisation(new TypeUsage(objectTranslator.getClass(ImplementationClass.KEY_NAME)));
    baseClass.addTemplateSpecialisation(new TypeUsage(objectTranslator.getClass(MapperClass.KEY_NAME)));
    baseClass.addTemplateSpecialisation(new TypeUsage(getMainObjectTranslator().getPopulationClass()));
    populationClass.addSuperclass(baseClass, Visibility.PUBLIC);

    bodyFile = objectTranslator.getFrameworkTranslator().getLibrary().createBodyFile(databaseTraits.getName() + Mangler.mangleFile(objectDeclaration) + "Population");
    headerFile = objectTranslator.getFrameworkTranslator().getLibrary().createPrivateHeader(databaseTraits.getName() + Mangler.mangleFile(objectDeclaration) + "Population");
    headerFile.addClassDeclaration(populationClass);


    constructionDestruction = populationClass.createDeclarationGroup("Constructors and Destructors");
    intialisation = populationClass.createDeclarationGroup("Initialisation");
    instanceCreation = populationClass.createDeclarationGroup("Instance Creation");

    findRoutines = populationClass.createDeclarationGroup("Find Routines");
    findObjectRoutines = populationClass.createDeclarationGroup("Find object Routines");
    identiferFindRoutines = populationClass.createDeclarationGroup("Identifier Find Routines");

    relationshipCounts = populationClass.createDeclarationGroup("Relationship Counts");
    relationshipLinkers = populationClass.createDeclarationGroup("Relationship Links");
    relationshipNavs = populationClass.createDeclarationGroup("Relationship Navigations");

    singletonRegistration = populationClass.createDeclarationGroup("Singleton Registration");
    populationAttributes = populationClass.createDeclarationGroup("Attributes");
    assignerStateRoutines = populationClass.createDeclarationGroup("Assigner methods");

    constructor = addConstructor();
    addDestructor();
    initialiseMethod = addInitialiseMethod();
    final Function getPopulationMethod = addGetPopulationMethod();
    addSingletonRegistration(getPopulationMethod);
    registerInitialisationFunction();
    addFindObjectMethods();
    addCreateMethod();
    addDeleteMethod();
  }

  private void defineNormalLinkerFns ( final NormalRelationshipDeclaration normalRelationshipDecl )
  {
    // Each relationship has an associated relationship mapper class and
    // relationship mapper sql class. Need to get
    // the class representations for these so they can be used to form and
    // initialise the actual relationship data
    // member to be used by this population class.
    final Class relationshipMapperType = objectTranslator.getFrameworkTranslator()
                                                         .getRelationshipTranslator(normalRelationshipDecl)
                                                         .getRelationshipMapperClass();
    final Class relationshipMapperSqlType = objectTranslator.getFrameworkTranslator()
                                                            .getRelationshipTranslator(normalRelationshipDecl)
                                                            .getRelationshipMapperSqlClass();

    // Get the relationship delete function for this population class, this is
    // used to check that the instance to
    // be deleted does not have any links, if it was deleted while still having
    // links then a dangling relationship
    // error would be reported.
    final Function objectDeletedFn = getDeleteObjectFunction(normalRelationshipDecl.getLeftObject(),
                                                             normalRelationshipDecl.getRightObject(),
                                                             null);

    final Variable relationshipVar = addRelationshipDataMember(relationshipMapperType,
                                                               relationshipMapperSqlType,
                                                               normalRelationshipDecl.getName(),
                                                               objectDeletedFn);
    final RelationshipSpecification leftToRightRelSpec = normalRelationshipDecl.getLeftToRightSpec();
    final RelationshipSpecification rightToLeftRelSpec = normalRelationshipDecl.getRightToLeftSpec();

    final ObjectDeclaration relLhsObject = normalRelationshipDecl.getLeftObject();
    final ObjectDeclaration relRhsObject = normalRelationshipDecl.getRightObject();

    // Undertake any leftToRight relationship specs that
    // will need linker function implementations
    org.xtuml.masl.translate.main.object.RelationshipTranslator relTrans = getMainObjectTranslator().getRelationshipTranslator(leftToRightRelSpec);
    if ( relTrans != null )
    {
      final Function linkFunction = relTrans.getSubclassOverrides().getSingleLinkFunction();
      final Function unlinkFunction = relTrans.getSubclassOverrides().getSingleUnlinkFunction();
      final Function navigateFunction = relTrans.getSubclassOverrides().getNavigateFunction();
      final Function countFunction = relTrans.getSubclassOverrides().getCountFunction();
      implementNormalLinkerFns(normalRelationshipDecl,
                                     relationshipVar,
                                     RelationshipDirection.LeftToRight,
                                     linkFunction,
                                     unlinkFunction,
                                     navigateFunction,
                                     countFunction,
                                     relLhsObject,
                                     relRhsObject);
    }

    // Undertake any RightToLeft relationship specs that
    // will need linker function implementations
    relTrans = getMainObjectTranslator().getRelationshipTranslator(rightToLeftRelSpec);
    if ( relTrans != null )
    {
      final Function linkFunction = relTrans.getSubclassOverrides().getSingleLinkFunction();
      final Function unlinkFunction = relTrans.getSubclassOverrides().getSingleUnlinkFunction();
      final Function navigateFunction = relTrans.getSubclassOverrides().getNavigateFunction();
      final Function countFunction = relTrans.getSubclassOverrides().getCountFunction();
      implementNormalLinkerFns(normalRelationshipDecl,
                                     relationshipVar,
                                     RelationshipDirection.RightToLeft,
                                     linkFunction,
                                     unlinkFunction,
                                     navigateFunction,
                                     countFunction,
                                     relLhsObject,
                                     relRhsObject);
    }
  }

  private void defineAssociativeLinkerFns ( final AssociativeRelationshipDeclaration assocRelationshipDecl )
  {
    final ObjectDeclaration lhsObjectDecl = assocRelationshipDecl.getLeftObject();
    final ObjectDeclaration rhsObjectDecl = assocRelationshipDecl.getRightObject();
    final ObjectDeclaration assObjectDecl = assocRelationshipDecl.getAssocObject();

    // An object taking part in an associative relationship will contain linker
    // functions
    // for each of the other participating objects. To prevent the relationship
    // mapper class
    // from being included several times in the population class, undertake a
    // check to see if the
    // relationship mapper has already been defined.
    Variable relationshipVar = relationshipDataMemberList.get(assocRelationshipDecl.getName());
    if ( relationshipVar == null )
    {
      // Each relationship has an associated relationship mapper class and
      // relationship mapper sql class. Need to get
      // the class representations for these so they can be used to form and
      // initialise the actual relationship data
      // member to be used by this population class.
      final Class relationshipMapperType = objectTranslator.getFrameworkTranslator()
                                                           .getRelationshipTranslator(assocRelationshipDecl)
                                                           .getRelationshipMapperClass();
      final Class relationshipMapperSqlType = objectTranslator.getFrameworkTranslator()
                                                              .getRelationshipTranslator(assocRelationshipDecl)
                                                              .getRelationshipMapperSqlClass();

      // Get the relationship delete function for this population class, this is
      // used to check that the instance to
      // be deleted does not have any links, if it was deleted while still
      // having links then a dangling relationship
      // error would be reported.
      final Function objectDeletedFn = getDeleteObjectFunction(lhsObjectDecl, rhsObjectDecl, assObjectDecl);
      relationshipVar = addRelationshipDataMember(relationshipMapperType,
                                                  relationshipMapperSqlType,
                                                  assocRelationshipDecl.getName(),
                                                  objectDeletedFn);
    }

    if ( objectDeclaration == lhsObjectDecl )
    {
      final Function linkRhsFunction = getMainObjectTranslator().getRelationshipTranslator(assocRelationshipDecl.getLeftToRightSpec())
                                                                .getSubclassOverrides()
                                                                .getSingleLinkFunction();
      final Function unlinkRhsFunction = getMainObjectTranslator().getRelationshipTranslator(assocRelationshipDecl.getLeftToRightSpec())
                                                                  .getSubclassOverrides()
                                                                  .getSingleUnlinkFunction();
      implementAssociativeLinkerFns(assocRelationshipDecl,
                                    relationshipVar,
                                    linkRhsFunction,
                                    unlinkRhsFunction,
                                    RelationshipDirection.LeftToRight);
    }

    if ( objectDeclaration == rhsObjectDecl )
    {
      final Function linkLhsFunction = getMainObjectTranslator().getRelationshipTranslator(assocRelationshipDecl.getRightToLeftSpec())
                                                                .getSubclassOverrides()
                                                                .getSingleLinkFunction();
      final Function unlinkLhsFunction = getMainObjectTranslator().getRelationshipTranslator(assocRelationshipDecl.getRightToLeftSpec())
                                                                  .getSubclassOverrides()
                                                                  .getSingleUnlinkFunction();
      implementAssociativeLinkerFns(assocRelationshipDecl,
                                    relationshipVar,
                                    linkLhsFunction,
                                    unlinkLhsFunction,
                                    RelationshipDirection.RightToLeft);
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

    // An object taking part in an associative relationship will contain
    // navigation functions
    // for each of the other participating objects. To prevent the relationship
    // mapper class
    // from being included several times inthe population class, undertake a
    // check to see if the
    // relationship mapper has already been defined.
    Variable relationshipVar = relationshipDataMemberList.get(assocRelationshipDecl.getName());
    if ( relationshipVar == null )
    {
      // Each relationship has an associated relationship mapper class and
      // relationship mapper sql class. Need to get
      // the class representations for these so they can be used to form and
      // initialise the actual relationship data
      // member to be used by this population class.
      final Class relationshipMapperType = objectTranslator.getFrameworkTranslator()
                                                           .getRelationshipTranslator(assocRelationshipDecl)
                                                           .getRelationshipMapperClass();
      final Class relationshipMapperSqlType = objectTranslator.getFrameworkTranslator()
                                                              .getRelationshipTranslator(assocRelationshipDecl)
                                                              .getRelationshipMapperSqlClass();

      // Get the relationship delete function for this population class, this is
      // used to check that the instance to
      // be deleted does not have any links, if it was deleted while still
      // having links then a dangling relationship
      // error would be reported.
      final Function objectDeletedFn = getDeleteObjectFunction(lhsObjectDecl, rhsObjectDecl, assObjectDecl);
      relationshipVar = addRelationshipDataMember(relationshipMapperType,
                                                  relationshipMapperSqlType,
                                                  assocRelationshipDecl.getName(),
                                                  objectDeletedFn);
    }

    if ( objectDeclaration == lhsObjectDecl )
    {
      final Function navigateLhsToRhsFunction = getMainObjectTranslator().getRelationshipTranslator(assocRelationshipDecl.getLeftToRightSpec())
                                                                         .getSubclassOverrides()
                                                                         .getNavigateFunction();
      final Function countLhsToRhsFunction = getMainObjectTranslator().getRelationshipTranslator(assocRelationshipDecl.getLeftToRightSpec())
                                                                      .getSubclassOverrides()
                                                                      .getCountFunction();
      implementAssociativeNavFns(lhsObjectDecl,
                                 rhsObjectDecl,
                                 relationshipVar,
                                 navigateLhsToRhsFunction,
                                 countLhsToRhsFunction,
                                 RelationshipDirection.LeftToRight);

      final Function navigateLhsToAssFunction = getMainObjectTranslator().getRelationshipTranslator(assocRelationshipDecl.getLeftToAssocSpec())
                                                                         .getSubclassOverrides()
                                                                         .getNavigateFunction();
      final Function countLhsToAssFunction = getMainObjectTranslator().getRelationshipTranslator(assocRelationshipDecl.getLeftToAssocSpec())
                                                                      .getSubclassOverrides()
                                                                      .getCountFunction();
      final Function correlateLhsToAssFunction = getMainObjectTranslator().getRelationshipTranslator(assocRelationshipDecl.getLeftToRightSpec())
                                                                          .getSubclassOverrides()
                                                                          .getSingleCorrelateFunction();
      implementAssociativeNavFns(lhsObjectDecl,
                                 assObjectDecl,
                                 relationshipVar,
                                 navigateLhsToAssFunction,
                                 countLhsToAssFunction,
                                 RelationshipDirection.LeftToAssoc);
      implementAssociativeCorrelateFn(lhsObjectDecl,
                                      rhsObjectDecl,
                                      assObjectDecl,
                                      correlateLhsToAssFunction,
                                      relationshipVar,
                                      RelationshipDirection.LeftToRight);
    }

    if ( objectDeclaration == rhsObjectDecl )
    {
      final Function navigateRhsToLhsFunction = getMainObjectTranslator().getRelationshipTranslator(assocRelationshipDecl.getRightToLeftSpec())
                                                                         .getSubclassOverrides()
                                                                         .getNavigateFunction();
      final Function countRhsToLhsFunction = getMainObjectTranslator().getRelationshipTranslator(assocRelationshipDecl.getRightToLeftSpec())
                                                                      .getSubclassOverrides()
                                                                      .getCountFunction();
      implementAssociativeNavFns(rhsObjectDecl,
                                 lhsObjectDecl,
                                 relationshipVar,
                                 navigateRhsToLhsFunction,
                                 countRhsToLhsFunction,
                                 RelationshipDirection.RightToLeft);

      final Function navigateLhsToAssFunction = getMainObjectTranslator().getRelationshipTranslator(assocRelationshipDecl.getRightToAssocSpec())
                                                                         .getSubclassOverrides()
                                                                         .getNavigateFunction();
      final Function countLhsToAssFunction = getMainObjectTranslator().getRelationshipTranslator(assocRelationshipDecl.getRightToAssocSpec())
                                                                      .getSubclassOverrides()
                                                                      .getCountFunction();
      final Function correlateRhsToAssFunction = getMainObjectTranslator().getRelationshipTranslator(assocRelationshipDecl.getRightToLeftSpec())
                                                                          .getSubclassOverrides()
                                                                          .getSingleCorrelateFunction();
      implementAssociativeNavFns(rhsObjectDecl,
                                 assObjectDecl,
                                 relationshipVar,
                                 navigateLhsToAssFunction,
                                 countLhsToAssFunction,
                                 RelationshipDirection.RightToAssoc);
      implementAssociativeCorrelateFn(rhsObjectDecl,
                                      lhsObjectDecl,
                                      assObjectDecl,
                                      correlateRhsToAssFunction,
                                      relationshipVar,
                                      RelationshipDirection.RightToLeft);
    }

    if ( objectDeclaration == assObjectDecl )
    {
      // Associative objects are only linked through their parent objects
      // so will not have link/unlink methods to implements for associative
      // objects.
      final Function navigateAssocToLeftFunction = getMainObjectTranslator().getRelationshipTranslator(assocRelationshipDecl.getAssocToLeftSpec())
                                                                            .getSubclassOverrides()
                                                                            .getNavigateFunction();
      final Function countAssocToLeftFunction = getMainObjectTranslator().getRelationshipTranslator(assocRelationshipDecl.getAssocToLeftSpec())
                                                                         .getSubclassOverrides()
                                                                         .getCountFunction();

      final Function navigateAssocToRightFunction = getMainObjectTranslator().getRelationshipTranslator(assocRelationshipDecl.getAssocToRightSpec())
                                                                             .getSubclassOverrides()
                                                                             .getNavigateFunction();
      final Function countAssocToRightFunction = getMainObjectTranslator().getRelationshipTranslator(assocRelationshipDecl.getAssocToRightSpec())
                                                                          .getSubclassOverrides()
                                                                          .getCountFunction();

      implementAssociativeNavFns(assObjectDecl,
                                 lhsObjectDecl,
                                 relationshipVar,
                                 navigateAssocToLeftFunction,
                                 countAssocToLeftFunction,
                                 RelationshipDirection.AssocToLeft);
      implementAssociativeNavFns(assObjectDecl,
                                 rhsObjectDecl,
                                 relationshipVar,
                                 navigateAssocToRightFunction,
                                 countAssocToRightFunction,
                                 RelationshipDirection.AssocToRight);
    }
  }

  private String getSubtypeRelationshipName ( final SubtypeRelationshipDeclaration subtypeRelationshipDecl,
                                              final ObjectDeclaration derivedObjDecl )
  {
    return subtypeRelationshipDecl.getName() + "_"
           + subtypeRelationshipDecl.getSupertype().getName()
           + "_"
           + derivedObjDecl.getName();
  }

  private void defineSubtypeRelationshipInit ( final SubtypeRelationshipDeclaration subtypeRelationshipDecl,
                                               final StatementGroup forBody )
  {
    final ObjectDeclaration superTypeObjDecl = subtypeRelationshipDecl.getSupertype();
    if ( objectDeclaration == superTypeObjDecl )
    {
      // The current object is the supertype
      final Variable derived = new Variable("derived");
      final List<Expression> countFnExprList = new ArrayList<Expression>();
      for ( final ObjectDeclaration derivedObjDecl : subtypeRelationshipDecl.getSubtypes() )
      {
        final Function countLinkFunction = getMainObjectTranslator().getRelationshipTranslator(subtypeRelationshipDecl.getSuperToSubSpec(derivedObjDecl))
                                                                    .getSubclassOverrides()
                                                                    .getCountFunction();
        final Expression countFnExpr = countLinkFunction.asFunctionCall(derived.asExpression());
        countFnExprList.add(countFnExpr);
      }
      final Variable isLinkedVar = new Variable(new TypeUsage(FundamentalType.BOOL),
                                                "is" + subtypeRelationshipDecl.getName() + "Linked",
                                                callLinkCountFunctions(countFnExprList));
      forBody.appendStatement(isLinkedVar.asStatement());

      final Expression setlinkedFnCall = new Function("setLinked" + subtypeRelationshipDecl.getName()).asFunctionCall(derived.asExpression(),
                                                                                                                      true);
      final StatementGroup ifstatements = new StatementGroup();
      ifstatements.appendStatement(setlinkedFnCall.asStatement());
      final IfStatement linkTest = new IfStatement(isLinkedVar.asExpression(), ifstatements);
      forBody.appendStatement(linkTest);
    }
  }

  private Expression callLinkCountFunctions ( final List<Expression> countFnExprList )
  {
    if ( countFnExprList.size() == 1 )
    {
      return countFnExprList.get(0);
    }
    else if ( countFnExprList.size() == 2 )
    {
      final Expression current = countFnExprList.get(0);
      final Expression last = countFnExprList.get(1);
      return new BinaryExpression(current, BinaryOperator.OR, last);
    }
    if ( countFnExprList.size() > 2 )
    {
      // recursive call to self
      final Expression current = countFnExprList.get(0);
      return new BinaryExpression(current,
                                  BinaryOperator.OR,
                                  callLinkCountFunctions(countFnExprList.subList(1, countFnExprList.size() - 1)));
    }
    else
    {
      throw new RuntimeException("callLinkCountFunctions encountered error in recursive loop");
    }
  }

  private void defineSubtypeLinkerFns ( final SubtypeRelationshipDeclaration subtypeRelationshipDecl )
  {
    final ObjectDeclaration superTypeObjDecl = subtypeRelationshipDecl.getSupertype();
    final List<? extends ObjectDeclaration> subTypeObjListDecl = subtypeRelationshipDecl.getSubtypes();

    if ( objectDeclaration == superTypeObjDecl )
    {
      // The current object is the supertype
      for ( final ObjectDeclaration derivedObjDecl : subtypeRelationshipDecl.getSubtypes() )
      {
        final Variable relationshipVar = getSubTypeRelationship(subtypeRelationshipDecl,
                                                                derivedObjDecl,
                                                                RelationshipDirection.BaseToDerived);
        final Function linkSubtypeFunction = getMainObjectTranslator().getRelationshipTranslator(subtypeRelationshipDecl.getSuperToSubSpec(derivedObjDecl))
                                                                      .getSubclassOverrides()
                                                                      .getSingleLinkFunction();
        final Function unlinkSubtypeFunction = getMainObjectTranslator().getRelationshipTranslator(subtypeRelationshipDecl.getSuperToSubSpec(derivedObjDecl))
                                                                        .getSubclassOverrides()
                                                                        .getSingleUnlinkFunction();
        implementSupertypeLinkerFns(relationshipVar,
                                    linkSubtypeFunction,
                                    unlinkSubtypeFunction,
                                    superTypeObjDecl,
                                    derivedObjDecl,
                                    RelationshipDirection.BaseToDerived);
      }
    }
    else if ( subTypeObjListDecl.contains(objectDeclaration) )
    {
      // the current object is the subtype
      final Variable relationshipVar = getSubTypeRelationship(subtypeRelationshipDecl,
                                                              objectDeclaration,
                                                              RelationshipDirection.DerivedToBase);
      final Function linkSubtypeFunction = getMainObjectTranslator().getRelationshipTranslator(subtypeRelationshipDecl.getSubToSuperSpec(objectDeclaration))
                                                                    .getSubclassOverrides()
                                                                    .getSingleLinkFunction();
      final Function unlinkSubtypeFunction = getMainObjectTranslator().getRelationshipTranslator(subtypeRelationshipDecl.getSubToSuperSpec(objectDeclaration))
                                                                      .getSubclassOverrides()
                                                                      .getSingleUnlinkFunction();
      implementSupertypeLinkerFns(relationshipVar,
                                  linkSubtypeFunction,
                                  unlinkSubtypeFunction,
                                  objectDeclaration,
                                  subtypeRelationshipDecl.getSupertype(),
                                  RelationshipDirection.DerivedToBase);
    }
  }

  private void defineSubtypeNavFns ( final SubtypeRelationshipDeclaration subtypeRelationshipDecl )
  {
    final ObjectDeclaration superTypeObjDecl = subtypeRelationshipDecl.getSupertype();
    final List<? extends ObjectDeclaration> subTypeObjListDecl = subtypeRelationshipDecl.getSubtypes();

    if ( objectDeclaration == superTypeObjDecl )
    {
      // The current object is the supertype
      for ( final ObjectDeclaration derivedObjDecl : subtypeRelationshipDecl.getSubtypes() )
      {
        final Variable relationshipVar = getSubTypeRelationship(subtypeRelationshipDecl,
                                                                derivedObjDecl,
                                                                RelationshipDirection.BaseToDerived);
        final Function navigateSubtypeFunction = getMainObjectTranslator().getRelationshipTranslator(subtypeRelationshipDecl.getSuperToSubSpec(derivedObjDecl))
                                                                          .getSubclassOverrides()
                                                                          .getNavigateFunction();
        final Function countSubtypeFunction = getMainObjectTranslator().getRelationshipTranslator(subtypeRelationshipDecl.getSuperToSubSpec(derivedObjDecl))
                                                                       .getSubclassOverrides()
                                                                       .getCountFunction();
        implementSupertypeNavigateFns(relationshipVar,
                                      navigateSubtypeFunction,
                                      countSubtypeFunction,
                                      superTypeObjDecl,
                                      derivedObjDecl,
                                      RelationshipDirection.BaseToDerived);
      }
    }
    else if ( subTypeObjListDecl.contains(objectDeclaration) )
    {
      // the current object is the subtype
      final Variable relationshipVar = getSubTypeRelationship(subtypeRelationshipDecl,
                                                              objectDeclaration,
                                                              RelationshipDirection.DerivedToBase);
      final Function linkSubtypeFunction = getMainObjectTranslator().getRelationshipTranslator(subtypeRelationshipDecl.getSubToSuperSpec(objectDeclaration))
                                                                    .getSubclassOverrides()
                                                                    .getNavigateFunction();
      final Function unlinkSubtypeFunction = getMainObjectTranslator().getRelationshipTranslator(subtypeRelationshipDecl.getSubToSuperSpec(objectDeclaration))
                                                                      .getSubclassOverrides()
                                                                      .getCountFunction();
      implementSupertypeNavigateFns(relationshipVar,
                                    linkSubtypeFunction,
                                    unlinkSubtypeFunction,
                                    objectDeclaration,
                                    subtypeRelationshipDecl.getSupertype(),
                                    RelationshipDirection.DerivedToBase);
    }
  }

  private Variable getSubTypeRelationship ( final SubtypeRelationshipDeclaration subtypeRelationshipDecl,
                                            final ObjectDeclaration derivedObjDecl,
                                            final RelationshipDirection direction )
  {
    final ObjectDeclaration superTypeObjDecl = subtypeRelationshipDecl.getSupertype();
    Variable relationshipVar = relationshipDataMemberList.get(getSubtypeRelationshipName(subtypeRelationshipDecl, derivedObjDecl));
    if ( relationshipVar == null )
    {
      // Each relationship has an associated relationship mapper class and
      // relationship mapper sql class. Need to get
      // the class representations for these so they can be used to form and
      // initialise the actual relationship data
      // member to be used by this population class.
      final Class relationshipMapperType = objectTranslator.getFrameworkTranslator()
                                                           .getRelationshipTranslator(subtypeRelationshipDecl)
                                                           .getSuperToSubtypeRelationshipMapperClass(superTypeObjDecl,
                                                                                                     derivedObjDecl);
      final Class relationshipMapperSqlType = objectTranslator.getFrameworkTranslator()
                                                              .getRelationshipTranslator(subtypeRelationshipDecl)
                                                              .getSuperToSubtypeRelationshipMapperSqlClass(superTypeObjDecl,
                                                                                                           derivedObjDecl);

      // The base type object is always on left-hand side of one-to-one
      // relationship
      // used to implement the super-subtype association.
      final Function objectDeletedFn = (direction == RelationshipDirection.BaseToDerived ? new Function("objectDeletedLhs")
                                                                                        : new Function("objectDeletedRhs"));
      final String relationshipVarName = getSubtypeRelationshipName(subtypeRelationshipDecl, derivedObjDecl);
      relationshipVar = addRelationshipDataMember(relationshipMapperType,
                                                  relationshipMapperSqlType,
                                                  relationshipVarName,
                                                  objectDeletedFn);
    }
    return relationshipVar;
  }


  private void implementNormalLinkerFns ( final NormalRelationshipDeclaration normRelDecl,
                                          final Variable relVar,
                                          final RelationshipDirection direction,
                                          final Function mainLinkFn,
                                          final Function mainUnlinkFn,
                                          final Function mainNavigateFn,
                                          final Function mainCountFn,
                                          final ObjectDeclaration relLhsObject,
                                          final ObjectDeclaration relRhsObject )
  {

    // The parameters defined for the functions have the names lhs and rhs
    // respectively. The actual object types of these parameters will depend
    // on the object type of the population; this type will always be defined
    // first. Therefore use the relationship direction to determine the type
    // order for the linker/navigate service signatures.
    final ObjectDeclaration paramLhsObjDecl = (direction == RelationshipDirection.LeftToRight ? relLhsObject : relRhsObject);
    final ObjectDeclaration paramRhsObjDecl = (direction == RelationshipDirection.LeftToRight ? relRhsObject : relLhsObject);
    final ObjectTranslator paramLhsType = objectTranslator.getFrameworkTranslator().getObjectTranslator(paramLhsObjDecl);
    final ObjectTranslator paramRhsType = objectTranslator.getFrameworkTranslator().getObjectTranslator(paramRhsObjDecl);

    // Define the link Function
    final Function linkFn = defineLinkerFn("link", relVar, paramLhsType, paramRhsType, mainLinkFn, direction);
    bodyFile.addFunctionDefinition(linkFn);

    // Define the unlink Function
    final Function unlinkFn = defineLinkerFn("unlink", relVar, paramLhsType, paramRhsType, mainUnlinkFn, direction);
    bodyFile.addFunctionDefinition(unlinkFn);

    // Define the navigate Function
    final Function populationNavigateFn = defineNavigateFn(mainNavigateFn, relVar, direction, paramLhsType, paramRhsType);
    bodyFile.addFunctionDefinition(populationNavigateFn);

    // Define the Relationship count function
    final Function populationCountFn = defineCountFn(mainCountFn, relVar, direction, paramLhsType, paramRhsType);
    bodyFile.addFunctionDefinition(populationCountFn);
  }


  private void implementAssociativeNavFns ( final ObjectDeclaration ObjectOnLhsOfNav,
                                            final ObjectDeclaration ObjectOnRhsOfNav,
                                            final Variable relationshipVar,
                                            final Function navigateFunction,
                                            final Function countFunction,
                                            final RelationshipDirection direction )
  {
    final ObjectTranslator paramLhsType = objectTranslator.getFrameworkTranslator().getObjectTranslator(ObjectOnLhsOfNav);
    final ObjectTranslator paramRhsType = objectTranslator.getFrameworkTranslator().getObjectTranslator(ObjectOnRhsOfNav);

    final Function populationNavFn = defineNavigateFn(navigateFunction, relationshipVar, direction, paramLhsType, paramRhsType);
    bodyFile.addFunctionDefinition(populationNavFn);

    final Function populationCountFn = defineCountFn(countFunction, relationshipVar, direction, paramLhsType, paramRhsType);
    bodyFile.addFunctionDefinition(populationCountFn);
  }


  private void implementAssociativeCorrelateFn ( final ObjectDeclaration ObjectOnLhsOfNav,
                                                 final ObjectDeclaration ObjectOnRhsOfNav,
                                                 final ObjectDeclaration ObjectOnAssOfNav,
                                                 final Function correlateFunction,
                                                 final Variable relationshipVar,
                                                 final RelationshipDirection direction )
  {
    final ObjectTranslator paramLhsType = objectTranslator.getFrameworkTranslator().getObjectTranslator(ObjectOnLhsOfNav);
    final ObjectTranslator paramRhsType = objectTranslator.getFrameworkTranslator().getObjectTranslator(ObjectOnRhsOfNav);
    final ObjectTranslator paramAssType = objectTranslator.getFrameworkTranslator().getObjectTranslator(ObjectOnAssOfNav);

    final Class paramAssClass = getMainDomainTranslator().getObjectTranslator(ObjectOnAssOfNav).getMainClass();

    final Function populationCorrelateFn = populationClass.createMemberFunction(relationshipLinkers,
                                                                                correlateFunction.getName(),
                                                                                Visibility.PUBLIC);
    final Variable lhsParamVar = populationCorrelateFn.createParameter(new TypeUsage(Architecture.objectPtr(new TypeUsage(paramLhsType.getClass("ImplementationClass"))),
                                                                                     TypeUsage.ConstReference),
                                                                       "lhs");
    final Variable rhsParamVar = populationCorrelateFn.createParameter(new TypeUsage(Architecture.objectPtr(new TypeUsage(paramRhsType.getClass("ImplementationClass"))),
                                                                                     TypeUsage.ConstReference),
                                                                       "rhs");
    populationCorrelateFn.setReturnType(new TypeUsage(Architecture.objectPtr(new TypeUsage(paramAssClass))));

    // Create cpp line:
    // RelationshipR11Mapper::NavigatedAssType assObj =
    // relationshipR1.correlateFromLhsToRhs(lhs,rhs);
    final Function correlateFn = getRelationshipFunction("correlate", direction);
    final Expression correlateFnCall = correlateFn.asFunctionCall(relationshipVar.asExpression(), false, new Expression[]
      { lhsParamVar.asExpression(), rhsParamVar.asExpression() });

    final Class relationshipType = (Class)relationshipVar.getType().getType();
    final Class correlatedType = relationshipType.referenceNestedType("NavigatedAssType");
    final Variable correlatedVar = new Variable(new TypeUsage(correlatedType), "assObj", correlateFnCall);
    populationCorrelateFn.getCode().appendStatement(correlatedVar.asStatement());

    // Create cpp line:
    // return
    // maslo_One_To_One_Link_Test_Object_CCPopulation::getPopulation().findObject(assObj);
    final Expression getPopulationFnCall = paramAssType.getClass("PopulationClass").callStaticFunction("getPopulation");
    final Function findObjectFn = new Function("findObject");
    final Expression findObjectFnCall = findObjectFn.asFunctionCall(getPopulationFnCall, false, correlatedVar.asExpression());
    populationCorrelateFn.getCode().appendStatement(new ReturnStatement(findObjectFnCall));

    bodyFile.addFunctionDefinition(populationCorrelateFn);
  }


  private void implementAssociativeLinkerFns ( final AssociativeRelationshipDeclaration assocRelationshipDecl,
                                               final Variable relationshipVar,
                                               final Function linkFunction,
                                               final Function unlinkFunction,
                                               final RelationshipDirection direction )
  {
    // The parameters defined for the functions have the names lhs and rhs and
    // asso respectively. The actual
    // object types of the first two parameters will depend on the object stored
    // by the population class; this
    // type will always be defined first and the assoc object is always defined
    // last. Therefore use the
    // relationship direction to determine the object type order for the linker
    // service signatures.
    final ObjectDeclaration paramLhsObjDecl = (direction == RelationshipDirection.LeftToRight ? assocRelationshipDecl.getLeftObject()
                                                                                             : assocRelationshipDecl.getRightObject());
    final ObjectDeclaration paramRhsObjDecl = (direction == RelationshipDirection.LeftToRight ? assocRelationshipDecl.getRightObject()
                                                                                             : assocRelationshipDecl.getLeftObject());
    final ObjectDeclaration paramAssoObjDecl = assocRelationshipDecl.getAssocObject();

    final ObjectTranslator paramLhsType = objectTranslator.getFrameworkTranslator().getObjectTranslator(paramLhsObjDecl);
    final ObjectTranslator paramRhsType = objectTranslator.getFrameworkTranslator().getObjectTranslator(paramRhsObjDecl);
    final ObjectTranslator paramAssoType = objectTranslator.getFrameworkTranslator().getObjectTranslator(paramAssoObjDecl);

    // Define the link function
    final Function populationLinkFn = populationClass.createMemberFunction(relationshipLinkers,
                                                                           linkFunction.getName(),
                                                                           Visibility.PUBLIC);
    populationLinkFn.createParameter(new TypeUsage(Architecture.objectPtr(new TypeUsage(paramLhsType.getClass("ImplementationClass"))),
                                                   TypeUsage.ConstReference),
                                     "lhs");
    populationLinkFn.createParameter(new TypeUsage(Architecture.objectPtr(new TypeUsage(paramRhsType.getClass("ImplementationClass"))),
                                                   TypeUsage.ConstReference),
                                     "rhs");
    populationLinkFn.createParameter(new TypeUsage(Architecture.objectPtr(new TypeUsage(paramAssoType.getClass("ImplementationClass"))),
                                                   TypeUsage.ConstReference),
                                     "asso");
    bodyFile.addFunctionDefinition(populationLinkFn);

    // Create cpp line:
    // relationshipR1.linkFromRhsToLhs(lhs,rhs,asso);
    final Function mapperLinkFn = getRelationshipFunction("link", direction);
    final Expression mapperLinkFnCall = mapperLinkFn.asFunctionCall(relationshipVar.asExpression(), false, new Expression[]
      { new Variable("lhs").asExpression(), new Variable("rhs").asExpression(), new Variable("asso").asExpression() });
    populationLinkFn.getCode().appendExpression(mapperLinkFnCall);

    // Define the unlink function
    final Function populationUnlinkFn = populationClass.createMemberFunction(relationshipLinkers,
                                                                             unlinkFunction.getName(),
                                                                             Visibility.PUBLIC);
    populationUnlinkFn.createParameter(new TypeUsage(Architecture.objectPtr(new TypeUsage(paramLhsType.getClass("ImplementationClass"))),
                                                     TypeUsage.ConstReference),
                                       "lhs");
    populationUnlinkFn.createParameter(new TypeUsage(Architecture.objectPtr(new TypeUsage(paramRhsType.getClass("ImplementationClass"))),
                                                     TypeUsage.ConstReference),
                                       "rhs");
    populationUnlinkFn.createParameter(new TypeUsage(Architecture.objectPtr(new TypeUsage(paramAssoType.getClass("ImplementationClass"))),
                                                     TypeUsage.ConstReference),
                                       "asso");
    bodyFile.addFunctionDefinition(populationUnlinkFn);

    // Create cpp line:
    // relationshipR1.unlinkFromRhsToLhs(lhs,rhs);
    final Function mapperUnLinkFn = getRelationshipFunction("unlink", direction);
    final Expression mapperUnlinkFnCall = mapperUnLinkFn.asFunctionCall(relationshipVar.asExpression(), false, new Expression[]
      { new Variable("lhs").asExpression(), new Variable("rhs").asExpression(), new Variable("asso").asExpression() });
    populationUnlinkFn.getCode().appendExpression(mapperUnlinkFnCall);
  }


  private void implementSupertypeLinkerFns ( final Variable relVar,
                                             final Function mainLinkFn,
                                             final Function mainUnLinkFn,
                                             final ObjectDeclaration lhsObjDecl,
                                             final ObjectDeclaration rhsObjDecl,
                                             final RelationshipDirection direction )
  {
    final ObjectTranslator paramLhsType = objectTranslator.getFrameworkTranslator().getObjectTranslator(lhsObjDecl);
    final ObjectTranslator paramRhsType = objectTranslator.getFrameworkTranslator().getObjectTranslator(rhsObjDecl);

    final Function linkFn = defineLinkerFn("link", relVar, paramLhsType, paramRhsType, mainLinkFn, direction);
    bodyFile.addFunctionDefinition(linkFn);

    final Function unlinkFn = defineLinkerFn("unlink", relVar, paramLhsType, paramRhsType, mainUnLinkFn, direction);
    bodyFile.addFunctionDefinition(unlinkFn);
  }

  private void implementSupertypeNavigateFns ( final Variable relVar,
                                               final Function mainNavigateFn,
                                               final Function mainCountFn,
                                               final ObjectDeclaration lhsObjDecl,
                                               final ObjectDeclaration rhsObjDecl,
                                               final RelationshipDirection direction )
  {
    final ObjectTranslator paramLhsType = objectTranslator.getFrameworkTranslator().getObjectTranslator(lhsObjDecl);
    final ObjectTranslator paramRhsType = objectTranslator.getFrameworkTranslator().getObjectTranslator(rhsObjDecl);

    final Function navigateFn = defineNavigateFn(mainNavigateFn, relVar, direction, paramLhsType, paramRhsType);
    bodyFile.addFunctionDefinition(navigateFn);

    final Function countFn = defineCountFn(mainCountFn, relVar, direction, paramLhsType, paramRhsType);
    bodyFile.addFunctionDefinition(countFn);
  }


  private Function defineLinkerFn ( final String functionName,
                                    final Variable relationshipVar,
                                    final ObjectTranslator paramLhsType,
                                    final ObjectTranslator paramRhsType,
                                    final Function mainLinkerFn,
                                    final RelationshipDirection direction )
  {
    // Define the link function
    final Function populationLinkFn = populationClass.createMemberFunction(relationshipLinkers,
                                                                           mainLinkerFn.getName(),
                                                                           Visibility.PUBLIC);
    populationLinkFn.createParameter(new TypeUsage(Architecture.objectPtr(new TypeUsage(paramLhsType.getClass("ImplementationClass"))),
                                                   TypeUsage.ConstReference),
                                     "lhs");
    populationLinkFn.createParameter(new TypeUsage(Architecture.objectPtr(new TypeUsage(paramRhsType.getClass("ImplementationClass"))),
                                                   TypeUsage.ConstReference),
                                     "rhs");

    // Create cpp line:
    // relationshipR1.linkFromRhsToLhs(rhs,lhs);
    final Function mapperLinkFn = getRelationshipFunction(functionName, direction);
    final Expression mapperLinkFnCall = mapperLinkFn.asFunctionCall(relationshipVar.asExpression(), false, new Expression[]
      { new Variable("lhs").asExpression(), new Variable("rhs").asExpression() });
    populationLinkFn.getCode().appendExpression(mapperLinkFnCall);
    return populationLinkFn;
  }


  private Function defineNavigateFn ( final Function mainNavigateFn,
                                      final Variable relVar,
                                      final RelationshipDirection direction,
                                      final ObjectTranslator paramLhsType,
                                      final ObjectTranslator paramRhsType )
  {
    // Define the navigate Function
    final Function populationNavigateFn = populationClass.createMemberFunction(relationshipNavs,
                                                                               mainNavigateFn.getName(),
                                                                               Visibility.PUBLIC);
    populationNavigateFn.setReturnType(mainNavigateFn.getReturnType());
    populationNavigateFn.createParameter(new TypeUsage(Architecture.objectPtr(new TypeUsage(paramLhsType.getClass("ImplementationClass"))),
                                                       TypeUsage.ConstReference),
                                         "lhs");

    // Create cpp line:
    // const R1Mapper::NavigatedRhsType
    // navigatedSet(relationshipR1.navigateFromLhsToRhs(lhs));
    final Function mapperNavigateFn = getRelationshipFunction("navigate", direction);
    final Expression mapperNavigateFnCall = mapperNavigateFn.asFunctionCall(relVar.asExpression(),
                                                                            false,
                                                                            new Variable("lhs").asExpression());

    final TypeUsage navigatedType = getNavigatedType(relVar, direction);
    final Variable navigatedVar = new Variable(navigatedType, "navigated", new Expression[]
      { mapperNavigateFnCall });
    populationNavigateFn.getCode().appendStatement(navigatedVar.asStatement());

    // Create cpp line:
    // return
    // maslo_Find_Test_Object_HPopulation::getPopulation().findObject(navigated);
    final Expression getPopulationFnCall = paramRhsType.getClass("PopulationClass").callStaticFunction("getPopulation");
    final Function findObjectFn = new Function("findObject");
    final Expression findObjectFnCall = findObjectFn.asFunctionCall(getPopulationFnCall, false, navigatedVar.asExpression());
    populationNavigateFn.getCode().appendStatement(new ReturnStatement(findObjectFnCall));
    return populationNavigateFn;
  }


  private Function defineCountFn ( final Function countFn,
                                   final Variable relVar,
                                   final RelationshipDirection direction,
                                   final ObjectTranslator paramLhsType,
                                   final ObjectTranslator paramRhsType )
  {
    final Function populationCountFn = populationClass.createMemberFunction(relationshipCounts,
                                                                            countFn.getName(),
                                                                            Visibility.PUBLIC);
    populationCountFn.setReturnType(countFn.getReturnType());
    populationCountFn.createParameter(new TypeUsage(Architecture.objectPtr(new TypeUsage(paramLhsType.getClass("ImplementationClass"))),
                                                    TypeUsage.ConstReference),
                                      "lhs");

    // Create cpp line:
    // return R11Mapper.countFromLhsToAss(lhs);
    final Function countImplFn = getRelationshipFunction("count", direction);
    final Expression countImplFnCall = countImplFn.asFunctionCall(relVar.asExpression(), false, populationCountFn.getParameters()
                                                                                                                 .get(0)
                                                                                                                 .asExpression());
    populationCountFn.getCode().appendStatement(new ReturnStatement(countImplFnCall));
    return populationCountFn;
  }


  private Function addConstructor ()
  {
    final Function constructor = populationClass.createConstructor(constructionDestruction, Visibility.PRIVATE);
    bodyFile.addFunctionDefinition(constructor);
    final List<Expression> constructorArgs = Arrays.asList(new Expression[] {}); // no
                                                                                 // arg
                                                                                 // constructor
    constructor.setSuperclassArgs(baseClass, constructorArgs);
    return constructor;
  }


  private Function addDestructor ()
  {
    final Function destructor = populationClass.createDestructor(constructionDestruction, Visibility.PRIVATE);
    bodyFile.addFunctionDefinition(destructor);
    return destructor;
  }


  private Function addInitialiseMethod ()
  {
    final Function initialiseMethod = populationClass.createMemberFunction(intialisation, "initialise", Visibility.PUBLIC);
    bodyFile.addFunctionDefinition(initialiseMethod);
    final Function initFn = new Function("initialise");
    initialiseMethod.getCode()
                    .appendStatement(new ExpressionStatement(initFn.asFunctionCall(Database.mapperInstance.asExpression(), true)));

    return initialiseMethod;
  }


  private void addCreateMethod ()
  {
    creatorFn = populationClass.redefineFunction(instanceCreation,
                                                 getMainObjectTranslator().getPopulation().getCreateInstance(),
                                                 Visibility.PUBLIC);
    bodyFile.addFunctionDefinition(creatorFn);
  }

  private void addCreateMethodBody ()
  {
    final List<Expression> params = new ArrayList<Expression>();
    for ( final Variable param : creatorFn.getParameters() )
    {
      params.add(param.asExpression());
    }

    final Function createInstFn = new Function("createInstance");
    if ( identifierFindAtts.size() == 0 )
    {
      creatorFn.getCode().appendStatement(new ReturnStatement(createInstFn.asFunctionCall(Database.mapperInstance.asExpression(),
                                                                                          true,
                                                                                          params)));
    }
    else
    {

      final Class objPtr = Architecture.objectPtr(new TypeUsage(getMainObjectTranslator().getMainClass()));
      final Class objImplPtr = Architecture.objectPtr(new TypeUsage(objectTranslator.getClass(ImplementationClass.KEY_NAME)));

      final Variable objVar = new Variable(new TypeUsage(objPtr),
                                           "obj",
                                           createInstFn.asFunctionCall(Database.mapperInstance.asExpression(), true, params));
      creatorFn.getCode().appendStatement(objVar.asStatement());

      final Expression fullCachingCondition = new Function("fullCachingEnabled").asFunctionCall(Database.mapperInstance.asExpression(),
                                                                                                true);
      final CodeBlock cachingTrueStatements = new CodeBlock();
      final CodeBlock cachingFalseStatements = new CodeBlock();
      final IfStatement cachingIfBlock = new IfStatement(fullCachingCondition, cachingTrueStatements, cachingFalseStatements);
      creatorFn.getCode().appendStatement(cachingIfBlock);

      final Function downcastFn = new Function("downcast");
      downcastFn.addTemplateSpecialisation(new TypeUsage(objectTranslator.getClass(ImplementationClass.KEY_NAME)));
      final Expression downcastFnCall = downcastFn.asFunctionCall(objVar.asExpression(), false);
      final Variable objImplVar = new Variable(new TypeUsage(objImplPtr), "objImpl", new Expression[]
        { downcastFnCall });
      cachingTrueStatements.appendStatement(objImplVar.asStatement());

      for ( final Map.Entry<IdentifierDeclaration, Variable> identifierFind : identifierFindAtts.entrySet() )
      {
        final TypedefType objKeyType = objectTranslator.getKeyType(identifierFind.getKey());
        final Function objKeyGetterFn = objectTranslator.getKeyGetterFn(identifierFind.getKey());

        final Class lookupContainerType = Boost.unordered_map(new TypeUsage(objKeyType), new TypeUsage(objPtr));
        final Expression keyExpr = objKeyGetterFn.asFunctionCall(objImplVar.asExpression(), true);
        final Expression valueTypeExpr = lookupContainerType.referenceNestedType("value_type").callConstructor(new Expression[]
          { keyExpr, objImplVar.asExpression() });

        final Expression insertFnCall = new Function("insert").asFunctionCall(identifierFind.getValue().asExpression(),
                                                                              false,
                                                                              valueTypeExpr);
        final Expression clearFnCall = new Function("clear").asFunctionCall(identifierFind.getValue().asExpression(), false);

        cachingTrueStatements.appendExpression(insertFnCall);
        cachingFalseStatements.appendExpression(clearFnCall);
      }
      creatorFn.getCode().appendStatement(new ReturnStatement(objVar.asExpression()));
    }
  }

  private void addDeleteMethod ()
  {
    deleterFn = populationClass.redefineFunction(instanceCreation,
                                                 getMainObjectTranslator().getPopulation().getDeleteInstance(),
                                                 Visibility.PUBLIC);
    bodyFile.addFunctionDefinition(deleterFn);

    deleterCodeBlock = new CodeBlock();
    deleterFn.getCode().prependStatement(deleterCodeBlock);
  }

  private void addDeleteMethodBody ()
  {
    if ( identifierFindAtts.size() > 0 )
    {
      final Function downcastFn = new Function("downcast");
      downcastFn.addTemplateSpecialisation(new TypeUsage(objectTranslator.getClass(ImplementationClass.KEY_NAME)));
      final Expression downcastFnCall = downcastFn.asFunctionCall(deleterFn.getParameters().get(0).asExpression(), false);

      for ( final Map.Entry<IdentifierDeclaration, Variable> identifierEntry : identifierFindAtts.entrySet() )
      {
        final Function indexKeyFn = objectTranslator.getKeyGetterFn(identifierEntry.getKey());
        final Expression keyExpr = indexKeyFn.asFunctionCall(downcastFnCall, true);
        final Expression eraseFnCall = new Function("erase").asFunctionCall(identifierEntry.getValue().asExpression(),
                                                                            false,
                                                                            keyExpr);
        deleterFn.getCode().appendExpression(eraseFnCall);
      }
    }

    // Create cpp line:
    // mapper->deleteInstance(instance);
    final Expression deleteInstanceFnCall = new Function("deleteInstance").asFunctionCall(Database.mapperInstance.asExpression(),
                                                                                          true,
                                                                                          deleterFn.getParameters()
                                                                                                   .get(0)
                                                                                                   .asExpression());
    deleterFn.getCode().appendExpression(deleteInstanceFnCall);
  }

  private Function addGetPopulationMethod ()
  {
    final Function getPopulationMethod = populationClass.createStaticFunction(singletonRegistration,
                                                                              "getPopulation",
                                                                              Visibility.PUBLIC);
    getPopulationMethod.setReturnType(new TypeUsage(populationClass, TypeUsage.Reference));
    final Variable population = new Variable(new TypeUsage(populationClass), "population");
    population.setStatic(true);
    getPopulationMethod.getCode().appendStatement(new VariableDefinitionStatement(population));
    getPopulationMethod.getCode().appendStatement(new ReturnStatement(population.asExpression()));
    bodyFile.addFunctionDefinition(getPopulationMethod);
    return getPopulationMethod;
  }


  private Function addSingletonRegistration ( final Function populationMethod )
  {
    final Function registerSingleton = new Function("registerSingleton").inheritInto(populationClass);
    final Variable registered = populationClass.createStaticVariable(populationAttributes,
                                                                     "registered",
                                                                     new TypeUsage(FundamentalType.BOOL),
                                                                     registerSingleton.asFunctionCall(populationMethod.asFunctionPointer()),
                                                                     Visibility.PRIVATE);
    bodyFile.addVariableDefinition(registered);
    return registerSingleton;
  }


  private void registerInitialisationFunction ()
  {
    final Function getPopFnCall = new Function("getPopulation").inheritInto(populationClass);
    final Function initFnCall = new Function("initialise").inheritInto(populationClass);

    final Expression BoostBindGetPopFnCall = Boost.bind.asFunctionCall(new Expression[]
      { getPopFnCall.asFunctionPointer() });
    final Expression BoostBindInitFnCall = Boost.bind.asFunctionCall(new Expression[]
      { initFnCall.asFunctionPointer(), BoostBindGetPopFnCall });

    final Expression regInitFnCall = Architecture.registerProcessListener("initialising", BoostBindInitFnCall);

    final Variable initialised = populationClass.createStaticVariable(populationAttributes,
                                                                      "initialised",
                                                                      new TypeUsage(Architecture.listenerConnection),
                                                                      regInitFnCall,
                                                                      Visibility.PRIVATE);
    bodyFile.addVariableDefinition(initialised);
  }


  private void addFindObjectMethods ()
  {
    final Class objPtrClass = Architecture.objectPtr(new TypeUsage(getMainObjectTranslator().getMainClass()));

    // Define the findObject method for single instance
    final Function findObject = populationClass.createMemberFunction(findObjectRoutines, "findObject", Visibility.PUBLIC);
    final Variable objVar = findObject.createParameter(new TypeUsage(Architecture.ID_TYPE, TypeUsage.ConstReference), "obj");
    findObject.setReturnType(new TypeUsage(objPtrClass));
    findObject.getCode()
              .appendStatement(new ReturnStatement(new Function("find").asFunctionCall(new Variable("mapper").asExpression(),
                                                                                       true,
                                                                                       objVar.asExpression())));
    bodyFile.addFunctionDefinition(findObject);

    // Define the findObject method for set of instances.
    final Function findSetObject = populationClass.createMemberFunction(findObjectRoutines, "findObject", Visibility.PUBLIC);
    final Class setType = populationClass.referenceNestedType("MapperType").referenceNestedType("PsObjectIdSet");
    final Variable objSetVar = findSetObject.createParameter(new TypeUsage(setType, TypeUsage.ConstReference), "obj");
    findSetObject.setReturnType(new TypeUsage(Architecture.set(new TypeUsage(objPtrClass))));

    // Create cpp line:
    // MapperType::PsObjectPtrSet objectSet = mapper->find(obj);
    final Class objectSetType = populationClass.referenceNestedType("MapperType").referenceNestedType("PsObjectPtrSet");
    final Variable objectSetVar = new Variable(new TypeUsage(objectSetType),
                                               "objectSet",
                                               new Function("find").asFunctionCall(new Variable("mapper").asExpression(),
                                                                                   true,
                                                                                   objSetVar.asExpression()));
    findSetObject.getCode().appendStatement(objectSetVar.asStatement());

    // Create cpp line:
    // return ::SWA::Set< ::SWA::ObjectPtr<
    // ::masld_PERF::maslo_Find_Test_Object_H>
    // >(objectSet.begin(),objectSet.end());
    final Class returnSetType = Architecture.set(new TypeUsage(Architecture.objectPtr(new TypeUsage(getMainObjectTranslator().getMainClass()))));
    final Expression beginFnCall = new Function("begin").asFunctionCall(objectSetVar.asExpression(), false);
    final Expression endFnCall = new Function("end").asFunctionCall(objectSetVar.asExpression(), false);
    findSetObject.getCode()
                 .appendStatement(new ReturnStatement(returnSetType.callConstructor(beginFnCall, endFnCall, Literal.TRUE)));
    bodyFile.addFunctionDefinition(findSetObject);
  }


  private Variable addRelationshipDataMember ( final Class implRelationshipMapperType,
                                               final Class implRelationshipMapperSql,
                                               final String relationshipName,
                                               final Function objectDeletedFn )
  {
    // Class relationshipMapperType =
    // DomainTranslator.getInstance().getRelationshipTranslator(relationshipDecl).getRelationshipMapperClass();
    // Class relationshipMapperSqlType =
    // DomainTranslator.getInstance().getRelationshipTranslator(relationshipDecl).getRelationshipMapperSqlClass();
    // Function objectDeletedFn = getDeleteObjectFunction(relationshipDecl);
    final Variable relationshipVar = populationClass.createMemberVariable(populationAttributes,
                                                                          relationshipName + "Mapper",
                                                                          new TypeUsage(implRelationshipMapperType,
                                                                                        TypeUsage.Reference),
                                                                          Visibility.PRIVATE);
    relationshipDataMemberList.put(relationshipName, relationshipVar);

    // Set the Relationship data member as it is
    // part of contstructor initialiser list.
    constructor.setInitialValue(relationshipVar, implRelationshipMapperType.callStaticFunction("singleton"));

    // Create cpp line:
    // if(relationshipR1.isInitialised() == false){
    final Function isInit = new Function("isInitialised");
    final Expression isInitFnCall = isInit.asFunctionCall(relationshipVar.asExpression(), false);
    final Expression InitCondition = new BinaryExpression(isInitFnCall, BinaryOperator.EQUAL, new Literal("false"));
    final CodeBlock InitFailBlock = new CodeBlock();
    final IfStatement InitIf = new IfStatement(InitCondition, InitFailBlock);
    initialiseMethod.getCode().appendStatement(InitIf);
    initialiseMethod.getCode().appendStatement(new BlankLine(0));

    // Create cpp line:
    // relationshipR1.initialise(boost::shared_ptr<R1Mapper::RelSqlGeneratorType>(new
    // RelationshipR1SqlGenerator));

    final Function initialiseFn = new Function("initialise");
    final Class relSqlGenClass = Boost.getSharedPtrType(new TypeUsage(implRelationshipMapperType.referenceNestedType("RelSqlGeneratorType")));
    final Expression relSqlGenConstruct = relSqlGenClass.callConstructor(new NewExpression(new TypeUsage(implRelationshipMapperSql),
                                                                                           new ArrayList<Expression>()));
    final Expression initialiseFnCall = initialiseFn.asFunctionCall(relationshipVar.asExpression(), false, relSqlGenConstruct);
    InitFailBlock.appendExpression(initialiseFnCall);

    // Add a check in the deleteInstance to make sure an object is
    // not deleted while it has links to other object instances,
    final Function downCastFn = new Function("downcast");
    downCastFn.addTemplateSpecialisation(new TypeUsage(objectTranslator.getClass(ImplementationClass.KEY_NAME)));
    final Expression downCastFnCall = downCastFn.asFunctionCall(deleterFn.getParameters().get(0).asExpression(), false);
    final Expression objectDeletedFnCall = objectDeletedFn.asFunctionCall(relationshipVar.asExpression(), false, downCastFnCall);
    deleterCodeBlock.appendExpression(objectDeletedFnCall);
    return relationshipVar;
  }


  private void addCurrentState ()
  {
    if ( objectDeclaration.hasAssignerState() )
    {
      final StateMachineTranslator mainStateMachine = getMainObjectTranslator().getAssignerFsm();
      final TypeUsage currentStateType = new TypeUsage(mainStateMachine.getStateEnum());

      final Function getter = populationClass.redefineFunction(assignerStateRoutines,
                                                               getMainObjectTranslator().getPopulation().getGetCurrentState(),
                                                               Visibility.PUBLIC);
      final Function setter = populationClass.redefineFunction(assignerStateRoutines,
                                                               getMainObjectTranslator().getPopulation().getSetCurrentState(),
                                                               Visibility.PUBLIC);

      final String assignerStateKey = objectDeclaration.getDomain().getName() + "::" + objectDeclaration.getName();

      // Generate body for setter
      // Create cpp line:
      // AssignerStateMapper::singleton().setAssignerState("masld_SEVENT::maslo_Event_Testing_Service_Object",
      // newState);
      final Expression setAssignerStateFnCall = objectTranslator.getDatabase().setAssignerStateFnCall(assignerStateKey,
                                                                                                      setter.getParameters()
                                                                                                            .get(0)
                                                                                                            .asExpression());
      setter.getCode().appendExpression(setAssignerStateFnCall);

      // Generate body for getter
      final Expression getAssignerStateFnCall = objectTranslator.getDatabase().getAssignerStateFnCall(currentStateType,
                                                                                                      assignerStateKey);
      getter.getCode().appendStatement(new ReturnStatement(getAssignerStateFnCall));

      bodyFile.addFunctionDefinition(setter);
      bodyFile.addFunctionDefinition(getter);

      // Loop around the list of states and find the default
      // starting state for the assigner state model.
      Expression startingStateExpr = null;
      for ( final State state : mainStateMachine.getStates() )
      {
        if ( state.getType() == State.Type.ASSIGNER_START )
        {
          startingStateExpr = mainStateMachine.getState(state);
          break;
        }
      }

      if ( startingStateExpr == null )
      {
        throw new NullPointerException();
      }

      final Expression isAssignerSetFnCall = objectTranslator.getDatabase().isAssignerSetFnCall(assignerStateKey);

      final BinaryExpression assignerSetTestCondition = new BinaryExpression(isAssignerSetFnCall,
                                                                             BinaryOperator.EQUAL,
                                                                             new Literal("false"));
      final Expression setAssignerToDefaultStateFnCall = objectTranslator.getDatabase().setAssignerStateFnCall(assignerStateKey,
                                                                                                               startingStateExpr);
      final IfStatement testAssignerIf = new IfStatement(assignerSetTestCondition, setAssignerToDefaultStateFnCall.asStatement());
      initialiseMethod.getCode().appendStatement(testAssignerIf);
    }
  }


  private Function getDeleteObjectFunction ( final ObjectDeclaration leftObjectDecl,
                                             final ObjectDeclaration rightObjectDecl,
                                             final ObjectDeclaration assocObjectDecl )
  {
    Function deleteObjectFunction = null;
    if ( leftObjectDecl == objectDeclaration )
    {
      deleteObjectFunction = new Function("objectDeletedLhs");
    }
    else if ( rightObjectDecl == objectDeclaration )
    {
      deleteObjectFunction = new Function("objectDeletedRhs");
    }
    else if ( assocObjectDecl == objectDeclaration )
    {
      deleteObjectFunction = new Function("objectDeletedAss");
    }
    return deleteObjectFunction;
  }


  private Function getRelationshipFunction ( final String function, final RelationshipDirection direction )
  {
    Function relationshipFunction = null;
    if ( direction == RelationshipDirection.LeftToRight || direction == RelationshipDirection.RightToLeft )
    {
      relationshipFunction = new Function(function + "FromRhsToLhs");
      if ( direction == RelationshipDirection.LeftToRight )
      {
        relationshipFunction = new Function(function + "FromLhsToRhs");
      }
    }
    else if ( direction == RelationshipDirection.AssocToRight || direction == RelationshipDirection.AssocToLeft )
    {
      relationshipFunction = new Function(function + "FromAssToLhs");
      if ( direction == RelationshipDirection.AssocToRight )
      {
        relationshipFunction = new Function(function + "FromAssToRhs");
      }
    }
    else if ( direction == RelationshipDirection.DerivedToBase || direction == RelationshipDirection.BaseToDerived )
    {
      relationshipFunction = new Function(function + "FromRhsToLhs");
      if ( direction == RelationshipDirection.BaseToDerived )
      {
        relationshipFunction = new Function(function + "FromLhsToRhs");
      }
    }
    else
    {
      relationshipFunction = new Function(function + "FromLhsToAss");
      if ( direction == RelationshipDirection.RightToAssoc )
      {
        relationshipFunction = new Function(function + "FromRhsToAss");
      }
    }
    return relationshipFunction;
  }


  private TypeUsage getNavigatedType ( final Variable relationshipVar, final RelationshipDirection direction )
  {
    final Class relationshipClass = (Class)relationshipVar.getType().getType();
    TypeUsage navigatedType = null;
    if ( direction == RelationshipDirection.LeftToRight || direction == RelationshipDirection.RightToLeft )
    {
      navigatedType = new TypeUsage(relationshipClass.referenceNestedType("NavigatedRhsType"), TypeUsage.ConstReference);
      if ( direction == RelationshipDirection.RightToLeft )
      {
        navigatedType = new TypeUsage(relationshipClass.referenceNestedType("NavigatedLhsType"), TypeUsage.ConstReference);
      }
    }
    else if ( direction == RelationshipDirection.AssocToRight || direction == RelationshipDirection.AssocToLeft )
    {
      navigatedType = new TypeUsage(relationshipClass.referenceNestedType("NavigatedAssType"), TypeUsage.ConstReference);
      if ( direction == RelationshipDirection.AssocToRight )
      {
        navigatedType = new TypeUsage(relationshipClass.referenceNestedType("NavigatedAssType"), TypeUsage.ConstReference);
      }
    }
    else if ( direction == RelationshipDirection.DerivedToBase || direction == RelationshipDirection.BaseToDerived )
    {
      navigatedType = new TypeUsage(relationshipClass.referenceNestedType("NavigatedRhsType"), TypeUsage.ConstReference);
      if ( direction == RelationshipDirection.DerivedToBase )
      {
        navigatedType = new TypeUsage(relationshipClass.referenceNestedType("NavigatedLhsType"), TypeUsage.ConstReference);
      }
    }
    else
    {
      // When navigatiing from the Lhs or Rhs object to its associative object,
      // the navigated
      // type will be the opposite of that expected. Therefore when going from
      // Lhs-Assoc, the navigated
      // type will be determined by the multipicity of the Rhs relationship.
      navigatedType = new TypeUsage(relationshipClass.referenceNestedType("NavigatedRhsType"), TypeUsage.ConstReference);
      if ( direction == RelationshipDirection.RightToAssoc )
      {
        navigatedType = new TypeUsage(relationshipClass.referenceNestedType("NavigatedLhsType"), TypeUsage.ConstReference);
      }
    }
    return navigatedType;
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
