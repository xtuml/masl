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
import org.xtuml.masl.metamodel.expression.DictionaryAccessExpression;
import org.xtuml.masl.translate.main.Scope;

public class DictionaryAccessTranslator extends ExpressionTranslator {

    DictionaryAccessTranslator(final DictionaryAccessExpression indexedName, final Scope scope) {
        final org.xtuml.masl.metamodel.expression.Expression prefix = indexedName.getPrefix();

        final Expression readBase = createTranslator(prefix, scope).getReadExpression();
        final Expression writeBase = createTranslator(prefix, scope).getWriteableExpression();

        final Expression maslIndex = createTranslator(indexedName.getKey(), scope).getReadExpression();

        setReadExpression(new Function("getValue").asFunctionCall(readBase, false, maslIndex));
        setWriteableExpression(new Function("setValue").asFunctionCall(writeBase, false, maslIndex));

    }

}
