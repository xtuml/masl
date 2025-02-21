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
import org.xtuml.masl.cppgen.Std;
import org.xtuml.masl.cppgen.TypeUsage;
import org.xtuml.masl.metamodel.expression.CastExpression;
import org.xtuml.masl.translate.main.Scope;
import org.xtuml.masl.translate.main.Types;

public class CastExpressionTranslator extends ExpressionTranslator {

    CastExpressionTranslator(final CastExpression expression, final Scope scope) {
        rhs = ExpressionTranslator.createTranslator(expression.getRhs(), scope);

        final TypeUsage required = Types.getInstance().getType(expression.getType());

        final Function castFn = Std.static_cast(new TypeUsage(required.getType()));
        setReadExpression(castFn.asFunctionCall(rhs.getReadExpression()));
        setWriteableExpression(castFn.asFunctionCall(rhs.getWriteableExpression()));
    }

    ExpressionTranslator getRhs() {
        return rhs;
    }

    private final ExpressionTranslator rhs;

}
