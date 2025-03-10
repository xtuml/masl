/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.javagen.astimpl;

import org.xtuml.masl.javagen.ast.ASTNodeVisitor;
import org.xtuml.masl.javagen.ast.expr.BinaryExpression;
import org.xtuml.masl.javagen.ast.expr.Expression;

class BinaryExpressionImpl extends ExpressionImpl implements BinaryExpression {

    BinaryExpressionImpl(final ASTImpl ast, final Expression lhs, final Operator operator, final Expression rhs) {
        super(ast);
        setOperator(operator);
        setLhs(lhs);
        setRhs(rhs);
    }

    @Override
    public void accept(final ASTNodeVisitor v) throws Exception {
        v.visitBinaryExpression(this);
    }

    @Override
    public ExpressionImpl getLhs() {
        return lhs.get();
    }

    @Override
    public Operator getOperator() {
        return operator;
    }

    @Override
    public ExpressionImpl getRhs() {
        return rhs.get();
    }

    @Override
    public ExpressionImpl setLhs(Expression expression) {
        if (((ExpressionImpl) expression).getPrecedence() < getPrecedence()) {
            expression = getAST().createParenthesizedExpression(expression);
        }
        this.lhs.set((ExpressionImpl) expression);
        return (ExpressionImpl) expression;
    }

    private void setOperator(final Operator operator) {
        this.operator = operator;
    }

    @Override
    public ExpressionImpl setRhs(Expression expression) {
        // Left Associative, so need to parenthesize rhs if equal precedence
        if (((ExpressionImpl) expression).getPrecedence() <= getPrecedence()) {
            expression = getAST().createParenthesizedExpression(expression);
        }
        this.rhs.set((ExpressionImpl) expression);
        return (ExpressionImpl) expression;
    }

    @Override
    protected int getPrecedence() {
        // Values from Java in a Nutshell Operator Summary Table
        switch (operator) {
            case MULTIPLY:
            case DIVIDE:
            case REMAINDER:
                return 12;
            case ADD:
            case SUBTRACT:
                return 11;
            case LEFT_SHIFT:
            case RIGHT_SHIFT:
            case RIGHT_SHIFT_UNSIGNED:
                return 10;
            case LESS_THAN:
            case GREATER_THAN:
            case LESS_THAN_OR_EQUAL_TO:
            case GREATER_THAN_OR_EQUAL_TO:
            case INSTANCEOF:
                return 9;
            case EQUAL_TO:
            case NOT_EQUAL_TO:
                return 8;
            case BITWISE_AND:
                return 7;
            case BITWISE_XOR:
                return 6;
            case BITWISE_OR:
                return 5;
            case AND:
                return 4;
            case OR:
                return 3;
            default:
                assert false : "Unrecognised operator";
                return 0;
        }
    }

    private final ChildNode<ExpressionImpl> lhs = new ChildNode<>(this);
    private final ChildNode<ExpressionImpl> rhs = new ChildNode<>(this);

    Operator operator;
}
