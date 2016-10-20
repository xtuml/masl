//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.expression;

import org.xtuml.masl.metamodelImpl.common.Position;


public abstract class LiteralExpression extends Expression
    implements org.xtuml.masl.metamodel.expression.LiteralExpression
{

  LiteralExpression ( final Position position )
  {
    super(position);
  }

  @Override
  public LiteralExpression evaluate ()
  {
    return this;
  }

}
