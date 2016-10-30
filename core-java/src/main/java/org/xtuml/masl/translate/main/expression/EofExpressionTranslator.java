//
// File: UnaryExpressionTranslator.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.translate.main.expression;

import org.xtuml.masl.cppgen.Function;
import org.xtuml.masl.metamodel.expression.EofExpression;
import org.xtuml.masl.translate.main.Scope;



public class EofExpressionTranslator extends ExpressionTranslator
{

  EofExpressionTranslator ( final EofExpression expression, final Scope scope )
  {
    final ExpressionTranslator device = ExpressionTranslator.createTranslator(expression.getDevice(), scope);
    setReadExpression(new Function("atEOF").asFunctionCall(device.getReadExpression(), false));
  }

}
