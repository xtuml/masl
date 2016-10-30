//
// File: NameExpressionTranslator.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.translate.main.expression;

import org.xtuml.masl.cppgen.Expression;
import org.xtuml.masl.metamodel.expression.TimerFieldExpression;
import org.xtuml.masl.translate.main.Architecture;
import org.xtuml.masl.translate.main.Scope;



public class TimerFieldTranslator extends ExpressionTranslator
{

  TimerFieldTranslator ( final TimerFieldExpression expression, final Scope scope )
  {

    final Expression lhs = ExpressionTranslator.createTranslator(expression.getLhs(), scope).getReadExpression();
    switch ( expression.getField() )
    {
      case expired_at:
        setReadExpression(Architecture.Timer.getExpiredAt(lhs));
        break;
      case scheduled_at:
        setReadExpression(Architecture.Timer.getScheduledAt(lhs));
        break;
      case delta:
        setReadExpression(Architecture.Timer.getPeriod(lhs));
        break;
      case expired:
        setReadExpression(Architecture.Timer.isExpired(lhs));
        break;
      case scheduled:
        setReadExpression(Architecture.Timer.isScheduled(lhs));
        break;
      case missed:
        setReadExpression(Architecture.Timer.getMissed(lhs));
        break;

    }
  }


}
