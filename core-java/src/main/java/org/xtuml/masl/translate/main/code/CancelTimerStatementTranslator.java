/*
 * Filename : ScheduleStatementTranslator.java
 * 
 * UK Crown Copyright (c) 2008. All Rights Reserved
 */
package org.xtuml.masl.translate.main.code;

import org.xtuml.masl.cppgen.ExpressionStatement;
import org.xtuml.masl.translate.main.Architecture;
import org.xtuml.masl.translate.main.Scope;
import org.xtuml.masl.translate.main.expression.ExpressionTranslator;


public class CancelTimerStatementTranslator extends CodeTranslator
{

  protected CancelTimerStatementTranslator ( final org.xtuml.masl.metamodel.code.CancelTimerStatement cancel,
                                             final Scope parentScope,
                                             final CodeTranslator parentTranslator )
  {
    super(cancel, parentScope, parentTranslator);

    final ExpressionTranslator timerIdTranslator = ExpressionTranslator.createTranslator(cancel.getTimerId(), getScope());

    final org.xtuml.masl.cppgen.Expression scheduleTimerFnCall = Architecture.Timer.cancelTimer(timerIdTranslator.getReadExpression());
    getCode().appendStatement(new ExpressionStatement(scheduleTimerFnCall));
  }
}
