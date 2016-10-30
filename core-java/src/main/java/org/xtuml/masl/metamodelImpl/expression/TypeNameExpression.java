//
// File: ObjectNameExpression.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.expression;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.type.BasicType;
import org.xtuml.masl.metamodelImpl.type.InternalType;


public class TypeNameExpression extends Expression
    implements org.xtuml.masl.metamodel.TypeNameExpression
{

  public TypeNameExpression ( final Position position, final BasicType type )
  {
    super(position);
    this.type = type;
  }

  @Override
  public BasicType getReferencedType ()
  {
    return type;
  }

  private final BasicType type;

  @Override
  public boolean equals ( final Object obj )
  {
    if ( this == obj )
    {
      return true;
    }
    if ( !(obj instanceof TypeNameExpression) )
    {
      return false;
    }
    else
    {
      final TypeNameExpression obj2 = (TypeNameExpression)obj;

      return type.equals(obj2.type);
    }
  }

  @Override
  public String toString ()
  {
    return type.toString();
  }

  @Override
  public int hashCode ()
  {
    return type.hashCode();
  }


  @Override
  public BasicType getType ()
  {
    return InternalType.TYPE;
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitTypeNameExpression(this, p);
  }

}
