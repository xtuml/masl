/*
 ----------------------------------------------------------------------------
 (c) 2008-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 ----------------------------------------------------------------------------
 Classification: UK OFFICIAL
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.metamodelImpl.type;

import static org.xtuml.masl.metamodelImpl.type.TestTypes.data1;
import static org.xtuml.masl.metamodelImpl.type.TestTypes.data2;

public class TestBuiltinTypeConversion extends TestTypeConversion {

    public void testBooleanPrimitive() {
        checkPrimitive(data1.bBoolean, data2.bBoolean);
    }

    public void testBytePrimitive() {
        checkPrimitive(data1.bByte, data2.bInteger);
    }

    public void testCharacterPrimitive() {
        checkPrimitive(data1.bCharacter, data2.bWCharacter);
    }

    public void testDevicePrimitive() {
        checkPrimitive(data1.bDevice, data2.bDevice);
    }

    public void testDurationPrimitive() {
        checkPrimitive(data1.bDuration, data2.bDuration);
    }

    public void testEventPrimitive() {
        checkPrimitive(data1.bEvent, data2.bEvent);
    }

    public void testIntegerPrimitive() {
        checkPrimitive(data1.bInteger, data2.bInteger);
    }

    public void testRealPrimitive() {
        checkPrimitive(data1.bReal, data2.bReal);
    }

    public void testStringPrimitive() {
        checkPrimitive(data1.bString, data2.seqOfCharacter);
    }

    public void testTimestampPrimitive() {
        checkPrimitive(data1.bTimestamp, data2.bTimestamp);
    }

    public void testWCharacterPrimitive() {
        checkPrimitive(data1.bWCharacter, data2.bWCharacter);
    }

    public void testWStringPrimitive() {
        checkPrimitive(data1.bWString, data2.seqOfWCharacter);
    }

    public void testBooleanBasic() {
        checkBasicType(data1.bBoolean, data2.bBoolean);
    }

    public void testByteBasic() {
        checkBasicType(data1.bByte, data2.bByte);
    }

    public void testCharacterBasic() {
        checkBasicType(data1.bCharacter, data2.bCharacter);
    }

    public void testDeviceBasic() {
        checkBasicType(data1.bDevice, data2.bDevice);
    }

    public void testDurationBasic() {
        checkBasicType(data1.bDuration, data2.bDuration);
    }

    public void testEventBasic() {
        checkBasicType(data1.bEvent, data2.bEvent);
    }

    public void testIntegerBasic() {
        checkBasicType(data1.bInteger, data2.bInteger);
    }

    public void testRealBasic() {
        checkBasicType(data1.bReal, data2.bReal);
    }

    public void testStringBasic() {
        checkBasicType(data1.bString, data2.bString);
    }

    public void testTimestampBasic() {
        checkBasicType(data1.bTimestamp, data2.bTimestamp);
    }

    public void testWCharacterBasic() {
        checkBasicType(data1.bWCharacter, data2.bWCharacter);
    }

    public void testWStringBasic() {
        checkBasicType(data1.bWString, data2.bWString);
    }

}
