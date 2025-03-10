/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.translate.sql.sqlite;

import org.xtuml.masl.cppgen.*;
import org.xtuml.masl.metamodel.relationship.RelationshipDeclaration;
import org.xtuml.masl.translate.sql.main.BinaryRelationshipToTableTranslator;
import org.xtuml.masl.translate.sql.main.PreparedStatement;

import java.util.ArrayList;
import java.util.List;

public class SqliteBinaryRelationshipToTableTranslator implements BinaryRelationshipToTableTranslator {

    private final RelationshipDeclaration relationshipDecl;

    public SqliteBinaryRelationshipToTableTranslator(final RelationshipDeclaration relationshipDeclaration) {
        this.relationshipDecl = relationshipDeclaration;
    }

    @Override
    public String getTableName() {
        return relationshipDecl.getDomain().getName() + "_" + relationshipDecl.getName() + "_LINK_TABLE";
    }

    @Override
    public String getLeftColumnName() {
        return relationshipDecl.getName() + "_lhs";
    }

    @Override
    public String getRightColumnName() {
        return relationshipDecl.getName() + "_rhs";
    }

    @Override
    public String getCreateTableStatement() {
        // Define the SQL for the link table.
        String
                createTableStatement =
                "CREATE TABLE " +
                getTableName() +
                " (" +
                getLeftColumnName() +
                " INTEGER, " +
                getRightColumnName() +
                " INTEGER, " +
                "PRIMARY KEY (" +
                getLeftColumnName() +
                "," +
                getRightColumnName() +
                ")" +
                ");\n";
        return createTableStatement;
    }

    @Override
    public void createRowCountQuery(final String className, final Function implFunction) {
        SqliteSQL.createRowCountQuery(className, implFunction, getTableName());
    }

    @Override
    public PreparedStatement createPreparedStatement(final PreparedStatement.PreparedStatementType classification) {
        return new SqliteBinaryRelationshipPreparedStatement(this, classification);
    }

    @Override
    public void addLoadAllBody(final Function loadAllFn,
                               final Variable lhsToRhsLinkSet,
                               final Variable rhsToLhsLinkSet) {
        // Create cpp line:
        // ::std::string query;
        final Expression
                queryText =
                Literal.createStringLiteral("SELECT " +
                                            getLeftColumnName() +
                                            "," +
                                            getRightColumnName() +
                                            " FROM " +
                                            getTableName() +
                                            ";");
        final Variable
                queryVar =
                new Variable(new TypeUsage(Std.string, TypeUsage.Const), "query", new Expression[]{queryText});
        loadAllFn.getCode().appendStatement(queryVar.asStatement());
        loadAllFn.getCode().appendStatement(new BlankLine(0));

        final int expectedColumnCount = 2;
        final Variable
                sqlite3Stmt =
                new Variable(new TypeUsage(SqliteDatabase.sqlite3StmtClass, TypeUsage.Pointer),
                             "ppStmt",
                             new Literal("0"));
        final Expression
                methodName =
                Literal.createStringLiteral(relationshipDecl.getName() + "::" + loadAllFn.getName());
        final CodeBlock
                innerCodeBlock =
                SqliteSQL.addDbQueryCodeBlock(sqlite3Stmt,
                                              queryVar.asExpression(),
                                              methodName,
                                              loadAllFn.getCode(),
                                              expectedColumnCount);

        final Variable
                lhsColumnValueVar =
                SqliteSQL.addReadBinaryIntColumn(sqlite3Stmt, innerCodeBlock, "lhsColumnValue", 0);
        final Variable
                rhsColumnValueVar =
                SqliteSQL.addReadBinaryIntColumn(sqlite3Stmt, innerCodeBlock, "rhsColumnValue", 1);

        // Create cpp line:
        // lhsToRhsLinkSet[lhsColumnValue].link(rhsColumnValue);
        final Expression
                oneLinkSetAccess =
                new ArrayAccess(lhsToRhsLinkSet.asExpression(), lhsColumnValueVar.asExpression());
        final Expression
                lhsToRhsLinkFnCall =
                new Function("link").asFunctionCall(oneLinkSetAccess, false, rhsColumnValueVar.asExpression());
        innerCodeBlock.appendExpression(lhsToRhsLinkFnCall);

        // Create cpp line:
        // rhsToLhsLinkSet[rhsColumnValue].link(lhsColumnValue);
        final ArrayAccess
                manyLinkSetAccess =
                new ArrayAccess(rhsToLhsLinkSet.asExpression(), rhsColumnValueVar.asExpression());
        final Expression
                rhsToLhsLinkFnCall =
                new Function("link").asFunctionCall(manyLinkSetAccess, false, lhsColumnValueVar.asExpression());
        innerCodeBlock.appendExpression(rhsToLhsLinkFnCall);

    }

    @Override
    public void addLoadLhsBody(final Function loadLhsFn,
                               final Variable identityVar,
                               final Variable lhsToRhsLinkSet,
                               final Variable rhsToLhsLinkSet) {
        // Create cpp line:
        // ::std::ostringstream query;
        final Variable queryVar = new Variable(new TypeUsage(Std.ostringstream), "query");
        loadLhsFn.getCode().appendStatement(queryVar.asStatement());

        // Create cpp line:
        // query << "SELECT R5_lhs FROM R5_LINK_TABLE WHERE R5_rhs = " <<
        // rhsIdentity << ";";
        final Expression streamExpr = generateLoadLhsSelectSQL(queryVar, identityVar);
        loadLhsFn.getCode().appendExpression(streamExpr);
        loadLhsFn.getCode().appendStatement(new BlankLine(0));

        final int expectedColumnCount = 1;
        final Variable
                sqlite3Stmt =
                new Variable(new TypeUsage(SqliteDatabase.sqlite3StmtClass, TypeUsage.Pointer),
                             "ppStmt",
                             new Literal("0"));
        final Expression queryStringExpr = new Function("str").asFunctionCall(queryVar.asExpression(), false);
        final Expression
                methodName =
                Literal.createStringLiteral(relationshipDecl.getName() + "::" + loadLhsFn.getName());
        final CodeBlock
                innerCodeBlock =
                SqliteSQL.addDbQueryCodeBlock(sqlite3Stmt,
                                              queryStringExpr,
                                              methodName,
                                              loadLhsFn.getCode(),
                                              expectedColumnCount);

        final Variable
                lhsColumnValueVar =
                SqliteSQL.addReadBinaryIntColumn(sqlite3Stmt, innerCodeBlock, "lhsColumnValue", 0);

        // Create cpp line:
        // rhsToLhsLinkSet[identity].link(R4_lhsColumn)
        final Expression
                rhsToLhsLinkSetAccess =
                new ArrayAccess(rhsToLhsLinkSet.asExpression(), identityVar.asExpression());
        final Expression
                rhsToLhsLinkFnCall =
                new Function("link").asFunctionCall(rhsToLhsLinkSetAccess, false, lhsColumnValueVar.asExpression());
        innerCodeBlock.appendExpression(rhsToLhsLinkFnCall);

        // Create cpp line:
        // lhsToRhsLinkSet[R4_lhsColumn].link(identity);
        final Expression
                lhsToRhsLinkSetAccess =
                new ArrayAccess(lhsToRhsLinkSet.asExpression(), lhsColumnValueVar.asExpression());
        final Expression
                insertFnCall =
                new Function("link").asFunctionCall(lhsToRhsLinkSetAccess, false, identityVar.asExpression());
        innerCodeBlock.appendExpression(insertFnCall);

    }

    @Override
    public void addLoadRhsBody(final Function loadRhsFn,
                               final Variable identityVar,
                               final Variable lhsToRhsLinkSet,
                               final Variable rhsToLhsLinkSet) {
        // Create cpp line:
        // ::std::ostringstream query;
        final Variable queryVar = new Variable(new TypeUsage(Std.ostringstream), "query");
        loadRhsFn.getCode().appendStatement(queryVar.asStatement());

        // Create cpp line:
        // query << "SELECT R5_rhs FROM R5_LINK_TABLE WHERE R5_lhs = " << identity
        // << ";";
        final Expression streamExpr = generateLoadRhsSelectSQL(queryVar, identityVar);
        loadRhsFn.getCode().appendExpression(streamExpr);
        loadRhsFn.getCode().appendStatement(new BlankLine(0));

        final int expectedColumnCount = 1;
        final Expression queryStringExpr = new Function("str").asFunctionCall(queryVar.asExpression(), false);
        final Variable
                sqlite3Stmt =
                new Variable(new TypeUsage(SqliteDatabase.sqlite3StmtClass, TypeUsage.Pointer),
                             "ppStmt",
                             new Literal("0"));
        final Expression
                methodName =
                Literal.createStringLiteral(relationshipDecl.getName() + "::" + loadRhsFn.getName());
        final CodeBlock
                innerCodeBlock =
                SqliteSQL.addDbQueryCodeBlock(sqlite3Stmt,
                                              queryStringExpr,
                                              methodName,
                                              loadRhsFn.getCode(),
                                              expectedColumnCount);

        final Variable
                rhsColumnValueVar =
                SqliteSQL.addReadBinaryIntColumn(sqlite3Stmt, innerCodeBlock, "rhsColumnValue", 0);

        // Create cpp line:
        // lhsToRhsLinkSet[lhsIdentity].link(R4_rhsColumn);
        final ArrayAccess
                rhsLinkSetAccess =
                new ArrayAccess(lhsToRhsLinkSet.asExpression(), identityVar.asExpression());
        final Expression
                lhsToRhsLinkFnCall =
                new Function("link").asFunctionCall(rhsLinkSetAccess, false, rhsColumnValueVar.asExpression());
        innerCodeBlock.appendExpression(lhsToRhsLinkFnCall);

        // Create cpp line:
        // rhsToLhsLinkSet[R4_rhsColumn].link(identity);
        final ArrayAccess
                lhsLinkSetAccess =
                new ArrayAccess(rhsToLhsLinkSet.asExpression(), rhsColumnValueVar.asExpression());
        final Expression
                insertFnCall =
                new Function("link").asFunctionCall(lhsLinkSetAccess, false, identityVar.asExpression());
        innerCodeBlock.appendExpression(insertFnCall);

    }

    /**
     * Generate the SQL SELECT statement for loading the lhs column data based on a
     * where clause using the value of a rhs object id.
     * <p>
     * <p>
     * the variable to assign the SQL SELECT statement to
     * <p>
     * the variable that holds the value for the WHERE clause
     *
     * @return an expression that generates an SQL SELECT statement for loading lhs
     * column data
     */
    private Expression generateLoadLhsSelectSQL(final Variable queryVar, final Variable identityVar) {
        // Create cpp line:
        // query << "SELECT R5_lhs FROM R5_LINK_TABLE WHERE R5_rhs = " <<
        // rhsIdentity << ";";
        final List<Expression> streamExprList = new ArrayList<>();
        streamExprList.add(queryVar.asExpression());
        streamExprList.add(Literal.createStringLiteral("SELECT " +
                                                       getLeftColumnName() +
                                                       " FROM " +
                                                       getTableName() +
                                                       " WHERE " +
                                                       getRightColumnName() +
                                                       " = "));
        streamExprList.add(identityVar.asExpression());
        streamExprList.add(Literal.createStringLiteral(";"));
        final Expression streamExpr = Std.ostreamExpression(streamExprList);
        return streamExpr;
    }

    /**
     * Generate the SQL SELECT statement for loading the rhs column data based on a
     * where clause using the value of a lhs object id.
     * <p>
     * <p>
     * the variable to assign the SQL SELECT statement to
     * <p>
     * the variable that holds the value for the WHERE clause
     *
     * @return an expression that generates an SQL SELECT statement for loading rhs
     * column data
     */
    private Expression generateLoadRhsSelectSQL(final Variable queryVar, final Variable identityVar) {
        final List<Expression> streamExprList = new ArrayList<>();
        streamExprList.add(queryVar.asExpression());
        streamExprList.add(Literal.createStringLiteral("SELECT " +
                                                       getRightColumnName() +
                                                       " FROM " +
                                                       getTableName() +
                                                       " WHERE " +
                                                       getLeftColumnName() +
                                                       " = "));
        streamExprList.add(identityVar.asExpression());
        streamExprList.add(Literal.createStringLiteral(";"));
        final Expression streamExpr = Std.ostreamExpression(streamExprList);
        return streamExpr;
    }

}
