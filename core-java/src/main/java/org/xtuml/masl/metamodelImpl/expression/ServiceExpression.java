//
// File: EventExpression.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.expression;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.common.Service;
import org.xtuml.masl.metamodelImpl.common.ServiceOverload;
import org.xtuml.masl.metamodelImpl.type.BasicType;
import org.xtuml.masl.metamodelImpl.type.InternalType;



public class ServiceExpression<T extends Service> extends Expression
{

  
  public ServiceExpression ( final Position position, final ServiceOverload<T> overload )
  {
    super(position);
    this.overload = overload;
  }

  
  public ServiceOverload<T> getOverload ()
  {
    return this.overload;
  }

  @Override
  public String toString ()
  {
    final T first = overload.asList().get(0);
    return first.getQualifiedName();
  }


  /**
   * 
   * @return
   * @see org.xtuml.masl.metamodelImpl.expression.Expression#getType()
   */
  @Override
  public BasicType getType ()
  {
    return InternalType.SERVICE;
  }

  private final ServiceOverload<T> overload;

  @Override
  public boolean equals ( final Object obj )
  {
    if ( this == obj )
    {
      return true;
    }
    if ( !(obj instanceof ServiceExpression<?>) )
    {
      return false;
    }
    else
    {
      final ServiceExpression<?> obj2 = (ServiceExpression<?>)obj;

      return overload.equals(obj2.overload);
    }
  }

  @Override
  public int hashCode ()
  {
    return overload.hashCode();
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    throw new IllegalStateException("Cannot visit Service Expression");
  }

}
