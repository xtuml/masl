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

import org.xtuml.masl.cppgen.ExpressionStatement;
import org.xtuml.masl.translate.main.Architecture;
import org.xtuml.masl.translate.main.Scope;
import org.xtuml.masl.translate.main.expression.ExpressionTranslator;

public class CancelTimerStatementTranslator extends CodeTranslator {

    protected CancelTimerStatementTranslator(final org.xtuml.masl.metamodel.code.CancelTimerStatement cancel,
                                             final Scope parentScope,
                                             final CodeTranslator parentTranslator) {
        super(cancel, parentScope, parentTranslator);

        final ExpressionTranslator
                timerIdTranslator =
                ExpressionTranslator.createTranslator(cancel.getTimerId(), getScope());

        final org.xtuml.masl.cppgen.Expression
                scheduleTimerFnCall =
                Architecture.Timer.cancelTimer(timerIdTranslator.getReadExpression());
        getCode().appendStatement(new ExpressionStatement(scheduleTimerFnCall));
    }
}
