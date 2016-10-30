//
// File: DelayStatement.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.code;

import java.util.List;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.domain.DomainTerminatorService;
import org.xtuml.masl.metamodelImpl.expression.Expression;



public class TerminatorServiceInvocation extends ServiceInvocation<DomainTerminatorService>
    implements org.xtuml.masl.metamodel.code.TerminatorServiceInvocation
{

  TerminatorServiceInvocation ( final Position position, final DomainTerminatorService service, final List<Expression> arguments )
  {
    super(position, service, arguments);
  }

  @Override
  protected String getCallPrefix ()
  {
    return getService().getTerminator().getName() + "~>" + getService().getName();
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitTerminatorServiceInvocation(this, p);
  }


}
