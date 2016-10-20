//
// File: RaiseTranslator.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.translate.main.code;

import java.util.ArrayList;
import java.util.List;

import org.xtuml.masl.cppgen.Class;
import org.xtuml.masl.cppgen.Expression;
import org.xtuml.masl.cppgen.ThrowStatement;
import org.xtuml.masl.translate.main.ExceptionTranslator;
import org.xtuml.masl.translate.main.Scope;
import org.xtuml.masl.translate.main.expression.ExpressionTranslator;



public class RaiseTranslator extends CodeTranslator
{

  protected RaiseTranslator ( final org.xtuml.masl.metamodel.code.RaiseStatement raise,
                              final Scope parentScope,
                              final CodeTranslator parentTranslator )
  {
    super(raise, parentScope, parentTranslator);

    if ( raise.getException() == null )
    {
      getCode().appendStatement(new ThrowStatement());
    }
    else
    {
      final Class exceptionClass = ExceptionTranslator.getExceptionClass(raise.getException());

      final List<Expression> args = new ArrayList<Expression>();
      if ( raise.getMessage() != null )
      {
        args.add(ExpressionTranslator.createTranslator(raise.getMessage(), getScope()).getReadExpression());
      }

      getCode().appendStatement(new ThrowStatement(exceptionClass.callConstructor(args)));

    }

  }

}
