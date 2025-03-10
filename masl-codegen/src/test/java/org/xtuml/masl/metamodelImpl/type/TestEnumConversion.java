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
import static org.xtuml.masl.metamodelImpl.type.TestTypes.data1;

public class TestEnumConversion extends TestTypeConversion {

    public void testEnumPrimitive() {
        checkPrimitive(data1.rainbowColours, data1.udRainbowColours);
    }

}
