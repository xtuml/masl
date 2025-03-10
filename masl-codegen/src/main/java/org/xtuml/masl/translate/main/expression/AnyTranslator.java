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
import org.xtuml.masl.metamodel.expression.AnyExpression;
import org.xtuml.masl.translate.main.Scope;

public class AnyTranslator extends ExpressionTranslator {

    AnyTranslator(final AnyExpression expression, final Scope scope) {
        final ExpressionTranslator collTrans = createTranslator(expression.getCollection(), scope);

        if (expression.getCount() == null) {
            setReadExpression(new Function("any").asFunctionCall(collTrans.getReadExpression(), false));
            setWriteableExpression(new Function("any").asFunctionCall(collTrans.getWriteableExpression(), false));
        } else {
            final org.xtuml.masl.cppgen.Expression
                    count =
                    createTranslator(expression.getCount(), scope).getReadExpression();
            setReadExpression(new Function("any").asFunctionCall(collTrans.getReadExpression(), false, count));
            setWriteableExpression(new Function("any").asFunctionCall(collTrans.getWriteableExpression(),
                                                                      false,
                                                                      count));
        }
    }

}
