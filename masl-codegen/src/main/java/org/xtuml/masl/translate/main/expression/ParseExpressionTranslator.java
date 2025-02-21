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

import org.xtuml.masl.cppgen.Expression;
import org.xtuml.masl.metamodel.expression.ParseExpression;
import org.xtuml.masl.translate.main.Architecture;
import org.xtuml.masl.translate.main.Scope;
import org.xtuml.masl.translate.main.Types;

public class ParseExpressionTranslator extends ExpressionTranslator {

    ParseExpressionTranslator(final ParseExpression expression, final Scope scope) {
        final Expression
                arg =
                ExpressionTranslator.createTranslator(expression.getArgument(), scope).getReadExpression();
        if (expression.getBase() == null) {
            setReadExpression(Architecture.parse(Types.getInstance().getType(expression.getType()), arg));
        } else {
            final Expression
                    base =
                    ExpressionTranslator.createTranslator(expression.getBase(), scope).getReadExpression();
            setReadExpression(Architecture.parseBased(Types.getInstance().getType(expression.getType()), arg, base));
        }
    }

}
