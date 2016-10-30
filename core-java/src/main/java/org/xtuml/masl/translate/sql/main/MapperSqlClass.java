/*
 * Filename : MapperSqlClass.java
 *
 * UK Crown Copyright (c) 2008. All Rights Reserved
 */
package org.xtuml.masl.translate.sql.main;

import java.util.Arrays;
import java.util.List;

import org.xtuml.masl.cppgen.ArrayAccess;
import org.xtuml.masl.cppgen.BinaryExpression;
import org.xtuml.masl.cppgen.BinaryOperator;
import org.xtuml.masl.cppgen.Class;
import org.xtuml.masl.cppgen.CodeBlock;
import org.xtuml.masl.cppgen.CodeFile;
import org.xtuml.masl.cppgen.DeclarationGroup;
import org.xtuml.masl.cppgen.Expression;
import org.xtuml.masl.cppgen.Function;
import org.xtuml.masl.cppgen.FunctionCall;
import org.xtuml.masl.cppgen.FundamentalType;
import org.xtuml.masl.cppgen.IfStatement;
import org.xtuml.masl.cppgen.Literal;
import org.xtuml.masl.cppgen.Namespace;
import org.xtuml.masl.cppgen.ReturnStatement;
import org.xtuml.masl.cppgen.Std;
import org.xtuml.masl.cppgen.TypeUsage;
import org.xtuml.masl.cppgen.Variable;
import org.xtuml.masl.cppgen.Visibility;
import org.xtuml.masl.metamodel.object.AttributeDeclaration;
import org.xtuml.masl.metamodel.object.ObjectDeclaration;
import org.xtuml.masl.metamodel.type.EnumerateType;
import org.xtuml.masl.translate.main.ASN1;
import org.xtuml.masl.translate.main.Architecture;
import org.xtuml.masl.translate.main.BigTuple;
import org.xtuml.masl.translate.main.Boost;
import org.xtuml.masl.translate.main.Mangler;


public class MapperSqlClass
    implements GeneratedClass
{

  static public final String            KEY_NAME                = "MapperSqlClass";

  private final String                  className;
  private final Namespace               namespace;

  private final Class                   mapperSqlClass;
  private Class                         baseClass;

  private final ObjectTranslator        objectTranslator;
  private final ObjectDeclaration       objectDeclaration;

  private CodeFile                      bodyFile;
  private CodeFile                      headerFile;

  private final ObjectToTableTranslator tableTranslator;

  private DeclarationGroup              executeMethods;
  private DeclarationGroup              getMethods;
  private DeclarationGroup              constructionDestruction;
  private DeclarationGroup              dataMembers;

  Variable                              tableNameVar;
  Variable                              objectNameVar;

  PreparedStatement                     insertPreparedStatement;
  PreparedStatement                     updatePreparedStatement;
  PreparedStatement                     deletePreparedStatement;

  Variable                              insertStatement;
  Variable                              updateStatement;
  Variable                              deleteStatement;

  Class                                 mapperType              = Std.map(new TypeUsage(Std.string), new TypeUsage(Std.string));
  TypeUsage                             mapperIteratorType      = new TypeUsage(mapperType.referenceNestedType("iterator"));
  TypeUsage                             mapperConstIteratorType = new TypeUsage(mapperType.referenceNestedType("const_iterator"));

  Variable                              columnNameMapper;

  Function                              constructor;

  DatabaseTraits                        databaseTraits;

  public MapperSqlClass ( final ObjectTranslator parent, final ObjectDeclaration declaration, final Namespace topLevelNamespace )
  {
    objectTranslator = parent;
    objectDeclaration = declaration;
    tableTranslator = objectTranslator.getDatabase().getDatabaseTraits().createObjectToTableTranslator(objectTranslator,
                                                                                                       objectDeclaration);
    databaseTraits = parent.getDatabase().getDatabaseTraits();

    className = Mangler.mangleName(objectDeclaration) + "SqlGenerator";
    namespace = new Namespace(Mangler.mangleName(objectDeclaration.getDomain()), topLevelNamespace);
    mapperSqlClass = new Class(className,
                               namespace,
                               parent.getFrameworkTranslator().getLibrary().createPrivateHeader("Sqlite" + Mangler.mangleFile(objectDeclaration)
                                                         + "MapperSql"));
  }

  @Override
  public Class getCppClass ()
  {
    return mapperSqlClass;
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
  }

  @Override
  public void translateFind ()
  {

  }

  @Override
  public void translateEvents ()
  {

  }

  @Override
  public void translateRelationships ()
  {

  }

  @Override
  public void translateNavigations ()
  {

  }

  private void initialise ()
  {
    baseClass = objectTranslator.getDatabase().getObjectSqlGeneratorClass(getMainObjectTranslator().getMainClass(),
                                                                          objectTranslator.getClass(ImplementationClass.KEY_NAME));
    mapperSqlClass.addSuperclass(baseClass, Visibility.PUBLIC);

    bodyFile = objectTranslator.getFrameworkTranslator().getLibrary().createBodyFile(databaseTraits.getName() + Mangler.mangleFile(objectDeclaration) + "MapperSql");
    headerFile = objectTranslator.getFrameworkTranslator().getLibrary().createPrivateHeader(databaseTraits.getName() + Mangler.mangleFile(objectDeclaration) + "MapperSql");
    headerFile.addClassDeclaration(mapperSqlClass);

    insertPreparedStatement = tableTranslator.createPreparedStatement(PreparedStatement.PreparedStatementType.INSERT);
    updatePreparedStatement = tableTranslator.createPreparedStatement(PreparedStatement.PreparedStatementType.UPDATE);
    deletePreparedStatement = tableTranslator.createPreparedStatement(PreparedStatement.PreparedStatementType.DELETE);

    addDatabaseTable();

    executeMethods = mapperSqlClass.createDeclarationGroup("execute methods");
    getMethods = mapperSqlClass.createDeclarationGroup("getter methods");
    constructionDestruction = mapperSqlClass.createDeclarationGroup("Constructors and Destructors");
    dataMembers = mapperSqlClass.createDeclarationGroup("Data Members");

    addDataMembers();
    addConstructor();
    addDestructor();
    addInitialise();
    addGetterMemberFns();
    addExecuteMemberFns();
  }

  private void addDatabaseTable ()
  {
    final Namespace databaseAnon = new Namespace("");

    // Create cpp line:
    // const std::string createTableStatment()
    final Function createTableFn = new Function("createTableStatment", databaseAnon);
    createTableFn.setReturnType(new TypeUsage(Std.string, TypeUsage.Const));
    bodyFile.addFunctionDefinition(createTableFn);
    createTableFn.getCode()
                 .appendStatement(new ReturnStatement(Literal.createStringLiteral(tableTranslator.getCreateTableStatement())));

    // Create cpp line:
    // bool registerSchema =
    // Schema::singleton().registerTable(tableNameText,createTableStatment());
    final Class schemaClass = Database.schemaClass;
    final FunctionCall singletonFn = schemaClass.callStaticFunction("singleton");
    final Function registerTableFn = new Function("registerTable");
    final Expression registerTableExpr = registerTableFn.asFunctionCall(singletonFn,
                                                                        false,
                                                                        Literal.createStringLiteral(tableTranslator.getTableName()),
                                                                        createTableFn.asFunctionCall());

    final Variable registerSchemaVar = new Variable(new TypeUsage(FundamentalType.BOOL),
                                                    "registerSchema",
                                                    databaseAnon,
                                                    registerTableExpr);
    bodyFile.addVariableDefinition(registerSchemaVar);
  }

  private void addDataMembers ()
  {
    tableNameVar = baseClass.createMemberVariable(dataMembers, "tableName", new TypeUsage(Std.string), Visibility.PRIVATE);
    objectNameVar = baseClass.createMemberVariable(dataMembers, "objectName", new TypeUsage(Std.string), Visibility.PRIVATE);
    insertStatement = baseClass.createMemberVariable(dataMembers,
                                                     "insertStatement",
                                                     new TypeUsage(insertPreparedStatement.getClassType()),
                                                     Visibility.PRIVATE);
    updateStatement = baseClass.createMemberVariable(dataMembers,
                                                     "updateStatement",
                                                     new TypeUsage(updatePreparedStatement.getClassType()),
                                                     Visibility.PRIVATE);
    deleteStatement = baseClass.createMemberVariable(dataMembers,
                                                     "deleteStatement",
                                                     new TypeUsage(deletePreparedStatement.getClassType()),
                                                     Visibility.PRIVATE);
    columnNameMapper = baseClass.createMemberVariable(dataMembers,
                                                      "columnNameMapper",
                                                      new TypeUsage(mapperType),
                                                      Visibility.PRIVATE);
  }

  private void addConstructor ()
  {
    constructor = mapperSqlClass.createConstructor(constructionDestruction, Visibility.PUBLIC);
    bodyFile.addFunctionDefinition(constructor);
    constructor.setInitialValue(tableNameVar, Literal.createStringLiteral(tableTranslator.getTableName()));
    constructor.setInitialValue(objectNameVar, Literal.createStringLiteral(objectDeclaration.getName()));
    constructor.setInitialValue(insertStatement, Literal.createStringLiteral(insertPreparedStatement.getStatement()));
    constructor.setInitialValue(updateStatement, Literal.createStringLiteral(updatePreparedStatement.getStatement()));
    constructor.setInitialValue(deleteStatement, Literal.createStringLiteral(deletePreparedStatement.getStatement()));
  }

  private void addDestructor ()
  {
    final Function destructor = mapperSqlClass.createDestructor(constructionDestruction, Visibility.PUBLIC);
    destructor.setPure(false);
    bodyFile.addFunctionDefinition(destructor);
  }

  private void addInitialise ()
  {
    final Function initialiseFn = mapperSqlClass.createMemberFunction(constructionDestruction, "initialise", Visibility.PUBLIC);
    bodyFile.addFunctionDefinition(initialiseFn);

    for ( int x = 0; x < tableTranslator.getColumnNameList().size(); ++x )
    {
      // Create cpp line:
      // columnNameMapper[attributeNameList[0]] =
      // std::string(columnNameList[0]);
      final ArrayAccess accessor = new ArrayAccess(columnNameMapper.asExpression(),
                                                   Literal.createStringLiteral(tableTranslator.getAttributeNameList().get(x)));
      final BinaryExpression assignmentExpr = new BinaryExpression(accessor,
                                                                   BinaryOperator.ASSIGN,
                                                                   Std.string.callConstructor(Literal.createStringLiteral(tableTranslator.getColumnNameList()
                                                                                                                                         .get(x))));
      initialiseFn.getCode().appendExpression(assignmentExpr);
    }

    initialiseFn.getCode().appendExpression(insertPreparedStatement.prepare(insertStatement.asExpression()));
    initialiseFn.getCode().appendExpression(updatePreparedStatement.prepare(updateStatement.asExpression()));
    initialiseFn.getCode().appendExpression(deletePreparedStatement.prepare(deleteStatement.asExpression()));
  }

  private void addGetterMemberFns ()
  {
    // getDomainName
    final Function getDomainNameFn = mapperSqlClass.createMemberFunction(getMethods, "getDomainName", Visibility.PUBLIC);
    getDomainNameFn.setConst(true);
    getDomainNameFn.setReturnType(new TypeUsage(Std.string).getConstType());
    getDomainNameFn.getCode().appendStatement(new ReturnStatement(Literal.createStringLiteral(objectDeclaration.getDomain()
                                                                                                               .getName())));
    bodyFile.addFunctionDefinition(getDomainNameFn);

    // getTableName
    final Function getTableNameFn = mapperSqlClass.createMemberFunction(getMethods, "getTableName", Visibility.PUBLIC);
    getTableNameFn.setConst(true);
    getTableNameFn.setReturnType(new TypeUsage(Std.string, TypeUsage.ConstReference));
    getTableNameFn.getCode().appendStatement(new ReturnStatement(tableNameVar.asExpression()));
    bodyFile.addFunctionDefinition(getTableNameFn);

    // getObjectName
    final Function getObjectNameFn = mapperSqlClass.createMemberFunction(getMethods, "getObjectName", Visibility.PUBLIC);
    getObjectNameFn.setConst(true);
    getObjectNameFn.setReturnType(new TypeUsage(Std.string, TypeUsage.ConstReference));
    getObjectNameFn.getCode().appendStatement(new ReturnStatement(objectNameVar.asExpression()));
    bodyFile.addFunctionDefinition(getObjectNameFn);

    // getColumnName
    final Function getColumnNameFn = mapperSqlClass.createMemberFunction(getMethods, "getColumnName", Visibility.PUBLIC);
    getColumnNameFn.setConst(true);
    final Variable attributeVar = getColumnNameFn.createParameter(new TypeUsage(Std.string, TypeUsage.ConstReference), "attribute");
    getColumnNameFn.setReturnType(new TypeUsage(Std.string, TypeUsage.Const));
    bodyFile.addFunctionDefinition(getColumnNameFn);

    final Expression findFnCall = new Function("find").asFunctionCall(columnNameMapper.asExpression(),
                                                                      false,
                                                                      attributeVar.asExpression());
    final Variable requiredNameItrVar = new Variable(mapperConstIteratorType, "requiredNameItr", findFnCall);
    getColumnNameFn.getCode().appendStatement(requiredNameItrVar.asStatement());

    final Expression findLhs = requiredNameItrVar.asExpression();
    final Expression findRhs = new Function("end").asFunctionCall(columnNameMapper.asExpression(), false);
    final BinaryExpression findCondition = new BinaryExpression(findLhs, BinaryOperator.EQUAL, findRhs);
    final CodeBlock findFailureBranch = new CodeBlock();
    findFailureBranch.appendStatement(objectTranslator.getDatabase()
                                                      .getDatabaseTraits()
                                                      .throwDatabaseException(mapperSqlClass.getName() + "::getColumnName - failed to find attribute name "));
    final IfStatement ifFind = new IfStatement(findCondition, findFailureBranch);
    getColumnNameFn.getCode().appendStatement(ifFind);
    getColumnNameFn.getCode()
                   .appendStatement(new ReturnStatement(new Variable("second").asMemberReference(requiredNameItrVar.asExpression(),
                                                                                                 true)));

  }

  private void addExecuteMemberFns ()
  {
    final Function executeGetMaxColumnValue32Fn = mapperSqlClass.createMemberFunction(executeMethods,
                                                                                      "executeGetMaxColumnValue",
                                                                                      Visibility.PUBLIC);
    executeGetMaxColumnValue32Fn.setConst(true);
    final Variable attributeName32Var = executeGetMaxColumnValue32Fn.createParameter(new TypeUsage(Std.string).getConstReferenceType(),
                                                                                     "attribute");
    executeGetMaxColumnValue32Fn.createParameter(new TypeUsage(Std.int32).getReferenceType(), "value");
    tableTranslator.addGetMaxFnBody(mapperSqlClass.getName(), attributeName32Var, executeGetMaxColumnValue32Fn);
    bodyFile.addFunctionDefinition(executeGetMaxColumnValue32Fn);

    final Function executeGetMaxColumnValue64Fn = mapperSqlClass.createMemberFunction(executeMethods,
                                                                                      "executeGetMaxColumnValue",
                                                                                      Visibility.PUBLIC);
    executeGetMaxColumnValue64Fn.setConst(true);
    final Variable attributeName64Var = executeGetMaxColumnValue64Fn.createParameter(new TypeUsage(Std.string).getConstReferenceType(),
                                                                                     "attribute");
    executeGetMaxColumnValue64Fn.createParameter(new TypeUsage(Std.int64).getReferenceType(), "value");
    tableTranslator.addGetMaxFnBody(mapperSqlClass.getName(), attributeName64Var, executeGetMaxColumnValue64Fn);
    bodyFile.addFunctionDefinition(executeGetMaxColumnValue64Fn);

    final Function executeGetRowCountFn = mapperSqlClass.createMemberFunction(executeMethods,
                                                                              "executeGetRowCount",
                                                                              Visibility.PUBLIC);
    executeGetRowCountFn.setConst(true);
    executeGetRowCountFn.setReturnType(new TypeUsage(Architecture.ID_TYPE));
    tableTranslator.addGetRowCountFnBody(mapperSqlClass.getName(), executeGetRowCountFn);
    bodyFile.addFunctionDefinition(executeGetRowCountFn);

    final Function executeGetMaxIdentifierFn = mapperSqlClass.createMemberFunction(executeMethods,
                                                                                   "executeGetMaxIdentifier",
                                                                                   Visibility.PUBLIC);
    executeGetMaxIdentifierFn.setConst(true);
    executeGetMaxIdentifierFn.setReturnType(new TypeUsage(Architecture.ID_TYPE));
    tableTranslator.addGetMaxIdFnBody(mapperSqlClass.getName(), executeGetMaxIdentifierFn);
    bodyFile.addFunctionDefinition(executeGetMaxIdentifierFn);

    final Function executeUpdate = mapperSqlClass.createMemberFunction(executeMethods, "executeUpdate", Visibility.PUBLIC);
    executeUpdate.setConst(true);

    final Class nestedCacheType = mapperSqlClass.referenceNestedType("CacheType");
    final Class nestedPsObjectPtrClass = mapperSqlClass.referenceNestedType("PsObjectPtr");
    final Class nestedPsBaseObjectPtrSwaSet = mapperSqlClass.referenceNestedType("PsBaseObjectPtrSwaSet");

    final Variable updateObjectVar = executeUpdate.createParameter(new TypeUsage(nestedPsObjectPtrClass, TypeUsage.ConstReference),
                                                                   "object");
    final List<Expression> updateGetterCalls = BigTuple.getTupleList(Arrays.asList(createObjectGetterFnCalls(executeUpdate.getCode(),
                                                                                                             updateObjectVar)));
    final Expression executeUpdateFnCall = new Function("execute").asFunctionCall(updateStatement.asExpression(),
                                                                                  false,
                                                                                  updateGetterCalls);
    executeUpdate.getCode().appendExpression(executeUpdateFnCall);
    bodyFile.addFunctionDefinition(executeUpdate);

    final Function executeInsert = mapperSqlClass.createMemberFunction(executeMethods, "executeInsert", Visibility.PUBLIC);
    executeInsert.setConst(true);
    final Variable insertObjectVar = executeInsert.createParameter(new TypeUsage(nestedPsObjectPtrClass, TypeUsage.ConstReference),
                                                                   "object");
    final List<Expression> insertGetterCalls = BigTuple.getTupleList(Arrays.asList(createObjectGetterFnCalls(executeInsert.getCode(),
                                                                                                             insertObjectVar)));

    final Expression executeInsertFnCall = insertPreparedStatement.execute(insertStatement.asExpression(), insertGetterCalls);
    executeInsert.getCode().appendExpression(executeInsertFnCall);
    bodyFile.addFunctionDefinition(executeInsert);

    final Function executeRemove = mapperSqlClass.createMemberFunction(executeMethods, "executeRemove", Visibility.PUBLIC);
    executeRemove.setConst(true);
    final Variable removeObjectVar = executeRemove.createParameter(new TypeUsage(nestedPsObjectPtrClass, TypeUsage.ConstReference),
                                                                   "object");
    final Expression archGetterCall = getMainObjectTranslator().getGetId()
                                                               .asFunctionCall(new Function("getChecked").asFunctionCall(removeObjectVar.asExpression(),
                                                                                                                         false),
                                                                               true);

    final Expression executeRemoveFnCall = deletePreparedStatement.execute(deleteStatement.asExpression(),
                                                                           Arrays.asList(archGetterCall));
    executeRemove.getCode().appendExpression(executeRemoveFnCall);
    bodyFile.addFunctionDefinition(executeRemove);

    final Function executeRemoveId = mapperSqlClass.createMemberFunction(executeMethods, "executeRemoveId", Visibility.PUBLIC);
    executeRemoveId.setConst(true);
    final Variable removeIdObjectVar = executeRemoveId.createParameter(new TypeUsage(Architecture.ID_TYPE, TypeUsage.Const),
                                                                       "object");
    final Expression executeRemoveIdFnCall = deletePreparedStatement.execute(deleteStatement.asExpression(),
                                                                             Arrays.asList(removeIdObjectVar.asExpression()));
    executeRemoveId.getCode().appendExpression(executeRemoveIdFnCall);
    bodyFile.addFunctionDefinition(executeRemoveId);

    final Function executeSelect = mapperSqlClass.createMemberFunction(executeMethods, "executeSelect", Visibility.PUBLIC);
    executeSelect.setConst(true);
    final Variable cacheParameter = executeSelect.createParameter(new TypeUsage(nestedCacheType, TypeUsage.Reference), "cache");
    final Variable criteriaParameter = executeSelect.createParameter(new TypeUsage(Database.criteriaClass, TypeUsage.ConstReference),
                                                                     "criteria");
    final Variable resultParameter = executeSelect.createParameter(new TypeUsage(nestedPsBaseObjectPtrSwaSet, TypeUsage.Reference),
                                                                   "result");
    bodyFile.addFunctionDefinition(executeSelect);
    tableTranslator.addExecuteSelectBody(executeSelect, cacheParameter, criteriaParameter, resultParameter);

    final Function executeSelectNoResult = mapperSqlClass.createMemberFunction(executeMethods, "executeSelect", Visibility.PUBLIC);
    executeSelectNoResult.setConst(true);
    final Variable noResultCacheParameter = executeSelectNoResult.createParameter(new TypeUsage(nestedCacheType,
                                                                                                TypeUsage.Reference), "cache");
    final Variable noResultCriteriaParameter = executeSelectNoResult.createParameter(new TypeUsage(Database.criteriaClass,
                                                                                                   TypeUsage.ConstReference),
                                                                                     "criteria");
    bodyFile.addFunctionDefinition(executeSelectNoResult);
    tableTranslator.addExecuteSelectBody(executeSelectNoResult, noResultCacheParameter, noResultCriteriaParameter);
  }

  private Expression[] createObjectGetterFnCalls ( final CodeBlock currentBlock, final Variable objectVar )
  {
    final Expression attributeGetterCalls[] = new Expression[tableTranslator.getColumnNameList().size()];
    final int architectureIdBindIndex = tableTranslator.getArchIdBindIndex();
    final Function getCheckedFn = new Function("getChecked");
    attributeGetterCalls[architectureIdBindIndex - 1] = getMainObjectTranslator().getGetId()
                                                                                 .asFunctionCall(getCheckedFn.asFunctionCall(objectVar.asExpression(),
                                                                                                                             false),
                                                                                                 true);

    for ( final AttributeDeclaration attribute : objectDeclaration.getAttributes() )
    {
      // Referential attributes are not stored in the table unless they are
      // preferred identifiers. So need to skip when they are encountered.
      if ( attribute.isIdentifier() || !attribute.isReferential() )
      {
        final Function attributeGetter = getMainObjectTranslator().getAttributeGetter(attribute);
        final int attributeBindIndex = tableTranslator.getBindIndex(attribute);

        // The enum attribute types are stored in their textual representation.
        // Therefore detect
        // when an attribute is an enum type and invoke the getText method upon
        // it.
        final Expression attributeGetterFnCall = attributeGetter.asFunctionCall(getCheckedFn.asFunctionCall(objectVar.asExpression(),
                                                                                                            false),
                                                                                true);
        if ( attribute.getType().getDefinedType() instanceof EnumerateType )
        {
          attributeGetterCalls[attributeBindIndex - 1] = new Function("getText").asFunctionCall(attributeGetterFnCall, false);
        }
        else
        {
          // Some of the object attributes might be complex, i.e. structures or
          // sequence of structures. Therefore detect when a type has an
          // associated
          // blob column and encode the data using its ASN.1 interface.
          if ( tableTranslator.isBlobColumn(attribute) == false )
          {
            attributeGetterCalls[attributeBindIndex - 1] = attributeGetterFnCall;
          }
          else
          {
            final Variable encoder = new Variable(new TypeUsage(ASN1.DEREncoder),
                                                  "encoder_" + Mangler.mangleName(attribute),
                                                  ASN1.DEREncode.asFunctionCall(attributeGetterFnCall));
            currentBlock.appendStatement(encoder.asStatement());

            final Variable encoded = new Variable(new TypeUsage(databaseTraits.getBlobClass()),
                                                  "encoded_" + Mangler.mangleName(attribute),
                                                  new Function("begin").asFunctionCall(encoder.asExpression(), false),
                                                  new Function("end").asFunctionCall(encoder.asExpression(), false));
            currentBlock.appendStatement(encoded.asStatement());

            attributeGetterCalls[attributeBindIndex - 1] = Boost.ref.asFunctionCall(encoded.asExpression());
          }
        }
      }
    }

    // Add the CurrentState attribute if one is defined.
    if ( objectDeclaration.hasCurrentState() )
    {
      final Function attributeGetter = getMainObjectTranslator().getNormalFsm().getGetCurrentState();
      final int attributeBindIndex = tableTranslator.getCurrentStateBindIndex();
      attributeGetterCalls[attributeBindIndex - 1] = attributeGetter.asFunctionCall(objectVar.asExpression(), true);
    }
    return attributeGetterCalls;
  }

  private org.xtuml.masl.translate.main.object.ObjectTranslator getMainObjectTranslator ()
  {
    return objectTranslator.getMainObjectTranslator(objectDeclaration);
  }

}
