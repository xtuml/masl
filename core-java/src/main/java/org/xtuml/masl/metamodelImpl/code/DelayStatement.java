//
// File: DelayStatement.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.code;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.expression.Expression;
import org.xtuml.masl.metamodelImpl.type.DurationType;


public class DelayStatement extends Statement
    implements org.xtuml.masl.metamodel.code.DelayStatement
{

  private final Expression duration;

  public static DelayStatement create ( final Position position, final Expression duration )
  {
    if ( duration == null )
    {
      return null;
    }
    try
    {
      return new DelayStatement(position, duration);
    }
    catch ( final SemanticError e )
    {
      e.report();
      return null;
    }
  }

  public DelayStatement ( final Position position, final Expression duration ) throws SemanticError
  {
    super(position);

    if ( !DurationType.createAnonymous().isAssignableFrom(duration) )
    {
      throw new SemanticError(SemanticErrorCode.DelayParameterNotDuration, duration.getPosition());
    }

    this.duration = duration;
  }

  @Override
  public Expression getDuration ()
  {
    return duration;
  }

  @Override
  public String toString ()
  {
    return "delay " + duration + ";";
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitDelayStatement(this, p);
  }

}
