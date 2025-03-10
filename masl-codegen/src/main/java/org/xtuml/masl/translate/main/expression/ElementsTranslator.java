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
import org.xtuml.masl.metamodel.expression.DictionaryKeysExpression;
import org.xtuml.masl.metamodel.expression.DictionaryValuesExpression;
import org.xtuml.masl.metamodel.expression.ElementsExpression;
import org.xtuml.masl.translate.main.Scope;

public class ElementsTranslator extends ExpressionTranslator {

    ElementsTranslator(final ElementsExpression expression, final Scope scope) {
        setReadExpression(createTranslator(expression.getCollection(), scope).getReadExpression());
    }

    ElementsTranslator(final DictionaryKeysExpression expression, final Scope scope) {
        final ExpressionTranslator baseTrans = createTranslator(expression.getDictionary(), scope);

        setReadExpression(new Function("getKeys").asFunctionCall(baseTrans.getReadExpression(), false));
        setWriteableExpression(new Function("getKeys").asFunctionCall(baseTrans.getWriteableExpression(), false));
    }

    ElementsTranslator(final DictionaryValuesExpression expression, final Scope scope) {
        final ExpressionTranslator baseTrans = createTranslator(expression.getDictionary(), scope);

        setReadExpression(new Function("getValues").asFunctionCall(baseTrans.getReadExpression(), false));
        setWriteableExpression(new Function("getValues").asFunctionCall(baseTrans.getWriteableExpression(), false));
    }

}
