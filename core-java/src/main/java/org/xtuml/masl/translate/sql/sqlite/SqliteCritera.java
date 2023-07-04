/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 ----------------------------------------------------------------------------
 Classification: UK OFFICIAL
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.translate.sql.sqlite;

import org.xtuml.masl.cppgen.Class;
import org.xtuml.masl.cppgen.*;
import org.xtuml.masl.metamodel.object.AttributeDeclaration;
import org.xtuml.masl.metamodel.object.ObjectDeclaration;
import org.xtuml.masl.metamodel.object.ReferentialAttributeDefinition;
import org.xtuml.masl.metamodel.relationship.*;
import org.xtuml.masl.metamodel.type.BasicType;
import org.xtuml.masl.metamodel.type.EnumerateType;
import org.xtuml.masl.translate.sql.main.Database;
import org.xtuml.masl.translate.sql.main.DatabaseTraits;

import java.util.*;

public class SqliteCritera implements DatabaseTraits.SqlCritera {

    private final Variable variable;
    private final Map<String, org.xtuml.masl.cppgen.Expression> fromClauseExpression;
    private final List<org.xtuml.masl.cppgen.Expression> whereClauseExpression;
    private final List<org.xtuml.masl.cppgen.Expression> linkTableClauseExpression;
    private final Map<ObjectDeclaration, Variable> objectSqlVars;
    private final Map<RelationshipDeclaration, Variable> relationshipSqlVars;

    private final Class criteriaClass = Database.criteriaClass;

    private final ObjectDeclaration sourceObject;

    @Override
    public Variable getVariable() {
        return variable;
    }

    public SqliteCritera(final ObjectDeclaration objectDecl, final String variableName) {
        sourceObject = objectDecl;
        variable = new Variable(new TypeUsage(criteriaClass), variableName);
        whereClauseExpression = new ArrayList<>();
        fromClauseExpression = new LinkedHashMap<>();
        linkTableClauseExpression = new ArrayList<>();
        objectSqlVars = new LinkedHashMap<>();
        relationshipSqlVars = new LinkedHashMap<>();
    }

    @Override
    public boolean isEmpty() {
        return whereClauseExpression.size() == 0;
    }

    @Override
    public void beginCondition() {
        if (whereClauseExpression.size() == 0) {
            whereClauseExpression.add(Std.string.callConstructor(Literal.createStringLiteral("(")));
        } else {
            whereClauseExpression.add(Literal.createStringLiteral("("));
        }
    }

    @Override
    public Collection<ObjectDeclaration> getDependentObjects() {
        return objectSqlVars.keySet();
    }

    @Override
    public Collection<RelationshipDeclaration> getDependentRelationship() {
        return relationshipSqlVars.keySet();
    }

    @Override
    public void endCondition() {
        final Literal bracketLiteral = Literal.createStringLiteral(")");
        whereClauseExpression.add(bracketLiteral);
    }

    @Override
    public void addOperator(final org.xtuml.masl.metamodel.expression.BinaryExpression.Operator operator) {
        final Literal bracketLiteral = Literal.createStringLiteral(SqliteBinaryOperator.maslToSqlOperator(operator));
        whereClauseExpression.add(bracketLiteral);
    }

    @Override
    public void addOperator(final org.xtuml.masl.metamodel.expression.UnaryExpression.Operator operator) {
        final Literal bracketLiteral = Literal.createStringLiteral(SqliteUnaryOperator.maslToSqlOperator(operator));
        whereClauseExpression.add(bracketLiteral);
    }

    @Override
    public void addAttributeNameOperand(final AttributeDeclaration attribute) {
        if (!fromClauseExpression.containsKey(attribute.getParentObject())) {
            fromClauseExpression.put(attribute.getParentObject().getName(),
                                     getObjTableName(attribute.getParentObject()));
        }

        // Create cpp line:
        // sqlGenerator->getColumnName("masla_attribute_1")
        final Variable sqlGenVar = new Variable("sqlGenerator");
        final Function getColNameFn = new Function("getColumnName");
        final Literal attributeName = Literal.createStringLiteral(attribute.getName());
        final Expression getColNameFnCall = getColNameFn.asFunctionCall(sqlGenVar.asExpression(), true, attributeName);
        whereClauseExpression.add(getColNameFnCall);
    }

    @Override
    public void addRefAttributeNameOperand(final AttributeDeclaration attribute) {
        if (attribute.isIdentifier()) {
            // This is a preferred referential attribute. This
            // will have its own column in the source objects
            // database table. Can therefore treat it like any normal
            // attribute access.
            addAttributeNameOperand(attribute);
        } else {
            // For non-preferred referential attributes, the attribute values
            // are not stored in the source table, but are stored in the table
            // at the other end of the relationship. Therefore need to use the
            // link table as part of the select statement to find the instances
            // that take part in the association.
            if (!fromClauseExpression.containsKey(attribute.getParentObject().getName())) {
                fromClauseExpression.put(attribute.getParentObject().getName(),
                                         getObjTableName(attribute.getParentObject()));
            }

            for (final ReferentialAttributeDefinition refAttribute : attribute.getRefAttDefs()) {
                final AttributeDeclaration attributeDecl = refAttribute.getDestinationAttribute();
                final RelationshipSpecification relationshipSpec = refAttribute.getRelationship();

                if (!fromClauseExpression.containsKey(attributeDecl.getParentObject().getName())) {
                    fromClauseExpression.put(attributeDecl.getParentObject().getName(),
                                             getObjTableName(attributeDecl.getParentObject()));
                }

                whereClauseExpression.add(getQualifiedColumnName(getObjTableName(attributeDecl.getParentObject()),
                                                                 getObjColumnName(attributeDecl)));

                final RelationshipDeclaration relationshipDecl = relationshipSpec.getRelationship();
                if (!relationshipSqlVars.containsKey(relationshipDecl)) {

                    if (!fromClauseExpression.containsKey(relationshipDecl.getName())) {
                        fromClauseExpression.put(relationshipDecl.getName(), getRelTableName(relationshipDecl));
                    }

                    // Add INNER JOINS to the where clause to relate the values in
                    // the Relationship link table to the objects that participate
                    // in the relationship.
                    if (relationshipDecl instanceof NormalRelationshipDeclaration binaryRelationship) {
                        final ObjectDeclaration lhsObjectDecl = binaryRelationship.getLeftObject();
                        final ObjectDeclaration rhsObjectDecl = binaryRelationship.getRightObject();

                        final Expression assignmentLiteralExpr = Literal.createStringLiteral(" = ");
                        final Expression
                                andLiteralExpr =
                                Std.string.callConstructor(Literal.createStringLiteral(" AND "));
                        final Expression
                                lhsObjColumnNameExpr =
                                getQualifiedColumnName(getObjTableName(lhsObjectDecl),
                                                       getArchColumnName(lhsObjectDecl));
                        final Expression
                                rhsObjColumnNameExpr =
                                getQualifiedColumnName(getObjTableName(rhsObjectDecl),
                                                       getArchColumnName(rhsObjectDecl));
                        final Expression
                                lhsRelationshipColumnExpr =
                                getQualifiedColumnName(getRelTableName(binaryRelationship),
                                                       getRelLhsColumnName(relationshipDecl));
                        final Expression
                                rhsRelationshipColumnExpr =
                                getQualifiedColumnName(getRelTableName(binaryRelationship),
                                                       getRelRhsColumnName(relationshipDecl));

                        final Expression
                                lhslinkTableWhereExpr =
                                concatenateWhereClause(Arrays.asList(andLiteralExpr,
                                                                     lhsObjColumnNameExpr,
                                                                     assignmentLiteralExpr,
                                                                     lhsRelationshipColumnExpr));
                        final Expression
                                rhslinkTableWhereExpr =
                                concatenateWhereClause(Arrays.asList(andLiteralExpr,
                                                                     rhsObjColumnNameExpr,
                                                                     assignmentLiteralExpr,
                                                                     rhsRelationshipColumnExpr));

                        linkTableClauseExpression.add(lhslinkTableWhereExpr);
                        linkTableClauseExpression.add(rhslinkTableWhereExpr);
                    } else if (relationshipDecl instanceof AssociativeRelationshipDeclaration) {

                    } else if (relationshipDecl instanceof SubtypeRelationshipDeclaration) {

                    }
                }
            }
        }
    }

    private Expression getObjColumnName(final AttributeDeclaration attributeDecl) {
        final Literal attributeNameLiteral = Literal.createStringLiteral(attributeDecl.getName());
        final Variable ObjectSqlVar = getObjectSqlVariable(attributeDecl.getParentObject());
        final Expression
                getColumnNameFnCall =
                new Function("getColumnName").asFunctionCall(ObjectSqlVar.asExpression(), false, attributeNameLiteral);
        return getColumnNameFnCall;
    }

    private Variable getObjectSqlVariable(final ObjectDeclaration objectDecl) {
        if (!objectSqlVars.containsKey(objectDecl)) {
            final Literal objectNameLiteral = Literal.createStringLiteral(objectDecl.getName());
            final Literal domainNameLiteral = Literal.createStringLiteral(objectDecl.getDomain().getName());

            final Expression repositoryInstFnCall = Database.objectSqlRepository.callStaticFunction("getInstance");
            final Expression
                    getObjectSqlFnCall =
                    new Function("getObjectSql").asFunctionCall(repositoryInstFnCall,
                                                                false,
                                                                domainNameLiteral,
                                                                objectNameLiteral);
            final Variable
                    ObjectSqlVar =
                    new Variable(new TypeUsage(Database.objectSql).getConstReferenceType(),
                                 objectDecl.getName() + "Sql",
                                 getObjectSqlFnCall);
            objectSqlVars.put(objectDecl, ObjectSqlVar);
        }
        return objectSqlVars.get(objectDecl);
    }

    private Variable getRelationshipSqlVariable(final RelationshipDeclaration relationshipDecl) {
        if (!relationshipSqlVars.containsKey(relationshipDecl)) {

            final Literal relNameLiteral = Literal.createStringLiteral(relationshipDecl.getName());
            final Literal domainNameLiteral = Literal.createStringLiteral(relationshipDecl.getDomain().getName());

            final Expression
                    repositoryInstFnCall =
                    Database.relationshipSqlRepository.callStaticFunction("getInstance");
            final Expression
                    getRelationsipSqlFnCall =
                    new Function("getRelationshipSql").asFunctionCall(repositoryInstFnCall,
                                                                      false,
                                                                      domainNameLiteral,
                                                                      relNameLiteral);

            final Variable
                    relationshipSqlVar =
                    new Variable(new TypeUsage(Database.relationshipSql).getConstReferenceType(),
                                 relationshipDecl.getName() + "Sql",
                                 getRelationsipSqlFnCall);
            relationshipSqlVars.put(relationshipDecl, relationshipSqlVar);
        }
        return relationshipSqlVars.get(relationshipDecl);
    }

    private Expression getArchColumnName(final ObjectDeclaration objectDecl) {
        final Literal attributeNameLiteral = Literal.createStringLiteral("architecture_id");
        final Variable ObjectSqlVar = getObjectSqlVariable(objectDecl);
        final Expression
                getColumnNameFnCall =
                new Function("getColumnName").asFunctionCall(ObjectSqlVar.asExpression(), false, attributeNameLiteral);
        return getColumnNameFnCall;
    }

    private Expression getRelLhsColumnName(final RelationshipDeclaration relationshipDecl) {
        final Variable relationshipSqlVar = getRelationshipSqlVariable(relationshipDecl);
        final Expression
                getRelNameFnCall =
                new Function("getLhsColumnName").asFunctionCall(relationshipSqlVar.asExpression(), false);
        return getRelNameFnCall;
    }

    private Expression getRelRhsColumnName(final RelationshipDeclaration relationshipDecl) {
        final Variable relationshipSqlVar = getRelationshipSqlVariable(relationshipDecl);
        final Expression
                getRelNameFnCall =
                new Function("getRhsColumnName").asFunctionCall(relationshipSqlVar.asExpression(), false);
        return getRelNameFnCall;
    }

    private Expression getRelTableName(final RelationshipDeclaration relationshipDecl) {
        final Variable relationshipSqlVar = getRelationshipSqlVariable(relationshipDecl);
        final Expression
                getRelNameFnCall =
                new Function("getTableName").asFunctionCall(relationshipSqlVar.asExpression(), false);
        return getRelNameFnCall;
    }

    private Expression getObjTableName(final ObjectDeclaration objectDecl) {
        final Variable ObjectSqlVar = getObjectSqlVariable(objectDecl);
        final Expression
                getTableNameFnCall =
                new Function("getTableName").asFunctionCall(ObjectSqlVar.asExpression(), false);
        return getTableNameFnCall;
    }

    private Expression getQualifiedColumnName(final Expression tableNameExpr, final Expression columnName) {
        final List<Expression> sqlColumnNameElements = new ArrayList<>();
        sqlColumnNameElements.add(tableNameExpr);
        sqlColumnNameElements.add(Literal.createStringLiteral("."));
        sqlColumnNameElements.add(columnName);
        final Expression sqlColumnNameExpr = concatenateWhereClause(sqlColumnNameElements);
        return sqlColumnNameExpr;
    }

    @Override
    public void addParameterOperand(final BasicType paramType, final String parameter) {
        final Function convertToColumnFn = org.xtuml.masl.translate.sql.main.Database.getConvertToColumnValueFn();
        Expression convertToColumnFnCall = convertToColumnFn.asFunctionCall(new Variable(parameter).asExpression());
        if (paramType.getDefinedType() instanceof EnumerateType) {
            convertToColumnFnCall =
                    convertToColumnFn.asFunctionCall(new Function("getText").asFunctionCall(new Variable(parameter).asExpression(),
                                                                                            false));
        }
        whereClauseExpression.add(convertToColumnFnCall);
    }

    @Override
    public Expression concatenateWhereClause(final List<Expression> expression) {
        // The expressions contained in the whereClause List needs to be translated
        // into a string constructor call with all the expressions added together
        // to give something like:
        // ::std::string whereClause = ::std::string( "(" ) +
        // sqlGenerator->getColumnName( "masla_attribute_1" ) + "=" +
        // convertToColumnValue( p1 ) + ")";
        // To form this kind of statement need to chain the expressions into
        // BinaryExpressions. Therefore
        // recurse using this function to form the chained BinaryExpressions
        if (expression.size() == 1) {
            // exit condition from this recursive method.
            return expression.get(0);
        } else {
            final List<Expression> subExpressionList = expression.subList(0, expression.size() - 1);
            final Expression tailExpr = expression.get(expression.size() - 1);
            return new BinaryExpression(concatenateWhereClause(subExpressionList), BinaryOperator.PLUS, tailExpr);
        }
    }

    @Override
    public void appendStatements(final CodeBlock block) {
        // Create line:
        // Criteria sqlCriteria;
        block.appendStatement(variable.asStatement());
        block.appendStatement(new BlankLine(0));

        if (whereClauseExpression.size() > 0) {

            for (final Variable objectSqlVar : objectSqlVars.values()) {
                block.appendStatement(objectSqlVar.asStatement());
            }

            for (final Variable relationshipSqlVar : relationshipSqlVars.values()) {
                block.appendStatement(relationshipSqlVar.asStatement());
            }
            block.appendStatement(new BlankLine(0));

            for (final Expression fromExpr : fromClauseExpression.values()) {
                final Function addFromClauseFn = new Function("addFromClause");
                final Expression
                        addFromClauseExpr =
                        addFromClauseFn.asFunctionCall(variable.asExpression(), false, fromExpr);
                block.appendStatement(new ExpressionStatement(addFromClauseExpr));
            }

            block.appendStatement(new BlankLine(0));
            final Variable whereClause = new Variable(new TypeUsage(Std.string), "whereClause");
            final Expression whereExpr = concatenateWhereClause(whereClauseExpression);
            final BinaryExpression
                    initialiseToClause =
                    new BinaryExpression(whereClause.asExpression(), BinaryOperator.PLUS_ASSIGN, whereExpr);

            // Create line:
            // ::std::string whereClause;
            // whereClause += ::std::string( "(" ) + sqlGenerator->getColumnName(
            // "masla_attribute_1" ) + "=" + convertToColumnValue( p1 ) + ")";
            block.appendStatement(whereClause.asStatement());
            block.appendExpression(initialiseToClause);

            // When a referential attribute is accessed, the Where clause needs
            // to contain INNER JOINS to relate the information stored in the
            // link table with the objects that take part in the relationship.
            for (final Expression linkTableExpr : linkTableClauseExpression) {
                final BinaryExpression
                        linkTableClause =
                        new BinaryExpression(whereClause.asExpression(), BinaryOperator.PLUS_ASSIGN, linkTableExpr);
                block.appendExpression(linkTableClause);
            }

            block.appendStatement(new BlankLine(0));

            // Create line:
            // sqlCriteria.addAllColumns( Find_Object_Test_HSql.getTableName() );
            final Expression
                    addAllColumnsFnCall =
                    new Function("addAllColumns").asFunctionCall(variable.asExpression(),
                                                                 false,
                                                                 getObjTableName(sourceObject));
            block.appendExpression(addAllColumnsFnCall);

            // Create line:
            // sqlCriteria.addWhereClause( whereClause );
            final Function addWhereFn = new Function("addWhereClause");
            final Expression
                    addWhereExpr =
                    addWhereFn.asFunctionCall(variable.asExpression(), false, whereClause.asExpression());
            block.appendStatement(new ExpressionStatement(addWhereExpr));
        }
    }
}
