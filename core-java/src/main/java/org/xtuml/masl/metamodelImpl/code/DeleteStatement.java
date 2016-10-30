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
import org.xtuml.masl.metamodelImpl.type.AnyInstanceType;
import org.xtuml.masl.metamodelImpl.type.SequenceType;


public class DeleteStatement extends Statement
    implements org.xtuml.masl.metamodel.code.DeleteStatement
{

  private final Expression instance;

  public static DeleteStatement create ( final Position position, final Expression expression )
  {
    if ( expression == null )
    {
      return null;
    }

    try
    {
      return new DeleteStatement(position, expression);
    }
    catch ( final SemanticError e )
    {
      e.report();
      return null;
    }
  }

  private DeleteStatement ( final Position position, final Expression instance ) throws SemanticError
  {
    super(position);

    if ( !AnyInstanceType.createAnonymous().isAssignableFrom(instance)
         && !SequenceType.createAnonymous(AnyInstanceType.createAnonymous()).isAssignableFrom(instance) )
    {
      throw new SemanticError(SemanticErrorCode.NotInstanceType, instance.getPosition(), instance.getType());
    }

    this.instance = instance;
  }

  @Override
  public Expression getInstance ()
  {
    return instance;
  }

  @Override
  public String toString ()
  {
    return "delete " + instance + ";";
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitDeleteStatement(this, p);
  }


}
