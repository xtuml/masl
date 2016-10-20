/*
 * Filename : SqliteObjectToTableTranslator.java
 * 
 * UK Crown Copyright (c) 2008. All Rights Reserved
 */
package org.xtuml.masl.translate.sql.sqlite;

import java.util.ArrayList;
import java.util.List;

import org.xtuml.masl.cppgen.BinaryExpression;
import org.xtuml.masl.cppgen.BinaryOperator;
import org.xtuml.masl.cppgen.BlankLine;
import org.xtuml.masl.cppgen.Class;
import org.xtuml.masl.cppgen.CodeBlock;
import org.xtuml.masl.cppgen.Expression;
import org.xtuml.masl.cppgen.Function;
import org.xtuml.masl.cppgen.IfStatement;
import org.xtuml.masl.cppgen.Literal;
import org.xtuml.masl.cppgen.NewExpression;
import org.xtuml.masl.cppgen.ReturnStatement;
import org.xtuml.masl.cppgen.Std;
import org.xtuml.masl.cppgen.Type;
import org.xtuml.masl.cppgen.TypeUsage;
import org.xtuml.masl.cppgen.Variable;
import org.xtuml.masl.metamodel.object.AttributeDeclaration;
import org.xtuml.masl.metamodel.object.ObjectDeclaration;
import org.xtuml.masl.translate.main.Architecture;
import org.xtuml.masl.translate.main.Boost;
import org.xtuml.masl.translate.main.Mangler;
import org.xtuml.masl.translate.sql.main.ImplementationClass;
import org.xtuml.masl.translate.sql.main.ObjectToTableTranslator;
import org.xtuml.masl.translate.sql.main.ObjectTranslator;
import org.xtuml.masl.translate.sql.main.PreparedStatement;


public class SqliteObjectToTableTranslator
    implements ObjectToTableTranslator
{

  static private final String     CurrentStateColumnName = "CurrentState";

  private final List<String>      columnNameList;
  private final List<String>      attributeNameList;
  private String                  createTableStatement;

  private final ObjectDeclaration objectDeclaration;
  private final ObjectTranslator  objectTranslator;

  private final Class             psObjectPtrClass       = org.xtuml.masl.translate.sql.main.Database.psObjectPtrClass;
  private final Class             psObjectClass          = org.xtuml.masl.translate.sql.main.Database.psObjectClass;

  public SqliteObjectToTableTranslator ( final ObjectTranslator objectTran, final ObjectDeclaration objectDecl )
  {
    objectDeclaration = objectDecl;
    objectTranslator = objectTran;
    columnNameList = new ArrayList<String>();
    attributeNameList = new ArrayList<String>();
    formCreateTableStatement();
  }

  @Override
  public String getTableName ()
  {
    return "S_" + objectDeclaration.getDomain().getName() + "_" + objectDeclaration.getName().toUpperCase();
  }

  @Override
  public int getArchIdColumnIndex ()
  {
    return 0;
  }

  @Override
  public int getArchIdBindIndex ()
  {
    return getArchIdColumnIndex() + 1;
  }

  public int getColumnCount ()
  {
    return columnNameList.size();
  }

  @Override
  public List<String> getColumnNameList ()
  {
    return columnNameList;
  }

  @Override
  public void addGetMaxIdFnBody ( final String mapperClassName, final Function executeMaxFn )
  {
    final Variable maxIdValueVar = new Variable(new TypeUsage(Architecture.ID_TYPE), "maxIdValue", new Literal(0));
    executeMaxFn.getCode().appendStatement(maxIdValueVar.asStatement());

    final Function executeGetMaxColumnValue = new Function("executeGetMaxColumnValue");
    final Expression getMaxFnCall = executeGetMaxColumnValue.asFunctionCall(Literal.createStringLiteral(SqliteSQL.getArchitectureIdName()),
                                                                            maxIdValueVar.asExpression());
    executeMaxFn.getCode().appendExpression(getMaxFnCall);

    executeMaxFn.getCode().appendStatement(new ReturnStatement(maxIdValueVar.asExpression()));
  }

  @Override
  public void addGetMaxFnBody ( final String mapperClassName, final Variable attributeName, final Function executeMaxFn )
  {
    SqliteSQL.createMaxQuery(mapperClassName, attributeName.asExpression(), attributeName.getType(), executeMaxFn, getTableName());
  }

  @Override
  public void addGetRowCountFnBody ( final String mapperClassName, final Function executeRowCountFn )
  {
    SqliteSQL.createRowCountQuery(mapperClassName, executeRowCountFn, getTableName());
  }

  @Override
  public List<String> getAttributeNameList ()
  {
    return attributeNameList;
  }

  @Override
  public String getCreateTableStatement ()
  {
    return createTableStatement;
  }

  @Override
  public boolean isBlobColumn ( final AttributeDeclaration attribute )
  {
    return SqliteSQL.isColumnBlobType(attribute.getType());
  }

  @Override
  public int getColumnIndex ( final AttributeDeclaration attribute )
  {
    return attributeNameList.indexOf(attribute.getName());
  }

  public int getColumnIndex ( final String columnName )
  {
    return columnNameList.indexOf(columnName);
  }

  @Override
  public int getBindIndex ( final AttributeDeclaration attribute )
  {
    return getColumnIndex(attribute) + 1;
  }

  @Override
  public boolean hasCurrentStateColumn ()
  {
    return columnNameList.contains(CurrentStateColumnName);
  }

  @Override
  public int getCurrentStateBindIndex ()
  {
    return columnNameList.indexOf(CurrentStateColumnName) + 1;
  }

  @Override
  public PreparedStatement createPreparedStatement ( final PreparedStatement.PreparedStatementType classification )
  {
    return new SqliteObjectPreparedStatement(this, classification);
  }

  @Override
  public void addExecuteSelectBody ( final Function selectFn,
                                     final Variable cacheParameter,
                                     final Variable criteriaParameter,
                                     final Variable resultParameter )
  {
    final Variable sqlite3Stmt = new Variable(new TypeUsage(SqliteDatabase.sqlite3StmtClass, TypeUsage.Pointer),
                                              "ppStmt",
                                              new Literal("0"));
    final CodeBlock innerBlock = addDbAccessCodeBlock(sqlite3Stmt, selectFn, criteriaParameter);
    addCacheInsertCodeBlock(sqlite3Stmt, innerBlock, cacheParameter, resultParameter);
  }

  @Override
  public void addExecuteSelectBody ( final Function selectFn, final Variable cacheParameter, final Variable criteriaParameter )
  {
    final Variable sqlite3Stmt = new Variable(new TypeUsage(SqliteDatabase.sqlite3StmtClass, TypeUsage.Pointer),
                                              "ppStmt",
                                              new Literal("0"));
    final CodeBlock innerBlock = addDbAccessCodeBlock(sqlite3Stmt, selectFn, criteriaParameter);
    addCacheInsertCodeBlock(sqlite3Stmt, innerBlock, cacheParameter, null);
  }

  private CodeBlock addDbAccessCodeBlock ( final Variable sqlite3Stmt, final Function selectFn, final Variable criteriaParameter )
  {
    // Create cpp line:
    // std::string query = criteria.selectStatement(getTableName());
    final Expression queryValue = new Function("selectStatement").asFunctionCall(criteriaParameter.asExpression(), false);
    final Variable queryVar = new Variable(new TypeUsage(Std.string), "query", queryValue);
    selectFn.getCode().appendStatement(queryVar.asStatement());

    final CodeBlock resultSetBlock = new CodeBlock();
    selectFn.getCode().appendStatement(resultSetBlock);

    final Expression methodName = Literal.createStringLiteral(objectDeclaration.getName() + "::" + selectFn.getName());
    return SqliteSQL.addDbQueryCodeBlock(sqlite3Stmt, queryVar.asExpression(), methodName, resultSetBlock, getColumnCount());
  }

  private void addCacheInsertCodeBlock ( final Variable sqlite3Stmt,
                                         final CodeBlock parentBlock,
                                         final Variable cacheParameter,
                                         final Variable resultParameter )
  {
    // Create cpp line:
    // int64_t column0 = sqlite3_column_int(ppStmt,0);
    final Variable column0Var = SqliteSQL.addReadBinaryIntColumn(sqlite3Stmt, parentBlock, "column0", 0);

    final Class cacheParameterClass = (Class)cacheParameter.getType().getType();

    // Create cpp line:
    // CacheType::iterator objectItr = cache.find(column0);
    final Expression findFnCall = new Function("find").asFunctionCall(cacheParameter.asExpression(),
                                                                      false,
                                                                      column0Var.asExpression());
    final Variable objectItrVar = new Variable(new TypeUsage(cacheParameterClass.referenceNestedType("iterator")),
                                               "objectItr",
                                               findFnCall);
    parentBlock.appendStatement(objectItrVar.asStatement());

    if ( resultParameter != null )
    {
      // Create cpp line:
      // if (objectItr != cache.end()){
      final CodeBlock objectEndTrueBlock = new CodeBlock();
      final CodeBlock objectEndFalseBlock = new CodeBlock();
      final Expression endFnCall = new Function("end").asFunctionCall(cacheParameter.asExpression(), false);
      final Expression objectEndCondition = new BinaryExpression(objectItrVar.asExpression(), BinaryOperator.NOT_EQUAL, endFnCall);
      final IfStatement ifobjectEndBlock = new IfStatement(objectEndCondition, objectEndTrueBlock, objectEndFalseBlock);
      parentBlock.appendStatement(ifobjectEndBlock);

      // Create cpp line:
      // PsBaseObjectPtr currentObject(objectItr->second.get());
      final Expression getFnCall = new Function("get").asFunctionCall(new Variable("second").asMemberReference(objectItrVar.asExpression(),
                                                                                                               true),
                                                                      false);
      final Variable currentObjectVar = new Variable(new TypeUsage(org.xtuml.masl.translate.sql.main.Database.psBaseObjectPtrClass),
                                                     "currentObject",
                                                     new Expression[]
                                                       { getFnCall });
      objectEndTrueBlock.appendStatement(currentObjectVar.asStatement());

      final BinaryExpression insertObjExpr = new BinaryExpression(resultParameter.asExpression(),
                                                                  BinaryOperator.PLUS_ASSIGN,
                                                                  currentObjectVar.asExpression());
      objectEndTrueBlock.appendExpression(insertObjExpr);

      // Create cpp line:
      // PsObjectPtr object(new maslo_Find_Test_Object_A(column0));
      final List<Expression> constructorArgs = new ArrayList<Expression>();
      constructorArgs.add(column0Var.asExpression());
      final Expression newObjectExpr = new NewExpression(new TypeUsage(objectTranslator.getClass(ImplementationClass.KEY_NAME)),
                                                         constructorArgs);
      final Variable objectVar = new Variable(new TypeUsage(psObjectPtrClass), "currentObject", new Expression[]
        { newObjectExpr });
      objectEndFalseBlock.appendStatement(objectVar.asStatement());

      // Create cpp line:
      // cache.insert( CacheType::value_type(column0,
      // ::boost::shared_ptr<PsObject>( object.get() ) ) );
      final Expression objectGetFnCall = new Function("get").asFunctionCall(objectVar.asExpression(), false);
      final Expression boostSharedPtr = Boost.getSharedPtrType(new TypeUsage(psObjectClass)).callConstructor(objectGetFnCall);
      final Class valueTypeDef = cacheParameterClass.referenceNestedType("value_type");
      final Expression insertArgs = valueTypeDef.callConstructor(column0Var.asExpression(), boostSharedPtr);
      final Expression insertFnCall = new Function("insert").asFunctionCall(cacheParameter.asExpression(), false, insertArgs);
      objectEndFalseBlock.appendExpression(insertFnCall);
      objectEndFalseBlock.appendExpression(insertObjExpr);
      addDbCellValueExtraction(sqlite3Stmt, objectVar, objectEndFalseBlock);
    }
    else
    {
      // Create cpp line:
      // if (objectItr == cache.end()){
      final CodeBlock objectEndTrueBlock = new CodeBlock();
      final Expression endFnCall = new Function("end").asFunctionCall(cacheParameter.asExpression(), false);
      final Expression objectEndCondition = new BinaryExpression(objectItrVar.asExpression(), BinaryOperator.EQUAL, endFnCall);
      final IfStatement ifobjectEndBlock = new IfStatement(objectEndCondition, objectEndTrueBlock);
      parentBlock.appendStatement(ifobjectEndBlock);

      // Create cpp line:
      // PsObjectPtr object(new maslo_Find_Test_Object_A(column0));
      final List<Expression> constructorArgs = new ArrayList<Expression>();
      constructorArgs.add(column0Var.asExpression());
      final Expression newObjectExpr = new NewExpression(new TypeUsage(objectTranslator.getClass(ImplementationClass.KEY_NAME)),
                                                         constructorArgs);
      final Variable objectVar = new Variable(new TypeUsage(psObjectPtrClass), "currentObject", new Expression[]
        { newObjectExpr });
      objectEndTrueBlock.appendStatement(objectVar.asStatement());

      // Create cpp line:
      // cache.insert( CacheType::value_type(column0,
      // ::boost::shared_ptr<PsObject>( object.get() ) ) );
      final Expression objectGetFnCall = new Function("get").asFunctionCall(objectVar.asExpression(), false);
      final Expression boostSharedPtr = Boost.getSharedPtrType(new TypeUsage(psObjectClass)).callConstructor(objectGetFnCall);
      final Class valueTypeDef = cacheParameterClass.referenceNestedType("value_type");
      final Expression insertArgs = valueTypeDef.callConstructor(column0Var.asExpression(), boostSharedPtr);
      final Expression insertFnCall = new Function("insert").asFunctionCall(cacheParameter.asExpression(), false, insertArgs);
      objectEndTrueBlock.appendExpression(insertFnCall);
      addDbCellValueExtraction(sqlite3Stmt, objectVar, objectEndTrueBlock);
    }
  }

  private void addDbCellValueExtraction ( final Variable sqlite3Stmt, final Variable objectVar, final CodeBlock extractionBlock )
  {
    // Loop aound the attributes and extract the data
    for ( final AttributeDeclaration attribute : objectDeclaration.getAttributes() )
    {
      final int columnIndex = getColumnIndex(attribute);
      // If there is no valid column index then the attribute must be
      // referential and
      // will not have an associated column stored in the table.
      if ( columnIndex != -1 )
      {
        final Variable extractedValueVar = SqliteSQL.addReadBinaryColumn(sqlite3Stmt, extractionBlock, attribute, columnIndex);
        final Function setterFunction = objectTranslator.getSetterMethod(attribute);
        final Expression setterFnCall = setterFunction.asFunctionCall(objectVar.asExpression(),
                                                                      true,
                                                                      extractedValueVar.asExpression());
        extractionBlock.appendExpression(setterFnCall);
      }
    }

    // Add the CurrentState attribute if one is defined.
    if ( objectDeclaration.hasCurrentState() )
    {
      final int columnIndex = getColumnIndex(CurrentStateColumnName);
      if ( columnIndex != -1 )
      {
        final Variable extractedValueVar = SqliteSQL.addReadBinaryIntColumn(sqlite3Stmt,
                                                                            extractionBlock,
                                                                            "currentState",
                                                                            columnIndex);
        final Function setterFunction = getMainObjectTranslator().getNormalFsm().getSetCurrentState();
        final Type attributeType = setterFunction.getParameters().get(0).getType().getType();
        final Expression staticCastFnCall = Std.static_cast(new TypeUsage(attributeType))
                                               .asFunctionCall(extractedValueVar.asExpression());
        final Expression setterFnCall = setterFunction.asFunctionCall(objectVar.asExpression(), true, staticCastFnCall);

        extractionBlock.appendExpression(setterFnCall);
      }
    }

    // Create cpp line:
    // obj.markAsClean()
    final Expression markAsCleanFnCall = new Function("markAsClean").asFunctionCall(objectVar.asExpression(), true);
    extractionBlock.appendStatement(new BlankLine(1));
    extractionBlock.appendExpression(markAsCleanFnCall);
  }

  List<String> getColumnList ()
  {
    return columnNameList;
  }

  private void formCreateTableStatement ()
  {
    final StringBuilder createStatement = new StringBuilder("CREATE TABLE " + getTableName() + "(");
    createStatement.append("   " + SqliteSQL.getArchitectureIdName() + "  INTEGER ");

    columnNameList.add(SqliteSQL.getArchitectureIdName());
    attributeNameList.add(SqliteSQL.getArchitectureIdName());

    for ( final AttributeDeclaration attribute : objectDeclaration.getAttributes() )
    {
      if ( attribute.isIdentifier() == true || !attribute.isReferential() )
      {
        // The attribute is not referential, so it
        // will need an associated column
        createStatement.append(",");
        final String columnName = Mangler.mangleName((attribute));
        final String columnType = SqliteSQL.getColumnType(attribute.getType().getBasicType());
        createStatement.append("   " + columnName + " " + columnType);
        columnNameList.add(Mangler.mangleName((attribute)));
        attributeNameList.add((attribute.getName()));
      }
    }

    addCurrentState(createStatement);

    createStatement.append(", PRIMARY KEY (" + SqliteSQL.getArchitectureIdName() + ")");
    createStatement.append(");\n");
    createTableStatement = createStatement.toString();
  }

  private void addCurrentState ( final StringBuilder createStatement )
  {
    if ( objectDeclaration.hasCurrentState() )
    {
      final String columnName = CurrentStateColumnName;
      final String columnType = SqliteSQL.getCurrentStateColumnType();
      createStatement.append(",");
      createStatement.append("   " + columnName + " " + columnType);
      columnNameList.add(CurrentStateColumnName);
      attributeNameList.add(CurrentStateColumnName);
    }
  }

  private org.xtuml.masl.translate.main.object.ObjectTranslator getMainObjectTranslator ()
  {
    return org.xtuml.masl.translate.main.object.ObjectTranslator.getInstance(objectDeclaration);
  }
}
