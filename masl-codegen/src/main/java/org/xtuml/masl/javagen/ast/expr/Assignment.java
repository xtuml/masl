/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.javagen.ast.expr;

public interface Assignment extends StatementExpression {

    enum Operator {
        ASSIGN, ADD, MULTIPLY, REMAINDER, DIVIDE, SUBTRACT, LEFT_SHIFT, RIGHT_SHIFT, RIGHT_SHIFT_ZERO_EXTEND, BITWISE_AND, BITWISE_XOR, BITWISE_OR
    }

    Operator getOperator();

    Expression getTarget();

    Expression getSource();

    Expression setTarget(Expression target);

    Expression setSource(Expression source);

    void setOperator(Operator operator);
}
