//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.expression;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.type.BasicType;
import org.xtuml.masl.metamodelImpl.type.BooleanType;


public class BooleanLiteral extends LiteralExpression
    implements org.xtuml.masl.metamodel.expression.BooleanLiteral
{

  private final boolean value;

  public BooleanLiteral ( final Position position, final boolean value )
  {
    super(position);
    this.value = value;
  }

  BooleanLiteral ( final boolean value )
  {
    this(null, value);
  }

  @Override
  public String toString ()
  {
    return value ? "true" : "false";
  }

  @Override
  public boolean getValue ()
  {
    return value;
  }

  @Override
  public BasicType getType ()
  {
    return BooleanType.createAnonymous();
  }

  @Override
  public boolean equals ( final Object obj )
  {
    if ( this == obj )
    {
      return true;
    }
    if ( !(obj instanceof BooleanLiteral) )
    {
      return false;
    }
    else
    {
      final BooleanLiteral obj2 = (BooleanLiteral)obj;

      return value == obj2.value;
    }
  }

  @Override
  public int hashCode ()
  {
    return value ? 1 : 0;
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitBooleanLiteral(this, p);
  }
}
