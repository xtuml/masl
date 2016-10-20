//
// File: InstanceFunctionInvocation.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.expression;

import java.util.ArrayList;
import java.util.List;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.object.ObjectService;


public class InstanceFunctionInvocation extends FunctionInvocation<ObjectService>
    implements org.xtuml.masl.metamodel.expression.InstanceFunctionInvocation
{

  private final Expression instance;

  public InstanceFunctionInvocation ( final Position position,
                                      final Expression instance,
                                      final ObjectService service,
                                      final List<Expression> arguments )
  {
    super(position, service, arguments);
    this.instance = instance;
  }


  
  @Override
  public Expression getInstance ()
  {
    return this.instance;
  }

  @Override
  protected String getCallPrefix ()
  {
    return instance + "." + getService().getName();
  }


  @Override
  public Expression getFindSkeletonInner ()
  {
    return new InstanceFunctionInvocation(getPosition(), instance.getFindSkeleton(), getService(), getFindSkeletonArguments());
  }


  @Override
  public boolean equals ( final Object obj )
  {
    if ( this == obj )
    {
      return true;
    }
    if ( !(obj instanceof InstanceFunctionInvocation) )
    {
      return false;
    }
    else
    {
      final InstanceFunctionInvocation obj2 = (InstanceFunctionInvocation)obj;

      return getService().equals(obj2.getService()) && instance.equals(obj2.instance) && getArguments().equals(obj2.getArguments());
    }
  }

  @Override
  public int hashCode ()
  {
    return super.hashCode() ^ instance.hashCode();
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitInstanceFunctionInvocation(this, p);
  }

  @Override
  public List<Expression> getChildExpressions ()
  {
    final List<Expression> result = new ArrayList<Expression>();
    result.add(instance);
    result.addAll(super.getChildExpressions());
    return result;
  }

}
