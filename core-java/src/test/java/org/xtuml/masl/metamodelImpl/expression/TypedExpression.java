//
// File: TypedExpression.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.expression;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.expression.Expression;
import org.xtuml.masl.metamodelImpl.type.BasicType;


public class TypedExpression extends Expression
{

  public TypedExpression ( final BasicType type )
  {
    super(null);
    this.type = type;
  }

  @Override
  public boolean equals ( final Object obj )
  {
    return (this == obj);
  }


  @Override
  public BasicType getType ()
  {
    return type;
  }

  @Override
  public int hashCode ()
  {
    return System.identityHashCode(this);
  }

  private final BasicType type;

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return null;
  }

}
