// 
// Filename : LongData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.processInterface;


public abstract class NaturalData extends SimpleDataValue<Long>
{

  private static Long ZERO = 0l;

  @Override
  public void setValue ( final Long value )
  {
    if ( value.longValue() < 0 )
    {
      throw new IllegalArgumentException("Natural must be > 0");
    }
    super.setValue(value);
  }

  @Override
  public Class<Long> getValueClass ()
  {
    return Long.class;
  }

  @Override
  public void fromString ( final String string )
  {
    setValue(Long.decode(string));
  }

  @Override
  protected void setToDefaultValue ()
  {
    setValue(ZERO);
  }

}
