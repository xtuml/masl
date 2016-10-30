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



public class ObjectServiceInvocation extends ServiceInvocation<ObjectService>
    implements org.xtuml.masl.metamodel.code.ObjectServiceInvocation
{

  ObjectServiceInvocation ( final Position position, final ObjectService service, final List<Expression> arguments )
  {
    super(position, service, arguments);
  }

  @Override
  protected String getCallPrefix ()
  {
    return getService().getParentObject().getName() + "." + getService().getName();
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitObjectServiceInvocation(this, p);
  }


}
