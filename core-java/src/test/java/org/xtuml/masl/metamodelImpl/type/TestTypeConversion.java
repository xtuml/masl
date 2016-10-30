//
// File: TestSequenceType.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.type;

import org.xtuml.masl.metamodelImpl.type.BasicType;
import org.xtuml.masl.metamodelImpl.type.TypeDefinition;

import junit.framework.TestCase;


public abstract class TestTypeConversion extends TestCase
{

  protected void checkPrimitive ( final TypeDefinition value, final BasicType expected )
  {
    final BasicType result = value.getPrimitiveType();
    assertNotNull(result);
    assertEquals(value + ".getPrimitive()", expected, result);
  }

  protected void checkBasicType ( final BasicType value, final BasicType expected )
  {
    final BasicType result = value.getBasicType();
    assertNotNull(result);
    assertEquals(value + ".getBasicType()", expected, result);
  }

}
