//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.expression;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.type.AnyInstanceType;
import org.xtuml.masl.metamodelImpl.type.BasicType;
import org.xtuml.masl.metamodelImpl.type.InstanceType;


public class NullLiteral extends LiteralExpression
    implements org.xtuml.masl.metamodel.expression.NullLiteral
{

  public NullLiteral ( final Position position )
  {
    this(position, AnyInstanceType.createAnonymous());
  }

  public NullLiteral ( final Position position, final BasicType type )
  {
    super(position);
    this.type = type;
  }

  @Override
  public String toString ()
  {
    return "null";
  }

  @Override
  public BasicType getType ()
  {
    return type;
  }

  @Override
  public Expression resolveInner ( final BasicType type )
  {
    if ( type.getPrimitiveType() instanceof InstanceType )
    {
      return new NullLiteral(getPosition(), type);
    }
    else
    {
      return this;
    }
  }

  @Override
  public boolean equals ( final Object obj )
  {
    if ( this == obj )
    {
      return true;
    }
    if ( !(obj instanceof NullLiteral) )
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
    return v.visitNullLiteral(this, p);
  }


  private final BasicType type;
}
