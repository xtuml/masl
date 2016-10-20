//
// File: DomainServiceInvocation.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.code;

import java.util.List;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.domain.DomainService;
import org.xtuml.masl.metamodelImpl.expression.Expression;


public class DomainServiceInvocation extends ServiceInvocation<DomainService>
    implements org.xtuml.masl.metamodel.code.DomainServiceInvocation
{

  DomainServiceInvocation ( final Position position, final DomainService service, final List<Expression> arguments )
  {
    super(position, service, arguments);
  }

  @Override
  protected String getCallPrefix ()
  {
    return getService().getQualifiedName();
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitDomainServiceInvocation(this, p);
  }

}
