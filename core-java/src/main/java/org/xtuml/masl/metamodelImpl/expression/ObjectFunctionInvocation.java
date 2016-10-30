//
// File: ObjectFunctionInvocation.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.expression;

import java.util.List;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.object.ObjectService;


public class ObjectFunctionInvocation extends FunctionInvocation<ObjectService>
    implements org.xtuml.masl.metamodel.expression.ObjectFunctionInvocation
{

  public ObjectFunctionInvocation ( final Position position, final ObjectService service, final List<Expression> arguments )
  {
    super(position, service, arguments);
  }

  @Override
  protected String getCallPrefix ()
  {
    return getService().getParentObject().getName() + "." + getService().getName();
  }

  @Override
  public Expression getFindSkeletonInner ()
  {
    return new ObjectFunctionInvocation(getPosition(), getService(), getFindSkeletonArguments());
  }

  @Override
  public boolean equals ( final Object obj )
  {
    if ( this == obj )
    {
      return true;
    }
    if ( !(obj instanceof ObjectFunctionInvocation) )
    {
      return false;
    }
    else
    {
      final ObjectFunctionInvocation obj2 = (ObjectFunctionInvocation)obj;

      return getService().equals(obj2.getService())
             && getArguments().equals(obj2.getArguments());
    }
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitObjectFunctionInvocation(this, p);
  }

}
