//
// File: ReturnStatement.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.code;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.common.Service;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.expression.Expression;



public class ReturnStatement extends Statement
    implements org.xtuml.masl.metamodel.code.ReturnStatement
{

  private final Expression returnValue;

  public static ReturnStatement create ( final Position position, final Service currentService, final Expression expression )
  {
    if ( expression == null )
    {
      return null;
    }

    try
    {
      return new ReturnStatement(position, currentService, expression);
    }
    catch ( final SemanticError e )
    {
      e.report();
      return null;
    }
  }

  
  public ReturnStatement ( final Position position, final Service currentService, final Expression returnValue ) throws SemanticError
  {
    super(position);

    if ( currentService == null || !currentService.isFunction() )
    {
      throw new SemanticError(SemanticErrorCode.ReturnNotInFunction, position);
    }

    currentService.getReturnType().checkAssignable(returnValue);

    this.returnValue = returnValue;
  }

  
  @Override
  public Expression getReturnValue ()
  {
    return returnValue;
  }

  @Override
  public String toString ()
  {
    return "return " + returnValue + ";";
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitReturnStatement(this, p);
  }


}
