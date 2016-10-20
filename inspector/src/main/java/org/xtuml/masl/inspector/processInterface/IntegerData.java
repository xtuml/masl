// 
// Filename : IntegerData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.processInterface;

public abstract class IntegerData extends SimpleDataValue<Integer>
{

  private static Integer ZERO = 0;

  @Override
  public Class<Integer> getValueClass ()
  {
    return Integer.class;
  }

  @Override
  public void fromString ( final String string )
  {
    setValue(Integer.decode(string));
  }

  @Override
  protected void setToDefaultValue ()
  {
    setValue(ZERO);
  }
}
