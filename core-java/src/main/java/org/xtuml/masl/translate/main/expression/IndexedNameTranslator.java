//
// File: NameExpressionTranslator.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.translate.main.expression;

import org.xtuml.masl.cppgen.Expression;
import org.xtuml.masl.cppgen.Function;
import org.xtuml.masl.metamodel.expression.IndexedNameExpression;
import org.xtuml.masl.translate.main.Scope;



public class IndexedNameTranslator extends ExpressionTranslator
{

  IndexedNameTranslator ( final IndexedNameExpression indexedName, final Scope scope )
  {
    final ExpressionTranslator prefixTrans = createTranslator(indexedName.getPrefix(), scope);

    final Expression maslIndex = createTranslator(indexedName.getIndex(), scope).getReadExpression();

    setReadExpression(ACCESS.asFunctionCall(prefixTrans.getReadExpression(), false, maslIndex));
    setWriteableExpression(ACCESS.asFunctionCall(prefixTrans.getWriteableExpression(), false, maslIndex));

  }

  private final static Function ACCESS = new Function("access");

  public Function getGetter ()
  {
    return this.getter;
  }

  public Function getSetter ()
  {
    return this.setter;
  }

  private Function getter;
  private Function setter;

}
