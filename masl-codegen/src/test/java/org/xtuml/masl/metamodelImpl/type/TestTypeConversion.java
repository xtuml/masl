/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.metamodelImpl.type;
import junit.framework.TestCase;

public abstract class TestTypeConversion extends TestCase {

    protected void checkPrimitive(final TypeDefinition value, final BasicType expected) {
        final BasicType result = value.getPrimitiveType();
        assertNotNull(result);
        assertEquals(value + ".getPrimitive()", expected, result);
    }

    protected void checkBasicType(final BasicType value, final BasicType expected) {
        final BasicType result = value.getBasicType();
        assertNotNull(result);
        assertEquals(value + ".getBasicType()", expected, result);
    }

}
