/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.metamodel.expression;

public interface BinaryExpression extends Expression {

    enum Operator {
        AND, XOR, OR, NOT_EQUAL, EQUAL, LESS_THAN, GREATER_THAN, LESS_THAN_OR_EQUAL, GREATER_THAN_OR_EQUAL, PLUS, MINUS, CONCATENATE, UNION, NOT_IN, TIMES, DIVIDE, MOD, POWER, REM, INTERSECTION, DISUNION
    }

    Operator getOperator();

    Expression getLhs();

    Expression getRhs();

}
