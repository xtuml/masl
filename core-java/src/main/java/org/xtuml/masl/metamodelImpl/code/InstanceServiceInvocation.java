//
// File: DelayStatement.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.code;

import java.util.List;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.expression.Expression;
import org.xtuml.masl.metamodelImpl.object.ObjectService;



public class InstanceServiceInvocation extends ServiceInvocation<ObjectService>
    implements org.xtuml.masl.metamodel.code.InstanceServiceInvocation
{

  private final Expression instance;

  public InstanceServiceInvocation ( final Position position,
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
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitInstanceServiceInvocation(this, p);
  }

}
