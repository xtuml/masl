//
// File: UnknownName.java
//
package org.xtuml.masl.metamodelImpl.name;

import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.expression.StringLiteral;

public class UnknownName extends Name
{

  public static UnknownName create( final String name )
  {
    return new UnknownName(name);
  }

  private UnknownName( final String name )
  {
    super(name);
  }

  @Override
  public StringLiteral getReference ( final Position position )
  {
    return StringLiteral.create(position, "\"" + getName() + "\"");
  }

}
