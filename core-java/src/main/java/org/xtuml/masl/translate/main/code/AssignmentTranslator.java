//
// File: AssignmentTranslator.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.translate.main.code;

import org.xtuml.masl.cppgen.Expression;
import org.xtuml.masl.cppgen.ExpressionStatement;
import org.xtuml.masl.metamodel.code.AssignmentStatement;
import org.xtuml.masl.translate.main.Scope;
import org.xtuml.masl.translate.main.expression.ExpressionTranslator;



public class AssignmentTranslator extends CodeTranslator
{

  protected AssignmentTranslator ( final AssignmentStatement assignment,
                                   final Scope parentScope,
                                   final CodeTranslator parentTranslator )
  {
    super(assignment, parentScope, parentTranslator);

    rhsTranslator = ExpressionTranslator.createTranslator(assignment.getValue(), getScope(), assignment.getTarget());
    final Expression rhs = rhsTranslator.getReadExpression();
    final Expression result = ExpressionTranslator.createTranslator(assignment.getTarget(), getScope()).getWriteExpression(rhs);

    getCode().appendStatement(new ExpressionStatement(result));

  }

  public ExpressionTranslator getRhsTranslator ()
  {
    return rhsTranslator;
  }

  private final ExpressionTranslator rhsTranslator;


}
