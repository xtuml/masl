//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.expression;

import org.xtuml.masl.metamodelImpl.common.Position;


public abstract class NumericLiteral extends LiteralExpression
{

  NumericLiteral ( final Position position )
  {
    super(position);
  }

  public abstract Number getValue ();

}
