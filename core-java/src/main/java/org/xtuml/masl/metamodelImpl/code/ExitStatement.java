//
// File: ReturnStatement.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.code;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.expression.Expression;
import org.xtuml.masl.metamodelImpl.type.BooleanType;


public class ExitStatement extends Statement
    implements org.xtuml.masl.metamodel.code.ExitStatement
{

  public static ExitStatement create ( final Position position, final Expression condition )
  {
    try
    {
      return new ExitStatement(position, condition);
    }
    catch ( final SemanticError e )
    {
      e.report();
      return null;
    }
  }

  private final Expression condition;

  public ExitStatement ( final Position position, final Expression condition ) throws SemanticError
  {
    super(position);

    if ( condition != null && !BooleanType.createAnonymous().isAssignableFrom(condition) )
    {
      throw new SemanticError(SemanticErrorCode.ExpectedBooleanCondition, condition.getPosition(), condition.getType());
    }

    this.condition = condition;
  }

  @Override
  public Expression getCondition ()
  {
    return condition;
  }

  @Override
  public String toString ()
  {
    return "exit" + (condition == null ? "" : " when " + condition) + ";";
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitExitStatement(this, p);
  }


}
