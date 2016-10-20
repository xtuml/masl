//
// File: DomainFunctionInvocation.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.expression;

import java.util.List;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.domain.DomainService;



public class DomainFunctionInvocation extends FunctionInvocation<DomainService>
    implements org.xtuml.masl.metamodel.expression.DomainFunctionInvocation
{

  
  public DomainFunctionInvocation ( final Position position, final DomainService service, final List<Expression> arguments )
  {
    super(position, service, arguments);
  }

  @Override
  public Expression getFindSkeletonInner ()
  {
    return new DomainFunctionInvocation(getPosition(), getService(), getFindSkeletonArguments());
  }

  @Override
  protected String getCallPrefix ()
  {
    return getService().getQualifiedName();
  }

  @Override
  public boolean equals ( final Object obj )
  {
    if ( this == obj )
    {
      return true;
    }
    if ( !(obj instanceof DomainFunctionInvocation) )
    {
      return false;
    }
    else
    {
      final DomainFunctionInvocation obj2 = (DomainFunctionInvocation)obj;

      return getService().equals(obj2.getService())
             && getArguments().equals(obj2.getArguments());
    }
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitDomainFunctionInvocation(this, p);
  }

}
