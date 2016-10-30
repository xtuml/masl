//
// File: RaiseStatement.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.code;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.exception.ExceptionReference;
import org.xtuml.masl.metamodelImpl.expression.Expression;
import org.xtuml.masl.metamodelImpl.type.StringType;



public class RaiseStatement extends Statement
    implements org.xtuml.masl.metamodel.code.RaiseStatement
{

  private final ExceptionReference exception;
  private final Expression         message;

  public static RaiseStatement create ( final Position position, final ExceptionReference ref, final Expression message )
  {
    try
    {

      if ( ref == null )
      {
        return null;
      }

      if ( message != null )
      {
        StringType.createAnonymous().checkAssignable(message);
      }

      return new RaiseStatement(position, ref, message);
    }
    catch ( final SemanticError e )
    {
      e.report();
      return null;
    }
  }

  public RaiseStatement ( final Position position, final ExceptionReference exception, final Expression message )
  {
    super(position);
    this.exception = exception;
    this.message = message;
  }

  
  @Override
  public ExceptionReference getException ()
  {
    return exception;
  }

  /**
   * @return Returns the exeption text.
   */
  @Override
  public Expression getMessage ()
  {
    return message;
  }

  @Override
  public String toString ()
  {
    return "raise" + (exception == null ? "" : " " + exception) + (message == null ? "" : " ( " + message + " )") + ";";
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitRaiseStatement(this, p);
  }


}
