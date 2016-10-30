//
// File: NameExpressionTranslator.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.translate.main.expression;

import org.xtuml.masl.cppgen.Expression;
import org.xtuml.masl.metamodel.expression.TimestampDeltaExpression;
import org.xtuml.masl.translate.main.Architecture;
import org.xtuml.masl.translate.main.Scope;



public class TimestampDeltaTranslator extends ExpressionTranslator
{

  TimestampDeltaTranslator ( final TimestampDeltaExpression deltaExpression, final Scope scope )
  {
    final Expression lhs = ExpressionTranslator.createTranslator(deltaExpression.getLhs(), scope).getReadExpression();
    final Expression arg = ExpressionTranslator.createTranslator(deltaExpression.getArgument(), scope).getReadExpression();

    switch ( deltaExpression.getDeltaType() )
    {
      case YEARS:
        setReadExpression(Architecture.Timestamp.addYears(lhs, arg));
        break;
      case MONTHS:
        setReadExpression(Architecture.Timestamp.addMonths(lhs, arg));
        break;
    }
  }


}
