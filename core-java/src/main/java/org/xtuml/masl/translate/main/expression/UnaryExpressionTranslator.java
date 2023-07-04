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
