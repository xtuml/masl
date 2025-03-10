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

public interface UnaryExpression extends Expression {

    enum Operator {
        PLUS, MINUS, NOT, BITWISE_COMPLEMENT
    }

    Expression getExpression();

    Expression setExpression(Expression expression);

    Operator getOperator();

    void setOperator(Operator operator);

}
