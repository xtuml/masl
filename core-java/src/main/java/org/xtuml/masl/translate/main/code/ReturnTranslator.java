//
// File: ReturnTranslator.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.translate.main.code;

import org.xtuml.masl.cppgen.Expression;
import org.xtuml.masl.cppgen.ReturnStatement;
import org.xtuml.masl.translate.main.Scope;
import org.xtuml.masl.translate.main.expression.ExpressionTranslator;



public class ReturnTranslator extends CodeTranslator
{

  protected ReturnTranslator ( final org.xtuml.masl.metamodel.code.ReturnStatement ret,
                               final Scope parentScope,
                               final CodeTranslator parentTranslator )
  {
    super(ret, parentScope, parentTranslator);


    final Expression result = ret.getReturnValue() == null ? null : ExpressionTranslator.createTranslator(ret.getReturnValue(),
                                                                                                          getScope())
                                                                                        .getReadExpression();

    getCode().appendStatement(new ReturnStatement(result));

  }


}
