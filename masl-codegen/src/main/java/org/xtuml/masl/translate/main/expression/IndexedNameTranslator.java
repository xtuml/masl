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
import org.xtuml.masl.metamodel.expression.IndexedNameExpression;
import org.xtuml.masl.translate.main.Scope;

public class IndexedNameTranslator extends ExpressionTranslator {

    IndexedNameTranslator(final IndexedNameExpression indexedName, final Scope scope) {
        final ExpressionTranslator prefixTrans = createTranslator(indexedName.getPrefix(), scope);

        final Expression maslIndex = createTranslator(indexedName.getIndex(), scope).getReadExpression();

        setReadExpression(ACCESS.asFunctionCall(prefixTrans.getReadExpression(), false, maslIndex));
        setWriteableExpression(ACCESS_EXTEND.asFunctionCall(prefixTrans.getWriteableExpression(), false, maslIndex));

    }

    private final static Function ACCESS = new Function("access");
    private final static Function ACCESS_EXTEND = new Function("accessExtend");

    public Function getGetter() {
        return this.getter;
    }

    public Function getSetter() {
        return this.setter;
    }

    private Function getter;
    private Function setter;

}
