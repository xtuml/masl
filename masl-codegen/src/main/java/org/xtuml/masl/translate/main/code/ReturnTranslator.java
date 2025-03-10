/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.translate.main.code;

import org.xtuml.masl.cppgen.Expression;
import org.xtuml.masl.cppgen.ReturnStatement;
import org.xtuml.masl.translate.main.Scope;
import org.xtuml.masl.translate.main.expression.ExpressionTranslator;

public class ReturnTranslator extends CodeTranslator {

    protected ReturnTranslator(final org.xtuml.masl.metamodel.code.ReturnStatement ret,
                               final Scope parentScope,
                               final CodeTranslator parentTranslator) {
        super(ret, parentScope, parentTranslator);

        final Expression
                result =
                ret.getReturnValue() == null ?
                null :
                ExpressionTranslator.createTranslator(ret.getReturnValue(), getScope()).getReadExpression();

        getCode().appendStatement(new ReturnStatement(result));

    }

}
