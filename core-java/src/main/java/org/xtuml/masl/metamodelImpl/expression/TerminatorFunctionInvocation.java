//
// File: DomainFunctionInvocation.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.expression;

import java.util.List;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.domain.DomainTerminatorService;



public class TerminatorFunctionInvocation extends FunctionInvocation<DomainTerminatorService>
    implements org.xtuml.masl.metamodel.expression.TerminatorFunctionInvocation
{

  
  public TerminatorFunctionInvocation ( final Position position,
                                        final DomainTerminatorService service,
                                        final List<Expression> arguments )
  {
    super(position, service, arguments);
  }


  @Override
  protected String getCallPrefix ()
  {
    return getService().getTerminator().getName() + "~>" + getService().getName();
  }

  @Override
  public Expression getFindSkeletonInner ()
  {
    return new TerminatorFunctionInvocation(getPosition(), getService(), getFindSkeletonArguments());
  }

  @Override
  public boolean equals ( final Object obj )
  {
    if ( this == obj )
    {
      return true;
    }
    if ( !(obj instanceof TerminatorFunctionInvocation) )
    {
      return false;
    }
    else
    {
      final TerminatorFunctionInvocation obj2 = (TerminatorFunctionInvocation)obj;

      return getService().equals(getService())
             && getArguments().equals(obj2.getArguments());
    }
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitTerminatorFunctionInvocation(this, p);
  }
}
