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

import org.xtuml.masl.cppgen.Std;
import org.xtuml.masl.cppgen.UnaryExpression;
import org.xtuml.masl.cppgen.UnaryOperator;
import org.xtuml.masl.translate.main.Scope;

public class UnaryExpressionTranslator extends ExpressionTranslator {

    UnaryExpressionTranslator(final org.xtuml.masl.metamodel.expression.UnaryExpression expression, final Scope scope) {
        rhs = ExpressionTranslator.createTranslator(expression.getRhs(), scope);
        final org.xtuml.masl.cppgen.Expression rhsExp = rhs.getReadExpression();

        switch (expression.getOperator()) {
            case PLUS:
                setReadExpression(new UnaryExpression(UnaryOperator.PLUS, rhsExp));
                break;
            case MINUS:
                setReadExpression(new UnaryExpression(UnaryOperator.MINUS, rhsExp));
                break;
            case ABS:
                setReadExpression(Std.abs.asFunctionCall(rhsExp));
                break;
            case NOT:
                setReadExpression(new UnaryExpression(UnaryOperator.NOT, rhsExp));
                break;
            default:
                throw new IllegalArgumentException("Unrecognised UnaryExpression '" + expression + "'");
        }
    }

    ExpressionTranslator getRhs() {
        return rhs;
    }

    private final ExpressionTranslator rhs;

}
