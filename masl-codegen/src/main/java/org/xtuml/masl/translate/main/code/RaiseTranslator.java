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

import org.xtuml.masl.cppgen.*;
import org.xtuml.masl.cppgen.Class;
import org.xtuml.masl.translate.main.ExceptionTranslator;
import org.xtuml.masl.translate.main.Scope;
import org.xtuml.masl.translate.main.expression.ExpressionTranslator;

import java.util.ArrayList;
import java.util.List;

public class RaiseTranslator extends CodeTranslator {

    protected RaiseTranslator(final org.xtuml.masl.metamodel.code.RaiseStatement raise,
                              final Scope parentScope,
                              final CodeTranslator parentTranslator) {
        super(raise, parentScope, parentTranslator);

        if (raise.getException() == null) {
            getCode().appendStatement(new ThrowStatement());
        } else {
            final Class exceptionClass = ExceptionTranslator.getExceptionClass(raise.getException());

            final List<Expression> args = new ArrayList<>();
            if (raise.getMessage() != null) {
                args.add(ExpressionTranslator.createTranslator(raise.getMessage(), getScope()).getReadExpression());
            }

            if (raise.inExceptionHandler()) {
                final List<Expression> args2 = new ArrayList<>();
                args2.add(exceptionClass.callConstructor(args));
                getCode().appendStatement(new ExpressionStatement(new FunctionCall(new Function("throw_with_nested",
                                                                                                new Namespace("std")),
                                                                                   args2)));
            } else {
                getCode().appendStatement(new ThrowStatement(exceptionClass.callConstructor(args)));
            }

        }

    }

}
