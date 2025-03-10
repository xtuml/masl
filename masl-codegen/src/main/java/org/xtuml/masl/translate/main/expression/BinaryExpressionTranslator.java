/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.translate.main.expression;

import org.xtuml.masl.cppgen.*;
import org.xtuml.masl.metamodel.expression.LiteralExpression;
import org.xtuml.masl.metamodel.type.CollectionType;
import org.xtuml.masl.metamodel.type.SequenceType;
import org.xtuml.masl.translate.main.Architecture;
import org.xtuml.masl.translate.main.Scope;
import org.xtuml.masl.translate.main.Types;

public class BinaryExpressionTranslator extends ExpressionTranslator {

    private final ExpressionTranslator lhs;
    private final ExpressionTranslator rhs;

    BinaryExpressionTranslator(final org.xtuml.masl.metamodel.expression.BinaryExpression maslExpression,
                               final Scope scope,
                               final org.xtuml.masl.metamodel.expression.Expression assignedTo) {
        lhs = ExpressionTranslator.createTranslator(maslExpression.getLhs(), scope);
        rhs = ExpressionTranslator.createTranslator(maslExpression.getRhs(), scope);
        Expression lhsExp = lhs.getReadExpression();
        Expression rhsExp = rhs.getReadExpression();

        switch (maslExpression.getOperator()) {
            case PLUS:
                setReadExpression(new BinaryExpression(lhsExp, BinaryOperator.PLUS, rhsExp));
                break;
            case MINUS:
                setReadExpression(new BinaryExpression(lhsExp, BinaryOperator.MINUS, rhsExp));
                break;
            case TIMES:
                setReadExpression(new BinaryExpression(lhsExp, BinaryOperator.TIMES, rhsExp));
                break;
            case DIVIDE:
                setReadExpression(new BinaryExpression(lhsExp, BinaryOperator.DIVIDE, rhsExp));
                break;
            case POWER:
                setReadExpression(Architecture.pow.asFunctionCall(lhsExp, rhsExp));
                break;
            case REM:
                setReadExpression(Architecture.rem.asFunctionCall(lhsExp, rhsExp));
                break;
            case MOD:
                setReadExpression(Architecture.mod.asFunctionCall(lhsExp, rhsExp));
                break;
            case AND:
                setReadExpression(new BinaryExpression(lhsExp, BinaryOperator.AND, rhsExp));
                break;
            case OR:
                setReadExpression(new BinaryExpression(lhsExp, BinaryOperator.OR, rhsExp));
                break;
            case XOR:
                setReadExpression(new BinaryExpression(lhsExp, BinaryOperator.BITXOR, rhsExp));
                break;
            case GREATER_THAN:
                setReadExpression(new BinaryExpression(lhsExp, BinaryOperator.GREATER_THAN, rhsExp));
                break;
            case GREATER_THAN_OR_EQUAL:
                setReadExpression(new BinaryExpression(lhsExp, BinaryOperator.GREATER_THAN_OR_EQUAL, rhsExp));
                break;
            case LESS_THAN:
                setReadExpression(new BinaryExpression(lhsExp, BinaryOperator.LESS_THAN, rhsExp));
                break;
            case LESS_THAN_OR_EQUAL:
                setReadExpression(new BinaryExpression(lhsExp, BinaryOperator.LESS_THAN_OR_EQUAL, rhsExp));
                break;
            case EQUAL:
                setReadExpression(new BinaryExpression(lhsExp, BinaryOperator.EQUAL, rhsExp));
                break;
            case NOT_EQUAL:
                setReadExpression(new BinaryExpression(lhsExp, BinaryOperator.NOT_EQUAL, rhsExp));
                break;
            case CONCATENATE: {
                // Retranslate the rhs with the required element type if the lhs is a
                // collection. Some expressions (eg StructureAggregates) will give
                // better translations if they know what type they are inteded to be.
                if (maslExpression.getLhs().getType().getBasicType() instanceof CollectionType collection) {
                    rhsExp =
                            ExpressionTranslator.createTranslator(maslExpression.getRhs(),
                                                                  scope,
                                                                  collection.getContainedType()).getReadExpression();
                } else if (maslExpression.getLhs() instanceof LiteralExpression ||
                           !maslExpression.getType().equals(maslExpression.getLhs().getType())) {
                    final TypeUsage resultType = Types.getInstance().getType(maslExpression.getType());
                    lhsExp = resultType.getType().callConstructor(lhsExp);
                }

                // If this is a chained concatenation, we can optimise all but the
                // first
                // term. Need to create a copy of the first term to avoid the original
                // value being appended to, but subsequent terms can just append to
                // the
                // result of the last one.
                if (maslExpression.getLhs() instanceof org.xtuml.masl.metamodel.expression.BinaryExpression &&
                    ((org.xtuml.masl.metamodel.expression.BinaryExpression) maslExpression.getLhs()).getOperator() ==
                    org.xtuml.masl.metamodel.expression.BinaryExpression.Operator.CONCATENATE ||
                    maslExpression.getLhs().equals(assignedTo)) {
                    // If the binaryExpression has a write expression, use this
                    // otherwise
                    // use the read expression. The parser should pick up any errors
                    // with
                    // regards to a read expression being incorreclty used.
                    if (lhs.getWriteableExpression() != null) {
                        lhsExp = lhs.getWriteableExpression();
                    }
                    setReadExpression(new BinaryExpression(lhsExp, BinaryOperator.PLUS_ASSIGN, rhsExp));
                } else {
                    setReadExpression(new BinaryExpression(lhsExp, BinaryOperator.PLUS, rhsExp));
                }
            }
            break;

            case UNION:
                setReadExpression(new Function("set_union").asFunctionCall(promoteToUnorderedCollection(maslExpression.getLhs(),
                                                                                                        lhsExp),
                                                                           false,
                                                                           promoteToUnorderedCollection(maslExpression.getRhs(),
                                                                                                        rhsExp)));
                break;
            case DISUNION:
                setReadExpression(new Function("set_disunion").asFunctionCall(promoteToUnorderedCollection(
                        maslExpression.getLhs(),
                        lhsExp), false, promoteToUnorderedCollection(maslExpression.getRhs(), rhsExp)));
                break;
            case INTERSECTION:
                setReadExpression(new Function("set_intersection").asFunctionCall(promoteToUnorderedCollection(
                        maslExpression.getLhs(),
                        lhsExp), false, promoteToUnorderedCollection(maslExpression.getRhs(), rhsExp)));
                break;
            case NOT_IN:
                setReadExpression(new Function("set_not_in").asFunctionCall(promoteToUnorderedCollection(maslExpression.getLhs(),
                                                                                                         lhsExp),
                                                                            false,
                                                                            promoteToUnorderedCollection(maslExpression.getRhs(),
                                                                                                         rhsExp)));
                break;
            default:
                throw new IllegalArgumentException("Unrecognised BinaryExpression '" +
                                                   maslExpression.getClass() +
                                                   " " +
                                                   maslExpression +
                                                   "'");
        }
    }

    private Expression promoteToUnorderedCollection(final org.xtuml.masl.metamodel.expression.Expression maslExp,
                                                    final Expression exp) {
        // If the expression is not a collection type, then
        // this needs to be promoted to a collection type. If the collection is a
        // sequence, then convert it to a bag, as the set operations only operate on
        // unordered collections.
        Expression requiredExpr = exp;
        if (!(maslExp.getType().getBasicType() instanceof CollectionType)) {
            final TypeUsage lhsType = Types.getInstance().getType(maslExp.getType().getBasicType());
            final Expression sequenceExpr = Architecture.set(lhsType).callConstructor(exp);
            requiredExpr = sequenceExpr;
        } else if (maslExp.getType().getBasicType() instanceof SequenceType) {
            final TypeUsage
                    lhsType =
                    Types.getInstance().getType(((SequenceType) maslExp.getType().getBasicType()).getContainedType());
            final Expression sequenceExpr = Architecture.bag(lhsType).callConstructor(exp);
            requiredExpr = sequenceExpr;

        }
        return requiredExpr;
    }

    public ExpressionTranslator getLhs() {
        return lhs;
    }

    public ExpressionTranslator getRhs() {
        return rhs;
    }

}
