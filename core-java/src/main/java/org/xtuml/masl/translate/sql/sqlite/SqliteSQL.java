/*
 * Filename : SqliteSQL.java
 * 
 * UK Crown Copyright (c) 2008. All Rights Reserved
 */
package org.xtuml.masl.translate.sql.sqlite;


import java.util.Arrays;

import org.xtuml.masl.cppgen.BinaryExpression;
import org.xtuml.masl.cppgen.BinaryOperator;
import org.xtuml.masl.cppgen.BlankLine;
import org.xtuml.masl.cppgen.Class;
import org.xtuml.masl.cppgen.CodeBlock;
import org.xtuml.masl.cppgen.CodeFile;
import org.xtuml.masl.cppgen.Expression;
import org.xtuml.masl.cppgen.Function;
import org.xtuml.masl.cppgen.FunctionCall;
import org.xtuml.masl.cppgen.FundamentalType;
import org.xtuml.masl.cppgen.IfStatement;
import org.xtuml.masl.cppgen.Literal;
import org.xtuml.masl.cppgen.ReturnStatement;
import org.xtuml.masl.cppgen.Std;
import org.xtuml.masl.cppgen.TypeUsage;
import org.xtuml.masl.cppgen.UnaryExpression;
import org.xtuml.masl.cppgen.UnaryOperator;
import org.xtuml.masl.cppgen.Variable;
import org.xtuml.masl.cppgen.WhileStatement;
import org.xtuml.masl.metamodel.object.AttributeDeclaration;
import org.xtuml.masl.metamodel.type.ConstrainedType;
import org.xtuml.masl.metamodel.type.EnumerateType;
import org.xtuml.masl.metamodel.type.TypeDefinition;
import org.xtuml.masl.metamodel.type.UserDefinedType;
import org.xtuml.masl.translate.main.ASN1;
import org.xtuml.masl.translate.main.Architecture;
import org.xtuml.masl.translate.main.Types;


public class SqliteSQL
{

  /**
   * Define an enum that can be used to represent the columns allowed by SQLITE.
   * Use this enum to hold data on the column names that should be associated
   * with each type and the C API functions that should be invoked to extract
   * the data from the column.
   */
  private enum SqliteColumnType
  {
    INTEGER32("INTEGER"),
                                    INTEGER64("INTEGER"),
                                    DURATION("INTEGER"),
                                    TIMESTAMP("INTEGER"),
                                    TEXT("TEXT"),
                                    REAL("REAL"),
                                    BLOB("BLOB");

    private String columnType;

    SqliteColumnType ( final String columnType )
    {
      this.columnType = columnType;
    }

    String getColumnType ()
    {
      return columnType;
    }
  }

  /**
   * Need to make use of some of the general utilities supplied by the C++ SQL
   * framework library. Therefore reference the util header file.
   */
  static final CodeFile SqlUtilHeader = org.xtuml.masl.translate.sql.main.Database.utilInc;

  private SqliteSQL ()
  {

  }

  /**
   * @return the column name associated with the architecture id
   */
  static String getArchitectureIdName ()
  {
    return "architecture_id";
  }

  /**
   * @return the column type for Current State attribute of active bjects
   */
  static String getCurrentStateColumnType ()
  {
    return SqliteColumnType.INTEGER32.getColumnType();
  }

  /**
   * @return the column type for Enumerates as attributes of objects
   */
  static boolean isColumnBlobType ( final TypeDefinition baseType )
  {
    return getSqliteColumnType(baseType) == SqliteColumnType.BLOB;
  }

  /**
   * 

   *          the MASL type that needs to be mapped to a column type.
   * @return the name of the column type that will be used to store the
   *         specified type.
   */
  static String getColumnType ( final TypeDefinition baseType )
  {
    final SqliteColumnType columnType = getSqliteColumnType(baseType);
    return columnType.getColumnType();
  }

  /**
   * Break the type down into its base type use this to work out the associated
   * column type.
   * 

   *          the MASL type that needs to be mapped to a column type.
   * @return an enumeration that represents the deduced column type.
   */
  static private SqliteColumnType getSqliteColumnType ( final TypeDefinition baseType )
  {
    // If the type is not one of the base types, then
    // it is a complex type (structure/sequence) and
    // will need to be stored as a binary block of data
    // The binary representation of the type is undertaken
    // using ASN.1.
    SqliteColumnType columnType = null;

    switch ( baseType.getActualType() )
    {
      case SMALL_INTEGER:
      case TIMER:
        columnType = SqliteColumnType.INTEGER32;
        break;
      case INTEGER:
        columnType = SqliteColumnType.INTEGER64;
        break;
      case DURATION:
        columnType = SqliteColumnType.DURATION;
        break;
      case TIMESTAMP:
        columnType = SqliteColumnType.TIMESTAMP;
        break;
      case REAL:
        columnType = SqliteColumnType.REAL;
        break;
      case STRING:
        columnType = SqliteColumnType.TEXT;
        break;
      case BOOLEAN:
        columnType = SqliteColumnType.INTEGER32;
        break;
      case BYTE:
        columnType = SqliteColumnType.INTEGER32;
        break;
      case CHARACTER:
        columnType = SqliteColumnType.TEXT;
        break;
      case ENUMERATE:
        columnType = SqliteColumnType.TEXT;
        break;
      case USER_DEFINED:
        final UserDefinedType udt = (UserDefinedType)baseType;
        final TypeDefinition udtTypeDef = udt.getDefinedType();
        columnType = getSqliteColumnType(udtTypeDef);
        break;
      case SEQUENCE:
      case BAG:
      case SET:
      case ARRAY:
      case STRUCTURE:
      case DICTIONARY:
        columnType = SqliteColumnType.BLOB;
        break;
      case CONSTRAINED:
        final ConstrainedType constrainedType = (ConstrainedType)baseType;
        columnType = getSqliteColumnType(constrainedType.getFullType());
        break;
      default:
        throw new RuntimeException("Sqlite type to column translation failed for type : " + baseType);
    }
    return columnType;
  }

  /**
   * Using the supplied parameters create a select query that uses the max
   * function to determine the highest architecture id used for the specified
   * table name.
   * 

   *          the name of the class that the function belongs to (used for debug
   *          on error).

   *          the function body the query should be placed in.

   *          the SQL table name
   */
  static void createMaxQuery ( final String className,
                               final Expression attributeName,
                               final TypeUsage attributeType,
                               final Function actualFn,
                               final String tableName )
  {
    final Expression selectExpr = BinaryExpression.createCompoundExpression(BinaryOperator.PLUS,
                                                                            Std.string.callConstructor(Literal.createStringLiteral("SELECT max(")),
                                                                                              new Function("getColumnName").asFunctionCall(attributeName),
                                                                                              Literal.createStringLiteral(") FROM " + tableName
                                                                                                                          + ";"));
    final Variable valueVar = actualFn.getParameters().get(1);
    createSingleCellQuery(className, actualFn, selectExpr, valueVar, valueVar.getType());
  }

  /**
   * Using the supplied parameters create a select query that uses the count
   * function to determine the number of rows contained within the specified
   * table name.
   * 

   *          the name of the class that the function belongs to (used for debug
   *          on error).

   *          the function body the query should be placed in.

   *          the SQL table name.
   */
  static void createRowCountQuery ( final String className, final Function actualFn, final String tableName )
  {
    final Literal selectLiteral = Literal.createStringLiteral("SELECT count(*) FROM " + tableName + ";");
    final Variable rowCountVar = new Variable(new TypeUsage(Std.int32), "rowCount", new Literal(0));
    actualFn.getCode().appendStatement(rowCountVar.asStatement());
    createSingleCellQuery(className, actualFn, selectLiteral, rowCountVar, rowCountVar.getType());
    actualFn.getCode().appendStatement(new ReturnStatement(rowCountVar.asExpression()));
  }

  /**
   * Generic function that can be used to generate the required code to extract
   * a single integer cell value from the result set of a select query that uses
   * one of the associated SQL functions i.e. max/min/count
   * 

   *          the name of the class that the function belongs to (used for debug
   *          on error).

   *          the function body generated code should be placed in.

   *          the SQL table name.

   *          the select statement that needs to be executed.
   */
  static private void createSingleCellQuery ( final String className,
                                              final Function actualFn,
                                              final Expression selectLiteral,
                                              final Variable valueVar,
                                              final TypeUsage columnType )
  {

    // Create cpp line:
    // std::string
    // rowCountSelect("SELECT max(architecture_id) FROM S_FINDTESTOBJECTA;");
    final Variable valueSelectVar = new Variable(new TypeUsage(Std.string), "valueSelect", new Expression[]
      { selectLiteral });
    actualFn.getCode().appendStatement(valueSelectVar.asStatement());

    // Create cpp line:
    // Database& database = Database::singleton();
    final FunctionCall singletonFnCall = SqliteDatabase.getSqlitedatabaseclass().callStaticFunction("singleton");
    final Variable databaseVar = new Variable(new TypeUsage(SqliteDatabase.getSqlitedatabaseclass(), TypeUsage.Reference),
                                              "database",
                                              singletonFnCall);
    actualFn.getCode().appendStatement(databaseVar.asStatement());

    // Create cpp line:
    // ResultSet rowCountResult;
    final Variable valueResultVar = new Variable(new TypeUsage(SqliteDatabase.resultSetClass), "valueResult");
    actualFn.getCode().appendStatement(valueResultVar.asStatement());

    // Create cpp:
    // database.executeQuery( rowCountSelect,rowCountResult ) == true
    final Function executeQueryFn = new Function("executeQuery");
    final Expression executeQueryFnExpr = executeQueryFn.asFunctionCall(databaseVar.asExpression(),
                                                                        false,
                                                                        valueSelectVar.asExpression(),
                                                                        valueResultVar.asExpression());
    final BinaryExpression executeQueryCondition = new BinaryExpression(executeQueryFnExpr,
                                                                        BinaryOperator.EQUAL,
                                                                        new Literal("true"));

    final CodeBlock executeQueryTrueBranch = new CodeBlock();
    final CodeBlock executeQueryFalseBranch = new CodeBlock();
    final IfStatement ifExecuteQueryBlock = new IfStatement(executeQueryCondition, executeQueryTrueBranch, executeQueryFalseBranch);
    actualFn.getCode().appendStatement(ifExecuteQueryBlock);
    executeQueryFalseBranch.appendStatement(SqliteDatabase.throwDatabaseException(className + "::executeGetRowCount - query failed"));

    // Create cpp:
    // rowCountResult.getColumns() != 1
    final Function getColumnsFn = new Function("getColumns");
    final Expression getColumnsFnCall = getColumnsFn.asFunctionCall(valueResultVar.asExpression(), false);
    final BinaryExpression columnCountCondition = new BinaryExpression(getColumnsFnCall, BinaryOperator.NOT_EQUAL, new Literal("1"));

    // Create cpp:
    // rowCountResult.getRows() != 1
    final Function getRowsFn = new Function("getRows");
    final Expression getRowsFnCall = getRowsFn.asFunctionCall(valueResultVar.asExpression(), false);
    final BinaryExpression getRowsCondition = new BinaryExpression(getRowsFnCall, BinaryOperator.NOT_EQUAL, new Literal("1"));

    // Create cpp:
    // rowCountResult.getRows() != 1 && rowCountResult.getColumns() != 1
    final BinaryExpression queryResultCondition = new BinaryExpression(getRowsCondition, BinaryOperator.AND, columnCountCondition);

    final CodeBlock queryResultFailureBranch = new CodeBlock();
    queryResultFailureBranch.appendStatement(SqliteDatabase.throwDatabaseException(className + "::executeGetRowCount - incorrect result contents"));
    final IfStatement ifQueryResultBlock = new IfStatement(queryResultCondition, queryResultFailureBranch);
    executeQueryTrueBranch.appendStatement(ifQueryResultBlock);

    // Create cpp line:
    // const std::string& cellValue = rowCountResult.getRow(0).at(0);
    final Function getRowFn = new Function("getRow");
    final Function atFn = new Function("at");
    final Expression getRowFnCall = getRowFn.asFunctionCall(valueResultVar.asExpression(), false, new Literal("0"));
    final Expression atFnCall = atFn.asFunctionCall(getRowFnCall, false, new Literal("0"));
    final Variable cellValueVar = new Variable(new TypeUsage(Std.string, TypeUsage.ConstReference), "cellValue", atFnCall);
    executeQueryTrueBranch.appendStatement(cellValueVar.asStatement());

    // Create cpp line:
    // if (cellValue != "NULL"){
    final BinaryExpression nullCondition = new BinaryExpression(cellValueVar.asExpression(),
                                                                BinaryOperator.NOT_EQUAL,
                                                                Literal.createStringLiteral("NULL"));
    final CodeBlock notNullBlock = new CodeBlock();
    final IfStatement ifNullCell = new IfStatement(nullCondition, notNullBlock);
    executeQueryTrueBranch.appendStatement(ifNullCell);

    // Create cpp line:
    // value = stringToValue< ::SWA::IdType >(cellValue);
    final Function stringToValueFn = org.xtuml.masl.translate.sql.main.Database.getStringToValueFn();
    stringToValueFn.addTemplateSpecialisation(new TypeUsage(valueVar.getType().getType()));
    final Expression stringToValueFnCall = stringToValueFn.asFunctionCall(new Expression[]
      { cellValueVar.asExpression() });
    final BinaryExpression rowCountAssign = new BinaryExpression(valueVar.asExpression(),
                                                                 BinaryOperator.ASSIGN,
                                                                 stringToValueFnCall);
    notNullBlock.appendStatement(rowCountAssign.asStatement());
  }

  /**
   * This function will generate the standard sqlite code block to enable the
   * execution of a query and the processing of the associated result set. It
   * will generate all the required error handling code.
   * 

   *          the sqlite database handle

   *          the select query to be executed

   *          the name of the method that this code block wil be placed in (used
   *          for debug on error)

   *          the code block to place all generated code.

   *          the expected number of columns in the query result set.
   * @return the inner code block that all further statements, relating to
   *         column extraction, should be placed.
   */
  static CodeBlock addDbQueryCodeBlock ( final Variable sqlite3Stmt,
                                         final Expression queryExpr,
                                         final Expression methodName,
                                         final CodeBlock resultSetBlock,
                                         final int columnCount )
  {
    // Create cpp line:
    // Database& database = Database::singleton();
    final FunctionCall singletonFnCall = SqliteDatabase.getSqlitedatabaseclass().callStaticFunction("singleton");
    final Variable databaseVar = new Variable(new TypeUsage(SqliteDatabase.getSqlitedatabaseclass(), TypeUsage.Reference),
                                              "database",
                                              singletonFnCall);
    resultSetBlock.appendStatement(databaseVar.asStatement());
    resultSetBlock.appendStatement(new BlankLine(0));

    // Create cpp line:
    // sqlite3_stmt *ppStmt = 0;
    resultSetBlock.appendStatement(sqlite3Stmt.asStatement());

    // Create cpp line:
    // Database::ScopedFinalise
    // finaliser("RelationshipR11SqlGenerator::loadAss",ppStmt);
    final Class databaseClass = (Class)databaseVar.getType().getType();
    final Class finaliserClass = databaseClass.referenceNestedType("ScopedFinalise");
    final Variable finaliserVar = new Variable(new TypeUsage(finaliserClass), "finaliser", new Expression[]
      { methodName, sqlite3Stmt.asExpression() });
    resultSetBlock.appendStatement(finaliserVar.asStatement());

    // Create cpp line:
    // int compile_result =
    // sqlite3_prepare(database.getDatabaseImpl(),query.c_str(),-1,&ppStmt,0);
    final Expression getCStr = new Function("c_str").asFunctionCall(queryExpr, false);
    final Expression getImplFnCall = new Function("getDatabaseImpl").asFunctionCall(databaseVar.asExpression(), false);
    final Expression getImplFnArgs[] =
      {  getImplFnCall,
          getCStr,
          new Literal("-1"),
          new UnaryExpression(UnaryOperator.ADDRESS_OF, sqlite3Stmt.asExpression()),
          new Literal("0") };
    final Expression compileResultAssignment = new Function("sqlite3_prepare").asFunctionCall(getImplFnArgs);
    final Variable compileResultVar = new Variable(new TypeUsage(Std.int32), "compile_result", compileResultAssignment);
    resultSetBlock.appendStatement(compileResultVar.asStatement());
    resultSetBlock.appendStatement(new BlankLine(0));

    // Create cpp line:
    // database.checkCompile ("RelationshipR1SqlGenerator::loadMany",
    // compile_result, query.str());
    final Function checkCompileFn = new Function("checkCompile");
    final Expression checkCompileFnCall = checkCompileFn.asFunctionCall(databaseVar.asExpression(),
                                                                        false,
                                                                        methodName,
                                                                        compileResultVar.asExpression(),
                                                                        queryExpr);
    resultSetBlock.appendExpression(checkCompileFnCall);

    // Create cpp line:
    // database.checkColumnCount
    // ("RelationshipR1SqlGenerator::loadMany",sqlite3_column_count( ppStmt ));
    final Function checkColumnCountFn = new Function("checkColumnCount");
    final Function sqlite3ColumnCountFn = new Function("sqlite3_column_count");
    final Expression sqlite3ColumnCountFnCall = sqlite3ColumnCountFn.asFunctionCall(new Expression[]
      { sqlite3Stmt.asExpression() });
    final Expression checkColumnCountFnCall = checkColumnCountFn.asFunctionCall(databaseVar.asExpression(),
                                                                                false,
                                                                                methodName,
                                                                                sqlite3ColumnCountFnCall,
                                                                                new Literal(columnCount),
                                                                                queryExpr);
    resultSetBlock.appendExpression(checkColumnCountFnCall);
    resultSetBlock.appendStatement(new BlankLine(0));

    // Create cpp line:
    // while(sqlite3_step(ppStmt) == SQLITE_ROW) {
    final CodeBlock stepWhileBlock = new CodeBlock();
    final Expression sqlite3StepfnCall = new Function("sqlite3_step").asFunctionCall(sqlite3Stmt.asExpression());
    final Expression sqlite3StepWhileCondition = new BinaryExpression(sqlite3StepfnCall,
                                                                      BinaryOperator.EQUAL,
                                                                      new Literal("SQLITE_ROW"));
    final WhileStatement sqlite3StepLoop = new WhileStatement(sqlite3StepWhileCondition, stepWhileBlock);
    resultSetBlock.appendStatement(sqlite3StepLoop);

    // Create cpp line:
    // SqlQueryMonitor(query);
    final Variable monitorVar = new Variable(new TypeUsage(SqliteDatabase.sqlMonitorClass), "queryMonitor", new Expression[]
      { queryExpr });
    resultSetBlock.appendStatement(monitorVar.asStatement());

    return stepWhileBlock;
  }

  /**
   * Generate the required code to de-serialise the data stored in a table
   * column to it's associated object attribute. The generated code uses the
   * SQLite C API.
   * 

   *          the sqlite database handle.

   *          the code block to place all generated code.

   *          the attribute that maps to the associated column index.

   *          the column index to read data from.
   * @return the value read from the specified column.
   */
  static Variable addReadBinaryColumn ( final Variable sqlite3Stmt,
                                        final CodeBlock codeBlock,
                                        final AttributeDeclaration attribute,
                                        final int columnIndex )
  {
    Variable readValueVar = null;

    // The enumeration types are stored in the database as text, but when
    // this text has been extracted, it needs to be changed back into the
    // required enum value.
    if ( attribute.getType().getDefinedType() instanceof EnumerateType )
    {
      readValueVar = addReadBinaryTextColumn(sqlite3Stmt, codeBlock, attribute.getName(), columnIndex);
      final TypeUsage attributeType = Types.getInstance().getType(attribute.getType());
      final Variable enumVar = new Variable(attributeType, "column" + columnIndex, new Expression[]
        { readValueVar.asExpression() });
      codeBlock.appendStatement(enumVar.asStatement());
      readValueVar = enumVar;
    }
    else
    {
      final SqliteColumnType columnType = SqliteSQL.getSqliteColumnType(attribute.getType().getBasicType());
      switch ( columnType )
      {
        case INTEGER32:
          readValueVar = addReadBinaryIntColumn(sqlite3Stmt, codeBlock, attribute.getName(), columnIndex);
          break;
        case INTEGER64:
          readValueVar = addReadBinaryInt64Column(sqlite3Stmt, codeBlock, attribute.getName(), columnIndex);
          break;
        case DURATION:
          readValueVar = addReadBinaryDurationColumn(sqlite3Stmt, codeBlock, attribute.getName(), columnIndex);
          break;
        case TIMESTAMP:
          readValueVar = addReadBinaryTimestampColumn(sqlite3Stmt, codeBlock, attribute.getName(), columnIndex);
          break;
        case TEXT:
          readValueVar = addReadBinaryTextColumn(sqlite3Stmt, codeBlock, attribute.getName(), columnIndex);
          break;
        case REAL:
          readValueVar = addReadBinaryRealColumn(sqlite3Stmt, codeBlock, attribute.getName(), columnIndex);
          break;
        case BLOB:
          readValueVar = addReadBinaryBlobColumn(sqlite3Stmt, codeBlock, attribute, columnIndex);
          break;
        default:
          throw new RuntimeException("Failed to find SQLITE column type for attribute : " + attribute);
      }
    }
    return readValueVar;
  }

  /**
   * Read an integer value from the specified column index and place it in an
   * int variable.
   * 

   *          the sqlite database handle.

   *          the code block to place all generated code.

   *          the name to use for the variable that will hold the extracted
   *          column value.

   *          the column index to read data from.
   * @return the value extracted from the specified column.
   */
  static Variable addReadBinaryIntColumn ( final Variable sqlite3Stmt,
                                           final CodeBlock codeBlock,
                                           final String destVarName,
                                           final int columnIndex )
  {
    // Create cpp line:
    // int32_t rhsColumnValue = sqlite3_column_int( ppStmt, 1);
    codeBlock.appendStatement(new BlankLine(0));

    final Expression sqlite3FnCall = new Function("sqlite3_column_int").asFunctionCall(sqlite3Stmt.asExpression(),
                                                                                       new Literal(columnIndex));
    final Variable destValueVar = new Variable(new TypeUsage(Std.int32), destVarName, sqlite3FnCall);
    codeBlock.appendStatement(destValueVar.asStatement());
    return destValueVar;
  }

  /**
   * Read an integer value from the specified column index and place it in an
   * int64 variable.
   * 

   *          the sqlite database handle.

   *          the code block to place all generated code.

   *          the name to use for the variable that will hold the extracted
   *          column value.

   *          the column index to read data from.
   * @return the value extracted from the specified column.
   */
  static Variable addReadBinaryInt64Column ( final Variable sqlite3Stmt,
                                             final CodeBlock codeBlock,
                                             final String destVarName,
                                             final int columnIndex )
  {
    // Create cpp line:
    // int32_t rhsColumnValue = sqlite3_column_int64( ppStmt, 1);
    codeBlock.appendStatement(new BlankLine(0));

    final Expression sqlite3FnCall = new Function("sqlite3_column_int64").asFunctionCall(sqlite3Stmt.asExpression(),
                                                                                         new Literal(columnIndex));
    final Variable destValueVar = new Variable(new TypeUsage(Std.int64), destVarName, sqlite3FnCall);
    codeBlock.appendStatement(destValueVar.asStatement());
    return destValueVar;
  }

  /**
   * Read a timestamp value from the specified column index and place it in an
   * SWA::Timetamp variable.
   * 

   *          the sqlite database handle.

   *          the code block to place all generated code.

   *          the name to use for the variable that will hold the extracted
   *          column value.

   *          the column index to read data from.
   * @return the value extracted from the specified column.
   */
  static Variable addReadBinaryTimestampColumn ( final Variable sqlite3Stmt,
                                                 final CodeBlock codeBlock,
                                                 final String destVarName,
                                                 final int columnIndex )
  {
    // Create cpp line:
    // int32_t rhsColumnValue = sqlite3_column_int64( ppStmt, 1);
    codeBlock.appendStatement(new BlankLine(0));

    final Expression sqlite3FnCall = new Function("sqlite3_column_int64").asFunctionCall(sqlite3Stmt.asExpression(),
                                                                                         new Literal(columnIndex));
    final Variable destValueVar = new Variable(new TypeUsage(Architecture.Timestamp.timestampClass),
                                               destVarName,
                                               Architecture.Timestamp.createFromNanosSinceEpoch(sqlite3FnCall));
    codeBlock.appendStatement(destValueVar.asStatement());
    return destValueVar;
  }

  /**
   * Read a duration value from the specified column index and place it in an
   * SWA::Duration variable.
   * 

   *          the sqlite database handle.

   *          the code block to place all generated code.

   *          the name to use for the variable that will hold the extracted
   *          column value.

   *          the column index to read data from.
   * @return the value extracted from the specified column.
   */
  static Variable addReadBinaryDurationColumn ( final Variable sqlite3Stmt,
                                                final CodeBlock codeBlock,
                                                final String destVarName,
                                                final int columnIndex )
  {
    // Create cpp line:
    // int32_t rhsColumnValue = sqlite3_column_int64( ppStmt, 1);
    codeBlock.appendStatement(new BlankLine(0));

    final Expression sqlite3FnCall = new Function("sqlite3_column_int64").asFunctionCall(sqlite3Stmt.asExpression(),
                                                                                         new Literal(columnIndex));
    final Variable destValueVar = new Variable(new TypeUsage(Architecture.Duration.durationClass),
                                               destVarName,
                                               Architecture.Duration.fromNanos(sqlite3FnCall));
    codeBlock.appendStatement(destValueVar.asStatement());
    return destValueVar;
  }


  /**
   * Read an real value from the specified column index and place it in an
   * double variable.
   * 

   *          the sqlite database handle.

   *          the code block to place all generated code.

   *          the name to use for the variable that will hold the extracted
   *          column value.

   *          the column index to read data from.
   * @return the value extracted from the specified column.
   */
  static Variable addReadBinaryRealColumn ( final Variable sqlite3Stmt,
                                            final CodeBlock codeBlock,
                                            final String destVarName,
                                            final int columnIndex )
  {
    // Create cpp line:
    // double rhsColumnValue = sqlite3_column_double( ppStmt, 1);
    codeBlock.appendStatement(new BlankLine(0));

    final Expression sqlite3FnCall = new Function("sqlite3_column_double").asFunctionCall(sqlite3Stmt.asExpression(),
                                                                                          new Literal(columnIndex));
    final Variable destValueVar = new Variable(new TypeUsage(FundamentalType.DOUBLE), destVarName, sqlite3FnCall);
    codeBlock.appendStatement(destValueVar.asStatement());
    return destValueVar;
  }

  /**
   * Read an text value from the specified column index and place it in an
   * std::string variable.
   * 

   *          the sqlite database handle.

   *          the code block to place all generated code.

   *          the name to use for the variable that will hold the extracted
   *          column value.

   *          the column index to read data from.
   * @return the value extracted from the specified column.
   */
  static Variable addReadBinaryTextColumn ( final Variable sqlite3Stmt,
                                            final CodeBlock codeBlock,
                                            final String destVarName,
                                            final int columnIndex )
  {
    // Create cpp line:
    // double rhsColumnValue = sqlite3_column_text( ppStmt, 1);
    codeBlock.appendStatement(new BlankLine(0));

    final Function reinterpretCastFn = new Function("reinterpret_cast");
    reinterpretCastFn.addTemplateSpecialisation(new TypeUsage(FundamentalType.CHAR, TypeUsage.PointerToConst));

    final Expression sqlite3FnCall = new Function("sqlite3_column_text").asFunctionCall(sqlite3Stmt.asExpression(),
                                                                                        new Literal(columnIndex));
    final Variable destValueVar = new Variable(new TypeUsage(Std.string),
                                               destVarName,
                                               reinterpretCastFn.asFunctionCall(sqlite3FnCall));
    codeBlock.appendStatement(destValueVar.asStatement());
    return destValueVar;
  }

  /**
   * Read an complex value from the specified column index and place it into a
   * variable of the required type.
   * 

   *          the sqlite database handle.

   *          the code block to place all generated code.

   *          the name to use for the variable that will hold the extracted
   *          column value.

   *          the column index to read data from.
   * @return the value extracted from the specified column.
   */
  static Variable addReadBinaryBlobColumn ( final Variable sqlite3Stmt,
                                            final CodeBlock codeBlock,
                                            final AttributeDeclaration attribute,
                                            final int columnIndex )
  {
    // Invoke the ASN.1 serialisation interface for the type and generate
    // code like the following:
    //
    // ::masld_BLAH::maslt_component_info column7;
    // int32_t column7Bytes = sqlite3_column_bytes( ppStmt, 7 );
    // const unsigned char* column7Blob = reinterpret_cast<const unsigned
    // char*>( sqlite3_column_blob( ppStmt, 7 ) );
    // ::SWA::DecodeBuffer column7Source(column7Blob, column7Bytes);
    // ::masld_BLAH::ASN1::Asn1Convertor::decode_type_component_info(
    // column7Source, column7 );
    // currentObject->set_masla_dummy_att_2( column7 );
    //

    codeBlock.appendStatement(new BlankLine(0));

    final TypeUsage attributeType = Types.getInstance().getType(attribute.getType());
    final Variable destValueVar = new Variable(attributeType, "column" + columnIndex);
    codeBlock.appendStatement(destValueVar.asStatement());

    final Expression bytesInColumnFnCall = new Function("sqlite3_column_bytes").asFunctionCall(sqlite3Stmt.asExpression(),
                                                                                               new Literal(columnIndex));
    final Variable bytesInColumnVar = new Variable(new TypeUsage(Std.int32), "column" + columnIndex + "Bytes", bytesInColumnFnCall);

    final Function reinterpretCastFn = new Function("reinterpret_cast");
    reinterpretCastFn.addTemplateSpecialisation(new TypeUsage(FundamentalType.UCHAR, TypeUsage.PointerToConst));

    final Expression blobColumnFnCall = reinterpretCastFn.asFunctionCall(new Function("sqlite3_column_blob").asFunctionCall(sqlite3Stmt.asExpression(),
                                                                                                                            new Literal(columnIndex)));
    final Variable blobColumnVar = new Variable(new TypeUsage(FundamentalType.UCHAR, TypeUsage.PointerToConst),
                                                "column" + columnIndex + "Blob",
                                                blobColumnFnCall);

    codeBlock.appendStatement(bytesInColumnVar.asStatement());
    codeBlock.appendStatement(blobColumnVar.asStatement());

    final Variable decoder = new Variable(new TypeUsage(ASN1.BERDecoder(new TypeUsage(FundamentalType.UCHAR,
                                                                                      TypeUsage.PointerToConst))),
                                          "decoder" + columnIndex,
                                          Arrays
                                                .<Expression>asList(blobColumnVar.asExpression()));
    codeBlock.appendStatement(decoder.asStatement());

    codeBlock.appendStatement(ASN1.BERDecode.asFunctionCall(decoder.asExpression(), destValueVar.asExpression()).asStatement());
    return destValueVar;
  }
}
