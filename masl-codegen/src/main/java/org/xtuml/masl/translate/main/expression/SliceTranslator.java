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
import org.xtuml.masl.cppgen.Function;
import org.xtuml.masl.metamodel.expression.SliceExpression;
import org.xtuml.masl.translate.main.Scope;

public class SliceTranslator extends ExpressionTranslator {

    SliceTranslator(final SliceExpression slice, final Scope scope) {
        final org.xtuml.masl.metamodel.expression.Expression prefix = slice.getPrefix();

        final ExpressionTranslator prefixTrans = createTranslator(prefix, scope);

        final Expression maslStartIndex = createTranslator(slice.getRange().getMin(), scope).getReadExpression();
        final Expression maslEndIndex = createTranslator(slice.getRange().getMax(), scope).getReadExpression();

        setReadExpression(SLICE.asFunctionCall(prefixTrans.getReadExpression(), false, maslStartIndex, maslEndIndex));
        setWriteableExpression(SLICE.asFunctionCall(prefixTrans.getWriteableExpression(),
                                                    false,
                                                    maslStartIndex,
                                                    maslEndIndex));

    }

    private final static Function SLICE = new Function("slice");

    public Function getGetter() {
        return this.getter;
    }

    public Function getSetter() {
        return this.setter;
    }

    private Function getter;
    private Function setter;

}
