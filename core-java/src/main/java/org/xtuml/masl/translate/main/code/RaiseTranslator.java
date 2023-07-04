/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 ----------------------------------------------------------------------------
 Classification: UK OFFICIAL
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.translate.main.code;

import org.xtuml.masl.cppgen.Class;
import org.xtuml.masl.cppgen.*;
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

            final List<Expression> args = new ArrayList<Expression>();
            if (raise.getMessage() != null) {
                args.add(ExpressionTranslator.createTranslator(raise.getMessage(), getScope()).getReadExpression());
            }

            if (raise.inExceptionHandler()) {
                final List<Expression> args2 = new ArrayList<Expression>();
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
