// 
// Filename : DoubleData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.processInterface;


public abstract class RealData extends SimpleDataValue<Double>
{

  private static Double ZERO = 0.0;

  @Override
  public Class<Double> getValueClass ()
  {
    return Double.class;
  }

  @Override
  public void fromString ( final String string )
  {
    if ( string.toLowerCase().equals("nan") )
    {
      setValue(Double.NaN);
    }
    else if ( string.toLowerCase().equals("inf") )
    {
      setValue(Double.POSITIVE_INFINITY);
    }
    else if ( string.toLowerCase().equals("-inf") )
    {
      setValue(Double.NEGATIVE_INFINITY);
    }
    else
    {
      setValue(Double.valueOf(string));
    }
  }

  @Override
  protected void setToDefaultValue ()
  {
    setValue(ZERO);
  }


  public static void main ( final String... args )
  {
    System.out.println(Double.valueOf("NaN"));
  }

}
