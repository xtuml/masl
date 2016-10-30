//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.expression;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.type.BasicType;
import org.xtuml.masl.metamodelImpl.type.InternalType;


public class FlushLiteral extends LiteralExpression
    implements org.xtuml.masl.metamodel.expression.FlushLiteral
{

  public FlushLiteral ( final Position position )
  {
    super(position);
  }

  @Override
  public String toString ()
  {
    return "flush";
  }

  @Override
  public BasicType getType ()
  {
    return InternalType.STREAM_MODIFIER;
  }

  @Override
  public boolean equals ( final Object obj )
  {
    if ( this == obj )
    {
      return true;
    }
    if ( !(obj instanceof FlushLiteral) )
    {
      return false;
    }
    else
    {
      return true;
    }
  }

  @Override
  public int hashCode ()
  {

    return 0;
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitFlushLiteral(this, p);
  }

}
