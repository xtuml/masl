//
// File: GenerateStatement.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.code;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.expression.Expression;

public class CancelTimerStatement extends Statement
    implements org.xtuml.masl.metamodel.code.CancelTimerStatement
{

  public static CancelTimerStatement create ( final Position position,
                                              final Expression timerId )
  {
    if ( timerId == null )
    {
      return null;
    }

    return new CancelTimerStatement(position, timerId);
  }

  private CancelTimerStatement ( final Position position,
                                 final Expression timerId )
  {
    super(position);
    this.timerId = timerId;
  }

  private final Expression timerId;

  @Override
  public String toString ()
  {
    return "cancel " + timerId + ";";

  }

  @Override
  public Expression getTimerId ()
  {
    return timerId;
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitCancelTimerStatement(this, p);
  }


}
