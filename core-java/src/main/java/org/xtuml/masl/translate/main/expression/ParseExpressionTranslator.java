//
// File: NameExpressionTranslator.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.translate.main.expression;

import org.xtuml.masl.cppgen.Expression;
import org.xtuml.masl.metamodel.expression.ParseExpression;
import org.xtuml.masl.translate.main.Architecture;
import org.xtuml.masl.translate.main.Scope;
import org.xtuml.masl.translate.main.Types;



public class ParseExpressionTranslator extends ExpressionTranslator
{

  ParseExpressionTranslator ( final ParseExpression expression, final Scope scope )
  {
    final Expression arg = ExpressionTranslator.createTranslator(expression.getArgument(), scope).getReadExpression();
    if ( expression.getBase() == null )
    {
      setReadExpression(Architecture.parse(Types.getInstance().getType(expression.getType()), arg));
    }
    else
    {
      final Expression base = ExpressionTranslator.createTranslator(expression.getBase(), scope).getReadExpression();
      setReadExpression(Architecture.parseBased(Types.getInstance().getType(expression.getType()), arg, base));
    }
  }

}
