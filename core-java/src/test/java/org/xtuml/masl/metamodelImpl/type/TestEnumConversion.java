//
// File: TestStructGetPrimitive.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.type;

import static org.xtuml.masl.metamodelImpl.type.TestTypes.data1;


public class TestEnumConversion extends TestTypeConversion
{

  public void testEnumPrimitive ()
  {
    checkPrimitive(data1.rainbowColours, data1.udRainbowColours);
  }

}
