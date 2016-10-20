//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.expression;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.type.BasicType;
import org.xtuml.masl.metamodelImpl.type.DeviceType;


public class ConsoleLiteral extends LiteralExpression
    implements org.xtuml.masl.metamodel.expression.ConsoleLiteral
{

  public ConsoleLiteral ( final Position position )
  {
    super(position);
  }

  @Override
  public String toString ()
  {
    return "console";
  }

  @Override
  public BasicType getType ()
  {
    return DeviceType.createAnonymous();
  }

  @Override
  public boolean equals ( final Object obj )
  {
    if ( this == obj )
    {
      return true;
    }
    if ( !(obj instanceof ConsoleLiteral) )
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
    return v.visitConsoleLiteral(this, p);
  }

}
