/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.metamodelImpl.expression;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.common.Positioned;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.object.AttributeDeclaration;
import org.xtuml.masl.utils.HashCode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class BinaryExpression extends Expression
        implements org.xtuml.masl.metamodel.expression.BinaryExpression {

    public enum ImplOperator {
        AND("and", Operator.AND), XOR("xor", Operator.XOR), OR("or", Operator.OR), NOT_EQUAL("/=",
                                                                                             Operator.NOT_EQUAL), EQUAL(
                "=",
                Operator.EQUAL), LESS_THAN("<", Operator.LESS_THAN), GREATER_THAN(">",
                                                                                  Operator.GREATER_THAN), LESS_THAN_OR_EQUAL(
                "<=",
                Operator.LESS_THAN_OR_EQUAL), GREATER_THAN_OR_EQUAL(">=", Operator.GREATER_THAN_OR_EQUAL), PLUS("+",
                                                                                                                Operator.PLUS), MINUS(
                "-",
                Operator.MINUS), TIMES("*", Operator.TIMES), DIVIDE("/", Operator.DIVIDE), POWER("**",
                                                                                                 Operator.POWER), MOD(
                "mod",
                Operator.MOD), REM("rem", Operator.REM), CONCATENATE("&", Operator.CONCATENATE), UNION("union",
                                                                                                       Operator.UNION), NOT_IN(
                "not_in",
                Operator.NOT_IN), INTERSECTION("intersection", Operator.INTERSECTION), DISUNION("disunion",
                                                                                                Operator.DISUNION);

        ImplOperator(final String text, final Operator operator) {
            this.text = text;
            this.operator = operator;
        }

        @Override
        public String toString() {
            return text;
        }

        Operator getOperator() {
            return operator;
        }

        private final String text;
        private final Operator operator;

    }

    public static class OperatorRef extends Positioned {

        public OperatorRef(final Position position, final ImplOperator operator) {
            super(position);
            this.operatorImpl = operator;
        }

        private final ImplOperator operatorImpl;

        Operator getOperator() {
            return operatorImpl.getOperator();
        }

        @Override
        public String toString() {
            return operatorImpl.toString();
        }

        @Override
        public int hashCode() {
            return operatorImpl.hashCode();
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof OperatorRef op) {

                return operatorImpl.equals(op.operatorImpl);
            } else {
                return false;
            }

        }

    }

    public static BinaryExpression create(final Expression lhs, final OperatorRef operator, final Expression rhs) {
        if (lhs == null || operator == null || rhs == null) {
            return null;
        }

        try {
            switch (operator.getOperator()) {
                case AND:
                case OR:
                case XOR:
                    return new BinaryLogicalExpression(lhs, operator, rhs);

                case EQUAL:
                case LESS_THAN:
                case LESS_THAN_OR_EQUAL:
                case GREATER_THAN:
                case GREATER_THAN_OR_EQUAL:
                case NOT_EQUAL:
                    return new BinaryComparisonExpression(lhs, operator, rhs);

                case PLUS:
                case MINUS:
                    return new BinaryAdditiveExpression(lhs, operator, rhs);

                case TIMES:
                case DIVIDE:
                case MOD:
                case REM:
                case POWER:
                    return new BinaryMultiplicativeExpression(lhs, operator, rhs);

                case UNION:
                case DISUNION:
                case NOT_IN:
                case INTERSECTION:
                case CONCATENATE:
                    return new BinaryCollectionExpression(lhs, operator, rhs);

                default:
                    assert false : "Invalid enumerate";
                    return null;
            }
        } catch (final SemanticError e) {
            e.report();
            return null;
        }
    }

    protected BinaryExpression(final Position position, final OperatorRef operator) {
        super(position);

        this.operator = operator;

    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof BinaryExpression be) {

            return lhs.equals(be.lhs) && rhs.equals(be.rhs) && operator.equals(be.operator);
        } else {
            return false;
        }
    }

    @Override
    public int getFindAttributeCount() {
        return lhs.getFindAttributeCount() + rhs.getFindAttributeCount();
    }

    @Override
    public Expression getFindSkeletonInner() {
        final Expression lhsSkel = lhs.getFindSkeleton();
        final Expression rhsSkel = rhs.getFindSkeleton();

        if (lhsSkel instanceof FindParameterExpression) {
            ((FindParameterExpression) lhsSkel).overrideType(rhs.getType());
        }

        if (rhsSkel instanceof FindParameterExpression) {
            ((FindParameterExpression) rhsSkel).overrideType(lhs.getType());
        }

        return BinaryExpression.create(lhsSkel, operator, rhsSkel);
    }

    @Override
    public Expression getLhs() {
        return lhs;
    }

    @Override
    public Operator getOperator() {
        return operator.getOperator();
    }

    protected OperatorRef getOperatorRef() {
        return operator;
    }

    @Override
    public Expression getRhs() {
        return rhs;
    }

    @Override
    public int hashCode() {
        return HashCode.makeHash(lhs, rhs, operator);
    }

    @Override
    public String toString() {
        return "(" + lhs + " " + operator + " " + rhs + ")";
    }

    @Override
    public abstract LiteralExpression evaluate();

    @Override
    protected List<Expression> getFindArgumentsInner() {
        final List<Expression> params = new ArrayList<>();
        params.addAll(lhs.getFindArguments());
        params.addAll(rhs.getFindArguments());
        return params;
    }

    @Override
    protected List<FindParameterExpression> getFindParametersInner() {
        final List<FindParameterExpression> params = new ArrayList<>();
        params.addAll(lhs.getConcreteFindParameters());
        params.addAll(rhs.getConcreteFindParameters());
        return params;
    }

    @Override
    public List<AttributeDeclaration> getFindEqualAttributes() {
        if (getOperator() == Operator.EQUAL) {
            if (getLhs() instanceof FindAttributeNameExpression && getRhs() instanceof FindParameterExpression) {
                return Collections.singletonList(((FindAttributeNameExpression) getLhs()).getDeclaration());
            } else if (getRhs() instanceof FindAttributeNameExpression && getLhs() instanceof FindParameterExpression) {
                return Collections.singletonList(((FindAttributeNameExpression) getRhs()).getDeclaration());
            }
        } else if (getOperator() == Operator.AND) {
            final List<AttributeDeclaration> lhsAtts = getLhs().getFindEqualAttributes();
            final List<AttributeDeclaration> rhsAtts = getRhs().getFindEqualAttributes();
            if (lhsAtts != null && rhsAtts != null) {
                final List<AttributeDeclaration> result = new ArrayList<>();
                result.addAll(lhsAtts);
                result.addAll(rhsAtts);
                return result;
            }
        }
        return null;
    }

    private Expression lhs;

    private Expression rhs;

    private final OperatorRef operator;

    protected void setLhs(final Expression lhs) {
        this.lhs = lhs;
    }

    protected void setRhs(final Expression rhs) {
        this.rhs = rhs;
    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitBinaryExpression(this);
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren(lhs, rhs);
    }

}
