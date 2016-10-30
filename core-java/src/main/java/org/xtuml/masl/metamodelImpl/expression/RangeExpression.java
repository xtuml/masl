//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.expression;

import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.type.BasicType;


public abstract class RangeExpression extends Expression
    implements org.xtuml.masl.metamodel.expression.RangeExpression
{

  RangeExpression ( final Position position )
  {
    super(position);
  }

  @Override
  public abstract Expression getMin ();

  @Override
  public abstract Expression getMax ();

  @Override
  public abstract BasicType getType ();

  @Override
  abstract public int hashCode ();

  @Override
  abstract public boolean equals ( Object obj );

}
