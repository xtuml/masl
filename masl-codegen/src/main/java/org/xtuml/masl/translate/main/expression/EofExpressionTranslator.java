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

import org.xtuml.masl.cppgen.Function;
import org.xtuml.masl.metamodel.expression.EofExpression;
import org.xtuml.masl.translate.main.Scope;

public class EofExpressionTranslator extends ExpressionTranslator {

    EofExpressionTranslator(final EofExpression expression, final Scope scope) {
        final ExpressionTranslator device = ExpressionTranslator.createTranslator(expression.getDevice(), scope);
        setReadExpression(new Function("atEOF").asFunctionCall(device.getReadExpression(), false));
    }

}
