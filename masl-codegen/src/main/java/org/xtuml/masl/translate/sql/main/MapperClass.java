/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.translate.sql.main;

import org.xtuml.masl.cppgen.Class;
import org.xtuml.masl.cppgen.Expression;
import org.xtuml.masl.cppgen.*;
import org.xtuml.masl.metamodel.expression.BinaryExpression;
import org.xtuml.masl.metamodel.expression.BinaryExpression.Operator;
import org.xtuml.masl.metamodel.expression.UnaryExpression;
import org.xtuml.masl.metamodel.expression.*;
import org.xtuml.masl.metamodel.object.AttributeDeclaration;
import org.xtuml.masl.metamodel.object.ObjectDeclaration;
import org.xtuml.masl.metamodel.relationship.RelationshipDeclaration;
import org.xtuml.masl.translate.main.Architecture;
import org.xtuml.masl.translate.main.BigTuple;
import org.xtuml.masl.translate.main.Boost;
import org.xtuml.masl.translate.main.Mangler;
import org.xtuml.masl.translate.main.object.Population;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapperClass implements GeneratedClass {

    static public final String KEY_NAME = "MapperClass";

    private final String className;
    private final Namespace namespace;

    private final ObjectTranslator objectTranslator;
    private final ObjectDeclaration objectDeclaration;

    private Class baseClass;
    private final Class mapperClass;

    private CodeFile bodyFile;
    private CodeFile headerFile;
    private Variable primaryKeyCacheVar;
    private final Variable sqlGenVar = new Variable("sqlGenerator");

    private DeclarationGroup attributes;
    private DeclarationGroup findRoutines;
    private DeclarationGroup instanceCreation;
    private DeclarationGroup constructionDestruction;
    private DeclarationGroup uniqueIdentifers;

    public MapperClass(final ObjectTranslator parent,
                       final ObjectDeclaration declaration,
                       final Namespace topLevelNamespace) {
        objectTranslator = parent;
        objectDeclaration = declaration;

        className = Mangler.mangleName(objectDeclaration) + "Mapper";
        namespace = new Namespace(Mangler.mangleName(objectDeclaration.getDomain()), topLevelNamespace);
        mapperClass =
                new Class(className,
                          namespace,
                          parent.getFrameworkTranslator().getLibrary().createPrivateHeader(objectTranslator.getDatabase().getDatabaseTraits().getName() +
                                                                                           Mangler.mangleFile(
                                                                                                   objectDeclaration) +
                                                                                           "Mapper"));
    }

    @Override
    public Class getCppClass() {
        return mapperClass;
    }

    @Override
    public String getClassName() {
        return KEY_NAME;
    }

    @Override
    public void translateAttributes() {
        initialise();

        for (final AttributeDeclaration attributeDecl : objectDeclaration.getAttributes()) {
            if (attributeDecl.isIdentifier() || !attributeDecl.isReferential()) {
                if (attributeDecl.isUnique()) {
                    final TypeUsage
                            attributeType =
                            getMainDomainTranslator().getTypes().getType(attributeDecl.getType());
                    final Function
                            getMaxFn =
                            mapperClass.createMemberFunction(uniqueIdentifers,
                                                             "get_max_" + attributeDecl.getName(),
                                                             Visibility.PUBLIC);
                    getMaxFn.setReturnType(attributeType);
                    getMaxFn.setConst(true);
                    bodyFile.addFunctionDefinition(getMaxFn);

                    // Create cpp line:
                    // int32_t maxValue(0);
                    final Variable maxValueVar = new Variable(attributeType, "maxValue", new Literal(0));
                    getMaxFn.getCode().appendStatement(maxValueVar.asStatement());

                    // Create cpp line:
                    // sqlGenerator->executeGetMaxColumnValue("antenna_id",maxValue);
                    final Function executeGetMaxColumnValueFn = new Function("executeGetMaxColumnValue");
                    final Expression
                            getMaxFnCall =
                            executeGetMaxColumnValueFn.asFunctionCall(sqlGenVar.asExpression(),
                                                                      true,
                                                                      Literal.createStringLiteral(attributeDecl.getName()),
                                                                      maxValueVar.asExpression());
                    getMaxFn.getCode().appendExpression(getMaxFnCall);

                    // Create cpp line:
                    // return maxValue;
                    getMaxFn.getCode().appendStatement(new ReturnStatement(maxValueVar.asExpression()));
                }
            }
        }
    }

    @Override
    public void translateEvents() {

    }

    @Override
    public void translateRelationships() {

    }

    @Override
    public void translateNavigations() {

    }

    @Override
    public void translateFind() {
        for (final Population.FindFunction func : getMainObjectTranslator().getPopulation().getFindFunctions()) {
            // The find_one/find_all/find_only methods are implemented by the Mappers
            // base class
            // as there is no predicate required. Therefore these can be safely
            // ignored.
            // The identifier find methods are implemnted in the population class
            // using
            // a transient cache bu tthis cache might be ignored and purged if the
            // caching
            // strategy defined by the mapper is not allowed to load all the object
            // instances
            if (func.predicate != null) {
                final Function predicateFn = getMainObjectTranslator().getFindPredicate(func.predicate);
                createFindFn(func, predicateFn);
            }
        }
    }

    private void initialise() {
        baseClass = objectTranslator.getDatabase().getObjectMapperClass();
        baseClass.addTemplateSpecialisation(new TypeUsage(getMainObjectTranslator().getMainClass()));
        baseClass.addTemplateSpecialisation(new TypeUsage(objectTranslator.getClass(ImplementationClass.KEY_NAME)));
        mapperClass.addSuperclass(baseClass, Visibility.PUBLIC);

        bodyFile =
                objectTranslator.getFrameworkTranslator().getLibrary().createBodyFile(objectTranslator.getDatabase().getDatabaseTraits().getName() +
                                                                                      Mangler.mangleFile(
                                                                                              objectDeclaration) +
                                                                                      "Mapper");
        headerFile =
                objectTranslator.getFrameworkTranslator().getLibrary().createPrivateHeader(objectTranslator.getDatabase().getDatabaseTraits().getName() +
                                                                                           Mangler.mangleFile(
                                                                                                   objectDeclaration) +
                                                                                           "Mapper");
        headerFile.addClassDeclaration(mapperClass);

        findRoutines = mapperClass.createDeclarationGroup("Finds");
        instanceCreation = mapperClass.createDeclarationGroup("Instance creation");
        constructionDestruction = mapperClass.createDeclarationGroup("Constructors and Destructors");
        uniqueIdentifers = mapperClass.createDeclarationGroup("Unique Identifiers");
        attributes = mapperClass.createDeclarationGroup("Attributes");

        addAttributes();
        addConstructor();
        addDestructor();
        addCreateMethod();
        addDestroyMethod();
        addDoPostInitMethod();
    }

    private void addAttributes() {
        // The sql implementation needs to keep track of the, primary keys used for
        // a class to make
        // sure the same key is not re-used by objects of this class.

        // Create cpp line:
        // ::boost::unordered_set< maslo_Object_B::PrimaryKeyType >
        // primarykey_cache;
        final Class objectClass = objectTranslator.getClass(ImplementationClass.KEY_NAME);
        final Class primaryKeyType = objectClass.referenceNestedType("PrimaryKeyType");
        final Class primaryKeyCacheType = Boost.unordered_set(new TypeUsage(primaryKeyType));
        primaryKeyCacheVar =
                mapperClass.createMemberVariable(attributes,
                                                 "primarykey_cache",
                                                 new TypeUsage(primaryKeyCacheType),
                                                 Visibility.PRIVATE);

    }

    private Function addConstructor() {
        final Function constructor = mapperClass.createConstructor(constructionDestruction, Visibility.PUBLIC);
        bodyFile.addFunctionDefinition(constructor);
        final Class sqlGenClass = objectTranslator.getClass(MapperSqlClass.KEY_NAME);
        final Class
                sqlGenPtr =
                Std.shared_ptr(new TypeUsage(objectTranslator.getDatabase().getObjectSqlGeneratorClass(
                        getMainObjectTranslator().getMainClass(),
                        objectTranslator.getClass("ImplementationClass"))));
        final FunctionCall
                sqlGenFunCall =
                sqlGenPtr.callConstructor(new NewExpression(new TypeUsage(sqlGenClass), new ArrayList<>()));
        final List<Expression> constructorArgs = Arrays.asList(new Expression[]{sqlGenFunCall});
        constructor.setSuperclassArgs(baseClass, constructorArgs);
        return constructor;
    }

    private Function addDestructor() {
        final Function destructor = mapperClass.createDestructor(constructionDestruction, Visibility.PUBLIC);
        destructor.setVirtual(true);
        bodyFile.addFunctionDefinition(destructor);
        return destructor;
    }

    private Function addCreateMethod() {
        final Function
                creator =
                mapperClass.redefineFunction(instanceCreation,
                                             getMainObjectTranslator().getPopulation().getCreateInstance(),
                                             Visibility.PUBLIC);
        bodyFile.addFunctionDefinition(creator);

        final List<Expression> idParamList = new ArrayList<>();
        for (final Variable parameter : creator.getParameters()) {
            final String parameterName = parameter.getName();
            final AttributeDeclaration attribute = findObjectAttribute(parameterName);
            if (attribute != null) {
                if (attribute.isPreferredIdentifier()) {
                    idParamList.add(parameter.asExpression());
                }
            }
        }

        // Create cpp line:
        // if(primarykey_cache.insert( ::boost::unordered_set<
        // maslo_Object_B::PrimaryKeyType >::value_type(masla_bkey)).second ==
        // false){
        // throw ::SWA::ProgramError("Object_B identifier already in use :");
        // }
        final ThrowStatement
                throwStatement =
                new ThrowStatement(Architecture.programError.callConstructor(Literal.createStringLiteral(
                        "identifier already in use")));
        final Expression
                valueTypeConstructCall =
                ((Class) primaryKeyCacheVar.getType().getType()).referenceNestedType("value_type").callConstructor(
                        idParamList);
        final Expression
                insertFnCall =
                new Function("insert").asFunctionCall(primaryKeyCacheVar.asExpression(), false, valueTypeConstructCall);

        final Variable secondVar = new Variable("second");
        final Expression cachePairSecondRef = secondVar.asMemberReference(insertFnCall, false);

        final Expression
                testIdFound =
                new org.xtuml.masl.cppgen.BinaryExpression(cachePairSecondRef,
                                                           org.xtuml.masl.cppgen.BinaryOperator.EQUAL,
                                                           new Literal("false"));
        final IfStatement ifIdAlreadyUsed = new IfStatement(testIdFound, throwStatement);
        creator.getCode().appendStatement(ifIdAlreadyUsed);

        // Create cpp line:
        // ::SWA::IdType uniqueId = getNextArchId();
        final Function getNextArchIdFn = new Function("getNextArchId");
        final Variable
                uniqueIdVar =
                new Variable(new TypeUsage(Architecture.ID_TYPE), "uniqueId", getNextArchIdFn.asFunctionCall());
        creator.getCode().appendStatement(uniqueIdVar.asStatement());

        // Create cpp line:
        // std::shared_ptr<maslo_Find_Test_Object_A> instance(new
        // maslo_Find_Test_Object_A(uniqueId,masla_attribute_1));
        final Class objectClass = objectTranslator.getClass(ImplementationClass.KEY_NAME);
        final Class sharedPtr = Std.shared_ptr(new TypeUsage(objectClass));

        final List<Expression> paramsAsExpr = new ArrayList<>();
        paramsAsExpr.add(uniqueIdVar.asExpression());
        for (final Variable parameter : creator.getParameters()) {
            paramsAsExpr.add(parameter.asExpression());
        }

        final NewExpression newObject = new NewExpression(new TypeUsage(objectClass), paramsAsExpr);
        final Variable instanceVar = new Variable(new TypeUsage(sharedPtr), "instance", new Expression[]{newObject});
        creator.getCode().appendStatement(instanceVar.asStatement());

        // Create cpp line:
        // unitOfWorkMap.registerInsert(PsObjectPtr(instance.get()));
        final Function registerInsert = new Function("registerInsert");
        final Variable unitOfWorkMapVar = new Variable("unitOfWorkMap");
        final Function getFn = new Function("get");
        final Class objectPtr = new Class("PsObjectPtr");
        final Expression instanceGetExpr = getFn.asFunctionCall(instanceVar.asExpression(), false);
        final FunctionCall objectPtrConstructor = objectPtr.callConstructor(instanceGetExpr);
        creator.getCode().appendStatement(new ExpressionStatement(registerInsert.asFunctionCall(unitOfWorkMapVar.asExpression(),
                                                                                                false,
                                                                                                objectPtrConstructor)));

        // Create cpp line:
        // cache.insert(std::make_pair(uniqueId,instance));
        final FunctionCall makePairFnCall = Std.make_pair(uniqueIdVar, instanceVar);
        final Function cacheInsert = new Function("insert");
        final Variable cacheVar = new Variable("cache");
        final Expression cacheInsertFnCall = cacheInsert.asFunctionCall(cacheVar.asExpression(), false, makePairFnCall);
        creator.getCode().appendStatement(new ExpressionStatement(cacheInsertFnCall));

        // Create cpp line:
        // flushCache();
        creator.getCode().appendExpression(new Function("flushCache").asFunctionCall());

        // Create cpp line:
        // return PsObjectPtr(instance.get());;
        creator.getCode().appendStatement(new ReturnStatement(objectPtrConstructor));
        return creator;
    }

    private void addDestroyMethod() {
        final Function
                deleterFn =
                mapperClass.redefineFunction(instanceCreation,
                                             getMainObjectTranslator().getPopulation().getDeleteInstance(),
                                             Visibility.PUBLIC);
        bodyFile.addFunctionDefinition(deleterFn);

        // Create cpp line:
        // ::SQL::ObjectMapper<
        // ::masld_TEST::maslo_Object_A,maslo_Object_A>::deleteInstance(instance);
        final Expression
                deleteInstFnCall =
                baseClass.callStaticFunction("deleteInstance", deleterFn.getParameters().get(0).asExpression());
        deleterFn.getCode().appendExpression(deleteInstFnCall);

        // Create cpp line:
        // primarykey_cache.erase(instance.downcast<maslo_Object_A>()->getPrimaryKey());
        final Function downcastFn = new Function("downcast");
        downcastFn.addTemplateSpecialisation(new TypeUsage(objectTranslator.getClass(ImplementationClass.KEY_NAME)));
        final Expression
                downcastFnCall =
                downcastFn.asFunctionCall(deleterFn.getParameters().get(0).asExpression(), false);
        final Expression getPrimaryKeyFnCall = new Function("getPrimaryKey").asFunctionCall(downcastFnCall, true);
        final Expression
                eraseFnCall =
                new Function("erase").asFunctionCall(primaryKeyCacheVar.asExpression(), false, getPrimaryKeyFnCall);
        deleterFn.getCode().appendExpression(eraseFnCall);

    }

    private void addDoPostInitMethod() {
        final Function
                postInitFn =
                mapperClass.createMemberFunction(instanceCreation, "doPostInit", Visibility.PROTECTED);
        postInitFn.setVirtual(true);
        postInitFn.setReturnType(new TypeUsage(FundamentalType.BOOL));
        bodyFile.addFunctionDefinition(postInitFn);

        // Create Cpp lines:
        // if (allLoaded == false){ loadAll(); }
        final org.xtuml.masl.cppgen.Expression
                allLoadedTest =
                new org.xtuml.masl.cppgen.BinaryExpression(new Variable("allLoaded").asExpression(),
                                                           org.xtuml.masl.cppgen.BinaryOperator.EQUAL,
                                                           Literal.FALSE);
        final Expression loadAllFnCall = new Function("loadAll").asFunctionCall();
        final StatementGroup loadStatements = new StatementGroup();
        loadStatements.appendStatement(loadAllFnCall.asStatement());
        final IfStatement allLoadedif = new IfStatement(allLoadedTest, loadStatements);
        postInitFn.getCode().appendStatement(allLoadedif);

        // Create Cpp lines:
        // for(PsCachedPtrMap::iterator objItr = cache.begin(); objItr !=
        // cache.end(); ++objItr){
        // primarykey_cache.insert( objItr->second->getPrimaryKey());
        // }
        final Variable cacheVar = new Variable("cache");
        final Class mapIterator = Database.psCachedPtrMapClass.referenceNestedType("iterator");
        final Variable
                objItrVar =
                new Variable(new TypeUsage(mapIterator),
                             "objItr",
                             new Function("begin").asFunctionCall(cacheVar.asExpression(), false));
        final org.xtuml.masl.cppgen.Expression
                forTest =
                new org.xtuml.masl.cppgen.BinaryExpression(objItrVar.asExpression(),
                                                           org.xtuml.masl.cppgen.BinaryOperator.NOT_EQUAL,
                                                           new Function("end").asFunctionCall(cacheVar.asExpression(),
                                                                                              false));
        final org.xtuml.masl.cppgen.Expression
                incrementExpr =
                new org.xtuml.masl.cppgen.UnaryExpression(org.xtuml.masl.cppgen.UnaryOperator.PREINCREMENT,
                                                          objItrVar.asExpression());
        final Expression
                getPrimaryKeyFnCall =
                new Function("getPrimaryKey").asFunctionCall(new Variable("second").asMemberReference(objItrVar.asExpression(),
                                                                                                      true), true);
        final Expression
                insertFnCall =
                new Function("insert").asFunctionCall(primaryKeyCacheVar.asExpression(), false, getPrimaryKeyFnCall);
        final StatementGroup forStatements = new StatementGroup();
        forStatements.appendStatement(insertFnCall.asStatement());
        final ForStatement
                cacheItrFor =
                new ForStatement(new VariableDefinitionStatement(objItrVar), forTest, incrementExpr, forStatements);
        postInitFn.getCode().appendStatement(cacheItrFor);

        // Create Cpp lines:
        // SQL::ResourceMonitorContext context;
        // compact(context);
        // return true;
        final Variable contextVar = new Variable(new TypeUsage(Database.resourceMonitorContextClass), "context");
        final Expression compactFnCall = new Function("compact").asFunctionCall(contextVar.asExpression());
        postInitFn.getCode().appendStatement(contextVar.asStatement());
        postInitFn.getCode().appendExpression(compactFnCall);
        postInitFn.getCode().appendStatement(new ReturnStatement(Literal.TRUE));
    }

    private void createFindFn(final Population.FindFunction func, final Function predicateFn) {
        // Each of the generated base class find functions has an associated
        // predicate function that can be used by the C++ STL find algorithms.
        // Therefore when calling the mapper select method need to pass in the
        // sqlCritiera instance and the find predicate. In this way the mapper
        // class can look at its current set of policies to determine whether to
        // undertake a database lookup or a transient lookup using the cached object
        // set and the predicate function.
        final Function findFn = mapperClass.redefineFunction(findRoutines, func.function, Visibility.PUBLIC);
        findFn.setConst(false);
        findFn.setComment("MASL identifier find: " + func.predicate.toString());
        bodyFile.addFunctionDefinition(findFn);

        final Class PsObjPtrClass = Database.psBaseObjectPtrClass;
        Variable selectedObjectVar = new Variable(new TypeUsage(PsObjPtrClass), "selectedObject");
        Function selectFn = new Function("selectOne");
        if (func.type == FindExpression.Type.FIND) {
            final Class PsObjPtrSetClass = Database.psBaseObjectPtrSwaSetClass;
            selectedObjectVar = new Variable(new TypeUsage(PsObjPtrSetClass), "selectedObjectSet");
            selectFn = new Function("selectAll");
        }
        findFn.getCode().appendStatement(selectedObjectVar.asStatement());

        final Expression inMemoryCondition = new Function("allowInMemoryFind").asFunctionCall();
        final CodeBlock memoryOperation = new CodeBlock();
        final CodeBlock sqlOperation = new CodeBlock();
        final IfStatement inMemoryIf = new IfStatement(inMemoryCondition, memoryOperation, sqlOperation);
        findFn.getCode().appendStatement(inMemoryIf);

        // Create cpp line:
        // ::std::function<bool (maslo_Find_Test_Object_A*) > predicate =
        // ::boost::bind(
        // &::masld_PERF::maslo_Find_Test_Object_A::findPredicate_OPOPOPmasl_attribute_1_maslEQp1CPOROPmasl_attribute_1_maslEQp2CPCPOROPmasl_attribute_1_maslEQp3CPCP,_1,p1,p2,p3);
        // selectOne(predicate, selectedObject);
        final Expression predicateValue = createFindFnPredicateValue(findFn, predicateFn);
        final Class predicateClass = createFindFnPredicateClass();
        final Variable predicateVar = new Variable(new TypeUsage(predicateClass), "predicate", predicateValue);
        memoryOperation.appendStatement(predicateVar.asStatement());
        final Expression
                inMemorySelectfnCall =
                selectFn.asFunctionCall(predicateVar.asExpression(), selectedObjectVar.asExpression());
        memoryOperation.appendExpression(inMemorySelectfnCall);

        // Add the code for the SQl select construction and select call.
        if (containsStructure(func.predicate)) {
            // For Objects that contain attributes with complex types. Any finds
            // undertaken that require access
            // to any fields of the complex data cannot be undertaken by an SQL query
            // on the database, as complex
            // types are stored as ASN.1 encoded binary blobs. Therefore the only way
            // the find can be satisfied is
            // to load all the data into transient memory and undertake a linear
            // search. This might go against the
            // caching stratergy employed by the object.
            final Variable
                    predicateDebug =
                    new Variable(new TypeUsage(Std.string).getConstType(),
                                 "predicatDebug",
                                 Literal.createStringLiteral(func.predicate.toString()));
            sqlOperation.appendStatement(predicateDebug.asStatement());

            Function selectFromCacheFn = new Function("selectOneFromCache");
            if (func.type == FindExpression.Type.FIND) {
                selectFromCacheFn = new Function("selectAllFromCache");
            }
            final Variable cachePredicateVar = new Variable(new TypeUsage(predicateClass), "predicate", predicateValue);
            sqlOperation.appendStatement(cachePredicateVar.asStatement());
            final Expression
                    cacheSelectFnCall =
                    selectFromCacheFn.asFunctionCall(predicateDebug.asExpression(),
                                                     cachePredicateVar.asExpression(),
                                                     selectedObjectVar.asExpression());
            sqlOperation.appendExpression(cacheSelectFnCall);
        } else {
            final DatabaseTraits.SqlCritera criteria = createCriteria("sqlCriteria", func);
            criteria.appendStatements(sqlOperation);

            // The SQL select statement that has been produced might be dependent on
            // other tables. This can happen if a referential attribute is being
            // referenced, as the destination object and associated relationship
            // tables
            // will be accessed as part of the where clause in the query. Before the
            // query can be executed the dependent tables need to be flushed so that
            // the query can act upon the correct data set.
            sqlOperation.appendStatement(new BlankLine(0));
            for (final ObjectDeclaration dependentObj : criteria.getDependentObjects()) {
                final ObjectTranslator
                        dependentObjTrans =
                        objectTranslator.getFrameworkTranslator().getObjectTranslator(dependentObj);
                if (dependentObjTrans != objectTranslator) {
                    final Class dependentPopulationClass = dependentObjTrans.getClass(PopulationClass.KEY_NAME);
                    final Expression getPopulationFnCall = dependentPopulationClass.callStaticFunction("getPopulation");
                    final Expression
                            forceFlushFnCall =
                            new Function("forceFlush").asFunctionCall(getPopulationFnCall, false);
                    sqlOperation.appendExpression(forceFlushFnCall);
                }
            }

            for (final RelationshipDeclaration dependentRel : criteria.getDependentRelationship()) {
                final RelationshipTranslator
                        dependentRelTranslator =
                        objectTranslator.getFrameworkTranslator().getRelationshipTranslator(dependentRel);
                final Class dependentRelMapperClass = dependentRelTranslator.getRelationshipMapperClass();
                final Expression getInstanceFnCall = dependentRelMapperClass.callStaticFunction("singleton");
                final Expression forceFlushFnCall = new Function("forceFlush").asFunctionCall(getInstanceFnCall, false);
                sqlOperation.appendExpression(forceFlushFnCall);
            }

            sqlOperation.appendStatement(new BlankLine(0));
            final Expression
                    inSqlSelectfnCall =
                    selectFn.asFunctionCall(criteria.getVariable().asExpression(), selectedObjectVar.asExpression());
            sqlOperation.appendExpression(inSqlSelectfnCall);
        }
        // Return the selected object(s)
        findFn.getCode().appendStatement(new ReturnStatement(selectedObjectVar.asExpression()));
    }

    private DatabaseTraits.SqlCritera createCriteria(final String variableName, final Population.FindFunction findFn) {
        // Create the critera sql selector.
        final DatabaseTraits.SqlCritera
                sqlSelector =
                objectTranslator.getDatabase().getDatabaseTraits().createSqlCriteria(objectDeclaration, variableName);
        final org.xtuml.masl.metamodel.expression.Expression predicate = findFn.predicate;

        if (!containsStructure(predicate)) {
            addCritera(sqlSelector, predicate);
        }
        return sqlSelector;
    }

    private boolean containsStructure(final org.xtuml.masl.metamodel.expression.Expression predicate) {
        boolean foundStructure = false;
        if (predicate instanceof BinaryExpression binExpression) {
            final org.xtuml.masl.metamodel.expression.Expression lhs = binExpression.getLhs();
            final org.xtuml.masl.metamodel.expression.Expression rhs = binExpression.getRhs();
            foundStructure = containsStructure(lhs);
            if (!foundStructure) {
                foundStructure = containsStructure(rhs);
            }
        } else if (predicate instanceof SelectedComponentExpression) {
            foundStructure = true;
        }
        return foundStructure;
    }

    private void addCritera(final DatabaseTraits.SqlCritera criteria,
                            final org.xtuml.masl.metamodel.expression.Expression currentExpression) {
        if (currentExpression instanceof BinaryExpression binExpression) {
            final org.xtuml.masl.metamodel.expression.Expression lhs = binExpression.getLhs();
            final org.xtuml.masl.metamodel.expression.Expression rhs = binExpression.getRhs();
            final Operator operator = binExpression.getOperator();
            criteria.beginCondition();
            addCritera(criteria, lhs);

            criteria.addOperator(operator);
            addCritera(criteria, rhs);
            criteria.endCondition();
        } else if (currentExpression instanceof UnaryExpression unaryExpression) {
            final UnaryExpression.Operator unaryOperator = unaryExpression.getOperator();
            if (unaryExpression.getRhs() instanceof BinaryExpression binaryExpr) {
                criteria.beginCondition();
                criteria.addOperator(unaryOperator);
                addCritera(criteria, binaryExpr);
                criteria.endCondition();
            } else {
                throw new RuntimeException(" addCritera SQL query cannot be formed for unaryExpression : " +
                                           unaryExpression);
            }
        } else if (currentExpression instanceof FindParameterExpression findParam) {
            criteria.addParameterOperand(findParam.getType(), findParam.getName());
        } else if (currentExpression instanceof FindAttributeNameExpression findAtt) {
            final AttributeDeclaration declaration = findAtt.getAttribute();
            if (declaration.isReferential()) {
                criteria.addRefAttributeNameOperand(declaration);
            } else {
                criteria.addAttributeNameOperand(declaration);
            }
        } else if (currentExpression instanceof SelectedComponentExpression) {
            // If the find attributes access an element of a structure type
            // then an SQL query cannot be formed as the structure type is stored
            // as a flat binary type.
            throw new RuntimeException(" addCritera SQL query cannot be formed for structure types : " +
                                       currentExpression);
        }
    }

    private Class createFindFnPredicateClass() {
        final List<TypeUsage> functionPtrParameters = new ArrayList<>();
        functionPtrParameters.add(new TypeUsage(objectTranslator.getClass(ImplementationClass.KEY_NAME),
                                                TypeUsage.Pointer));
        final Class predicateClass = Std.function(new TypeUsage(FundamentalType.BOOL), functionPtrParameters);
        return predicateClass;
    }

    private Expression createFindFnPredicateValue(final Function findFn, final Function predicateFn) {
        // Create cpp line:
        // ::std::function<bool (maslo_Find_Test_Object_A*) > predicate =
        // ::boost::bind(
        // &::masld_PERF::maslo_Find_Test_Object_A::findPredicate_OPOPOPmasl_attribute_1_maslEQp1CPOROPmasl_attribute_1_maslEQp2CPCPOROPmasl_attribute_1_maslEQp3CPCP,_1,p1,p2,p3);

        final List<Expression> bindArgs = new ArrayList<>();
        bindArgs.add(predicateFn.asFunctionPointer());
        bindArgs.add(Boost.bind_1);

        final List<Expression> findArgs = new ArrayList<>(findFn.getParameters().size());
        for (final Variable param : findFn.getParameters()) {
            findArgs.add(param.asExpression());
        }

        // If too many parameters for boost bind to cope with (not forgetting
        // the bound object), then wrap in a tuple. Note that the predicate
        // function should already be done!
        if (findArgs.size() + 1 > Boost.MAX_BIND_PARAMS) {
            bindArgs.add(BigTuple.getMakeTuple(findArgs));
        } else {
            bindArgs.addAll(findArgs);
        }
        final Expression predicateValue = Boost.bind.asFunctionCall(bindArgs);
        return predicateValue;
    }

    private AttributeDeclaration findObjectAttribute(final String parameterName) {
        AttributeDeclaration matchingAttribute = null;
        for (final AttributeDeclaration attribute : objectDeclaration.getAttributes()) {
            if (Mangler.mangleName(attribute).equals(parameterName)) {
                matchingAttribute = attribute;
                break;
            }
        }
        return matchingAttribute;
    }

    private org.xtuml.masl.translate.main.object.ObjectTranslator getMainObjectTranslator() {
        return objectTranslator.getMainObjectTranslator(objectDeclaration);
    }

    private org.xtuml.masl.translate.main.DomainTranslator getMainDomainTranslator() {
        return objectTranslator.getFrameworkTranslator().getMainDomainTranslator();
    }

}
