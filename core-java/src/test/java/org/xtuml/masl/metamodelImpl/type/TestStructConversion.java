//
// File: TestStructGetPrimitive.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.type;

import static org.xtuml.masl.metamodelImpl.type.TestTypes.data1;


public class TestStructConversion extends TestTypeConversion
{


  public void testSimpleStructurePrimitive ()
  {
    checkPrimitive(data1.simpleStruct, data1.anonSimpleStruct);
  }

  public void testComplexStructurePrimitive ()
  {
    checkPrimitive(data1.complexStruct, data1.anonComplexStruct);
  }

}
