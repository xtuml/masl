//
// File: DeleteTranslator.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.translate.main.code;

import org.xtuml.masl.cppgen.Expression;
import org.xtuml.masl.cppgen.ExpressionStatement;
import org.xtuml.masl.cppgen.Function;
import org.xtuml.masl.metamodel.code.EraseStatement;
import org.xtuml.masl.translate.main.Scope;
import org.xtuml.masl.translate.main.expression.ExpressionTranslator;



public class EraseTranslator extends CodeTranslator
{

  protected EraseTranslator ( final EraseStatement eraseStatement, final Scope parentScope, final CodeTranslator parentTranslator )
  {
    super(eraseStatement, parentScope, parentTranslator);

    final Expression dictionary = ExpressionTranslator.createTranslator(eraseStatement.getDictionary(), getScope())
                                                      .getWriteableExpression();
    final Expression key = ExpressionTranslator.createTranslator(eraseStatement.getKey(), getScope()).getReadExpression();
    getCode().appendStatement(new ExpressionStatement(new Function("eraseValue").asFunctionCall(dictionary, false, key)));

  }

}
