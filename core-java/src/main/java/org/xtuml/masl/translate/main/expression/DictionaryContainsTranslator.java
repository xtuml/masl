//
// File: NameExpressionTranslator.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.translate.main.expression;

import org.xtuml.masl.cppgen.Expression;
import org.xtuml.masl.cppgen.Function;
import org.xtuml.masl.metamodel.expression.DictionaryContainsExpression;
import org.xtuml.masl.translate.main.Scope;



public class DictionaryContainsTranslator extends ExpressionTranslator
{

  DictionaryContainsTranslator ( final DictionaryContainsExpression indexedName, final Scope scope )
  {
    final org.xtuml.masl.metamodel.expression.Expression prefix = indexedName.getPrefix();

    final Expression readBase = createTranslator(prefix, scope).getReadExpression();

    final Expression maslIndex = createTranslator(indexedName.getKey(), scope).getReadExpression();

    setReadExpression(new Function("hasValue").asFunctionCall(readBase, false, maslIndex));
  }

}
