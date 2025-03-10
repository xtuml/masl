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
import org.xtuml.masl.cppgen.ExpressionStatement;
import org.xtuml.masl.cppgen.Function;
import org.xtuml.masl.metamodel.code.DeleteStatement;
import org.xtuml.masl.translate.main.Scope;
import org.xtuml.masl.translate.main.expression.ExpressionTranslator;

public class DeleteTranslator extends CodeTranslator {

    protected DeleteTranslator(final DeleteStatement deletion,
                               final Scope parentScope,
                               final CodeTranslator parentTranslator) {
        super(deletion, parentScope, parentTranslator);

        final Expression
                instance =
                ExpressionTranslator.createTranslator(deletion.getInstance(), getScope()).getReadExpression();
        getCode().appendStatement(new ExpressionStatement(new Function("deleteInstance").asFunctionCall(instance,
                                                                                                        false)));

    }

}
