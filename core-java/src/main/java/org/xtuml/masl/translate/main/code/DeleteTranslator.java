//
// File: DeleteTranslator.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.translate.main.code;

import org.xtuml.masl.cppgen.Expression;
import org.xtuml.masl.cppgen.ExpressionStatement;
import org.xtuml.masl.cppgen.Function;
import org.xtuml.masl.metamodel.code.DeleteStatement;
import org.xtuml.masl.translate.main.Scope;
import org.xtuml.masl.translate.main.expression.ExpressionTranslator;



public class DeleteTranslator extends CodeTranslator
{

  protected DeleteTranslator ( final DeleteStatement deletion, final Scope parentScope, final CodeTranslator parentTranslator )
  {
    super(deletion, parentScope, parentTranslator);

    final Expression instance = ExpressionTranslator.createTranslator(deletion.getInstance(), getScope()).getReadExpression();
    getCode().appendStatement(new ExpressionStatement(new Function("deleteInstance").asFunctionCall(instance, false)));

  }


}
