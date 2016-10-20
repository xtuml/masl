/*
 * Filename : SqliteTenaryRelationshipToTableTranslator.java
 * 
 * UK Crown Copyright (c) 2008. All Rights Reserved
 */
package org.xtuml.masl.translate.sql.sqlite;

import java.util.ArrayList;
import java.util.List;

import org.xtuml.masl.cppgen.ArrayAccess;
import org.xtuml.masl.cppgen.BlankLine;
import org.xtuml.masl.cppgen.CodeBlock;
import org.xtuml.masl.cppgen.Expression;
import org.xtuml.masl.cppgen.Function;
import org.xtuml.masl.cppgen.Literal;
import org.xtuml.masl.cppgen.Std;
import org.xtuml.masl.cppgen.TypeUsage;
import org.xtuml.masl.cppgen.Variable;
import org.xtuml.masl.metamodel.relationship.RelationshipDeclaration;
import org.xtuml.masl.translate.sql.main.PreparedStatement;
import org.xtuml.masl.translate.sql.main.TenaryRelationshipToTableTranslator;


public class SqliteTenaryRelationshipToTableTranslator
    implements TenaryRelationshipToTableTranslator
{

  private final RelationshipDeclaration relationshipDecl;

  public SqliteTenaryRelationshipToTableTranslator ( final RelationshipDeclaration relationshipDeclaration )
  {
    this.relationshipDecl = relationshipDeclaration;
  }

  @Override
  public String getLeftColumnName ()
  {
    return relationshipDecl.getName() + "_lhs";
  }

  @Override
  public String getRightColumnName ()
  {
    return relationshipDecl.getName() + "_rhs";
  }

  @Override
  public String getAssocColumnName ()
  {
    return relationshipDecl.getName() + "_ass";
  }

  @Override
  public String getTableName ()
  {
    return relationshipDecl.getDomain().getName() + "_" + relationshipDecl.getName() + "_LINK_TABLE";
  }

  @Override
  public String getCreateTableStatement ()
  {
    final StringBuilder createTableStatement = new StringBuilder();
    createTableStatement.append("CREATE TABLE " + getTableName() + " (");
    createTableStatement.append(getLeftColumnName() + " INTEGER, ");
    createTableStatement.append(getRightColumnName() + " INTEGER, ");
    createTableStatement.append(getAssocColumnName() + " INTEGER, ");
    createTableStatement.append("PRIMARY KEY (" + getLeftColumnName()
                                + ","
                                + getRightColumnName()
                                + ","
                                + getAssocColumnName()
                                + ")");
    createTableStatement.append(");\n");
    return createTableStatement.toString();
  }

  @Override
  public void createRowCountQuery ( final String className, final Function implFunction )
  {
    SqliteSQL.createRowCountQuery(className, implFunction, getTableName());
  }

  @Override
  public PreparedStatement createPreparedStatement ( final PreparedStatement.PreparedStatementType classification )
  {
    return new SqliteTenaryRelationshipPreparedStatement(this, classification);
  }

  @Override
  public void addLoadAllBody ( final Function loadAllFn, final Variable cachedTenaryContVar )
  {
    // Create cpp line:
    // ::std::string query;
    final Expression queryText = Literal.createStringLiteral("SELECT " + getLeftColumnName()
                                                             + ","
                                                             + getRightColumnName()
                                                             + ","
                                                             + getAssocColumnName()
                                                             + " FROM "
                                                             + getTableName()
                                                             + ";");
    final Variable queryVar = new Variable(new TypeUsage(Std.string, TypeUsage.Const), "query", new Expression[]
      { queryText });
    loadAllFn.getCode().appendStatement(queryVar.asStatement());

    loadAllFn.getCode().appendStatement(new BlankLine(0));

    final int expectedColumnCount = 3;

    final Expression methodName = Literal.createStringLiteral(relationshipDecl.getName() + "::" + loadAllFn.getName());
    final Variable sqlite3Stmt = new Variable(new TypeUsage(SqliteDatabase.sqlite3StmtClass, TypeUsage.Pointer),
                                              "ppStmt",
                                              new Literal("0"));
    final CodeBlock innerCodeBlock = SqliteSQL.addDbQueryCodeBlock(sqlite3Stmt,
                                                                   queryVar.asExpression(),
                                                                   methodName,
                                                                   loadAllFn.getCode(),
                                                                   expectedColumnCount);

    final Variable lhsColumnValueVar = SqliteSQL.addReadBinaryIntColumn(sqlite3Stmt, innerCodeBlock, "lhsColumnValue", 0);
    final Variable rhsColumnValueVar = SqliteSQL.addReadBinaryIntColumn(sqlite3Stmt, innerCodeBlock, "rhsColumnValue", 1);
    final Variable assColumnValueVar = SqliteSQL.addReadBinaryIntColumn(sqlite3Stmt, innerCodeBlock, "assColumnValue", 2);
    innerCodeBlock.appendStatement(new BlankLine(0));

    addCachedSetLinkBlock(innerCodeBlock, cachedTenaryContVar, lhsColumnValueVar, rhsColumnValueVar, assColumnValueVar);
  }

  @Override
  public void addLoadLhsBody ( final Function loadLhsFn, final Variable rhsIdentityVar, final Variable cachedTenaryContVar )
  {
    // Create cpp line:
    // ::std::ostringstream query;
    final Variable queryVar = new Variable(new TypeUsage(Std.ostringstream), "query");
    loadLhsFn.getCode().appendStatement(queryVar.asStatement());

    // Create cpp line:
    // ::std::ostringstream query;
    final Expression streamExpr = formQueryStreamExpr(queryVar,
                                                      getLeftColumnName(),
                                                      getAssocColumnName(),
                                                      getRightColumnName(),
                                                      rhsIdentityVar);
    loadLhsFn.getCode().appendExpression(streamExpr);

    loadLhsFn.getCode().appendStatement(new BlankLine(0));
    loadLhsFn.getCode().appendStatement(new BlankLine(0));

    final int expectedColumnCount = 2;
    final Expression queryStringExpr = new Function("str").asFunctionCall(queryVar.asExpression(), false);
    final Expression methodName = Literal.createStringLiteral(relationshipDecl.getName() + "::" + loadLhsFn.getName());
    final Variable sqlite3Stmt = new Variable(new TypeUsage(SqliteDatabase.sqlite3StmtClass, TypeUsage.Pointer),
                                              "ppStmt",
                                              new Literal("0"));
    final CodeBlock innerCodeBlock = SqliteSQL.addDbQueryCodeBlock(sqlite3Stmt,
                                                                   queryStringExpr,
                                                                   methodName,
                                                                   loadLhsFn.getCode(),
                                                                   expectedColumnCount);

    final Variable lhsColumnValueVar = SqliteSQL.addReadBinaryIntColumn(sqlite3Stmt, innerCodeBlock, "lhsColumnValue", 0);
    final Variable assColumnValueVar = SqliteSQL.addReadBinaryIntColumn(sqlite3Stmt, innerCodeBlock, "assColumnValue", 1);

    innerCodeBlock.appendStatement(new BlankLine(0));
    addCachedSetLinkBlock(innerCodeBlock, cachedTenaryContVar, lhsColumnValueVar, rhsIdentityVar, assColumnValueVar);

  }

  @Override
  public void addLoadRhsBody ( final Function loadRhsFn, final Variable lhsIdentityVar, final Variable cachedTenaryContVar )
  {
    // Create cpp line:
    // ::std::ostringstream query;
    final Variable queryVar = new Variable(new TypeUsage(Std.ostringstream), "query");
    loadRhsFn.getCode().appendStatement(queryVar.asStatement());

    final Expression streamExpr = formQueryStreamExpr(queryVar,
                                                      getRightColumnName(),
                                                      getAssocColumnName(),
                                                      getLeftColumnName(),
                                                      lhsIdentityVar);
    loadRhsFn.getCode().appendExpression(streamExpr);

    loadRhsFn.getCode().appendStatement(new BlankLine(0));
    loadRhsFn.getCode().appendStatement(new BlankLine(0));

    final int expectedColumnCount = 2;
    final Expression queryStringExpr = new Function("str").asFunctionCall(queryVar.asExpression(), false);
    final Expression methodName = Literal.createStringLiteral(relationshipDecl.getName() + "::" + loadRhsFn.getName());
    final Variable sqlite3Stmt = new Variable(new TypeUsage(SqliteDatabase.sqlite3StmtClass, TypeUsage.Pointer),
                                              "ppStmt",
                                              new Literal("0"));
    final CodeBlock innerCodeBlock = SqliteSQL.addDbQueryCodeBlock(sqlite3Stmt,
                                                                   queryStringExpr,
                                                                   methodName,
                                                                   loadRhsFn.getCode(),
                                                                   expectedColumnCount);

    final Variable rhsColumnValueVar = SqliteSQL.addReadBinaryIntColumn(sqlite3Stmt, innerCodeBlock, "rhsColumnValue", 0);
    final Variable assColumnValueVar = SqliteSQL.addReadBinaryIntColumn(sqlite3Stmt, innerCodeBlock, "assColumnValue", 1);

    innerCodeBlock.appendStatement(new BlankLine(0));
    addCachedSetLinkBlock(innerCodeBlock, cachedTenaryContVar, lhsIdentityVar, rhsColumnValueVar, assColumnValueVar);
  }

  @Override
  public void addLoadAssBody ( final Function loadAssFn, final Variable assIdentityVar, final Variable cachedTenaryContVar )
  {
    // Create cpp line:
    // ::std::ostringstream query;
    final Variable queryVar = new Variable(new TypeUsage(Std.ostringstream), "query");
    loadAssFn.getCode().appendStatement(queryVar.asStatement());

    final Expression streamExpr = formQueryStreamExpr(queryVar,
                                                      getRightColumnName(),
                                                      getLeftColumnName(),
                                                      getAssocColumnName(),
                                                      assIdentityVar);
    loadAssFn.getCode().appendExpression(streamExpr);

    loadAssFn.getCode().appendStatement(new BlankLine(0));
    loadAssFn.getCode().appendStatement(new BlankLine(0));

    final int expectedColumnCount = 2;
    final Expression queryStringExpr = new Function("str").asFunctionCall(queryVar.asExpression(), false);
    final Expression methodName = Literal.createStringLiteral(relationshipDecl.getName() + "::" + loadAssFn.getName());
    final Variable sqlite3Stmt = new Variable(new TypeUsage(SqliteDatabase.sqlite3StmtClass, TypeUsage.Pointer),
                                              "ppStmt",
                                              new Literal("0"));
    final CodeBlock innerCodeBlock = SqliteSQL.addDbQueryCodeBlock(sqlite3Stmt,
                                                                   queryStringExpr,
                                                                   methodName,
                                                                   loadAssFn.getCode(),
                                                                   expectedColumnCount);

    final Variable rhsColumnValueVar = SqliteSQL.addReadBinaryIntColumn(sqlite3Stmt, innerCodeBlock, "rhsColumnValue", 0);
    final Variable lhsColumnValueVar = SqliteSQL.addReadBinaryIntColumn(sqlite3Stmt, innerCodeBlock, "lhsColumnValue", 1);

    innerCodeBlock.appendStatement(new BlankLine(0));

    addCachedSetLinkBlock(innerCodeBlock, cachedTenaryContVar, lhsColumnValueVar, rhsColumnValueVar, assIdentityVar);
  }

  /**
   * When generating the code to insert into the cached set of containers data
   * extracted from a row in the database, the code is always the same.
   * Therefore encapsulate this block of generated code into this helper method.
   * 

   *          insert all generated code into this codeBlock

   *          the variable currently represented the set of cached Containers

   *          the variable currently representing the lhs object Id value

   *          the variable currently representing the rhs object Id value

   *          the variable currently representing the ass object Id value
   */
  private void addCachedSetLinkBlock ( final CodeBlock codeBlock,
                                       final Variable cachedTenaryContVar,
                                       final Variable lhsColumnValueVar,
                                       final Variable rhsColumnValueVar,
                                       final Variable assColumnValueVar )
  {
    // Create cpp line:
    // cachedTenaryContainerSet.getLhsLinks()[lhsColumnValue].link(rhsColumnValue,assColumnValue);
    final Expression lhsLink = addCachedSetLinkExpression(new Function("getLhsLinks"),
                                                          cachedTenaryContVar,
                                                          lhsColumnValueVar,
                                                          rhsColumnValueVar,
                                                          assColumnValueVar);
    codeBlock.appendExpression(lhsLink);

    // Create cpp line:
    // cachedTenaryContainers.getRhsLinks()[lhsColumnValue].link(
    // lhsColumnValue, assColumnValue);
    final Expression rhsLink = addCachedSetLinkExpression(new Function("getRhsLinks"),
                                                          cachedTenaryContVar,
                                                          rhsColumnValueVar,
                                                          lhsColumnValueVar,
                                                          assColumnValueVar);
    codeBlock.appendExpression(rhsLink);

    // Create cpp line:
    // cachedTenaryContainers.getAssLinks()[rhsColumnValue].link(
    // lhsColumnValue, rhsColumnValue);
    final Expression assLink = addCachedSetLinkExpression(new Function("getAssLinks"),
                                                          cachedTenaryContVar,
                                                          assColumnValueVar,
                                                          lhsColumnValueVar,
                                                          rhsColumnValueVar);
    codeBlock.appendExpression(assLink);
  }

  /**
   * Generate a block of code that enables the specified link operation to be
   * invoked on one of the cached set of containers.An example is shown below
   * 
   * cachedTenaryContainerSet.getLhsLinks()[lhsColumnValue].link(rhsColumnValue,
   * assColumnValue);
   * 

   *          the get rhs/lhs to rhs/lhs links function to call

   *          the variable holding the cached container set

   *          the index to use into the returned cached container.

   *          the first value to be passed to the link function invoked.

   *          the seocnd value to be passed to the link function invoked.
   * @return
   */
  private Expression addCachedSetLinkExpression ( final Function getLinksFn,
                                                  final Variable cachedContVar,
                                                  final Variable arrayIndexVar,
                                                  final Variable firstValueVar,
                                                  final Variable secondValueVar )
  {
    // Create cpp line:
    // cachedTenaryContainerSet.getRhsToLhsLinks()[rhsColumnValue].link(lhsColumnValue);
    final Expression getLinksFnCall = getLinksFn.asFunctionCall(cachedContVar.asExpression(), false);
    final Expression linksArrayAccess = new ArrayAccess(getLinksFnCall, arrayIndexVar.asExpression());
    final Expression lhsToRhsLink = new Function("link").asFunctionCall(linksArrayAccess,
                                                                        false,
                                                                        firstValueVar.asExpression(),
                                                                        secondValueVar.asExpression());
    return lhsToRhsLink;
  }

  /**
   * Generate the required SQL SELECT statement
   * 

   *          the variable to store the select statement

   *          the column name to use as the first column name

   *          the column name to use as the second column name

   *          the column name to use in the where clause

   *          the value to use in the where clause
   * @return an expression representing the required SQL SELECT statement.
   */
  private Expression formQueryStreamExpr ( final Variable queryVar,
                                           final String selectColumn1,
                                           final String selectColumn2,
                                           final String whereColumnName,
                                           final Variable whereColumnVar )
  {
    final List<Expression> streamQueryExprList = new ArrayList<Expression>();
    streamQueryExprList.add(queryVar.asExpression());
    streamQueryExprList.add(Literal.createStringLiteral("SELECT " + selectColumn1 + "," + selectColumn2 + " FROM " + getTableName()));
    streamQueryExprList.add(Literal.createStringLiteral(" WHERE " + whereColumnName + " = "));
    streamQueryExprList.add(whereColumnVar.asExpression());
    streamQueryExprList.add(Literal.createStringLiteral(";"));
    final Expression streamExpr = Std.ostreamExpression(streamQueryExprList);
    return streamExpr;
  }

}
