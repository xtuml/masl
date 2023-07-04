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

public class TestUserDefinedTypeConversion extends TestTypeConversion {

    public void testUdIntTypePrimitive() {
        checkPrimitive(data1.udIntType, data1.bInteger);
    }

    public void testUdIntTypeBasic() {
        checkBasicType(data1.udIntType, data1.bInteger);
    }

    public void testUdIntTypeTypePrimitive() {
        checkPrimitive(data1.udIntTypeType, data1.bInteger);
    }

    public void testUdIntTypeTypeBasic() {
        checkBasicType(data1.udIntTypeType, data1.bInteger);
    }

    public void testUdIntConstrainedTypePrimitive() {
        checkPrimitive(data1.udIntConstrainedType, data1.bInteger);
    }

    public void testUdIntConstrainedTypeBasic() {
        checkBasicType(data1.udIntConstrainedType, data1.bInteger);
    }

    public void testUdIntTypeConstrainedTypePrimitive() {
        checkPrimitive(data1.udIntTypeConstrainedType, data1.bInteger);
    }

    public void testUdIntTypeConstrainedTypeBasic() {
        checkBasicType(data1.udIntTypeConstrainedType, data1.bInteger);
    }

    public void testUdSeqOfIntTypePrimitive() {
        checkPrimitive(data1.udSeqOfIntType, data1.seqOfInt);
    }

    public void testUdSeqOfIntTypeBasic() {
        checkBasicType(data1.udSeqOfIntType, data1.seqOfInt);
    }

    public void testUdBagOfIntTypePrimitive() {
        checkPrimitive(data1.udBagOfIntType, data1.seqOfInt);
    }

    public void testUdBagOfIntTypeBasic() {
        checkBasicType(data1.udBagOfIntType, data1.bagOfInt);
    }

    public void testUdSetOfIntTypePrimitive() {
        checkPrimitive(data1.udSetOfIntType, data1.seqOfInt);
    }

    public void testUdSetOfIntTypeBasic() {
        checkBasicType(data1.udSetOfIntType, data1.setOfInt);
    }

    public void testUdArrayOfIntTypePrimitive() {
        checkPrimitive(data1.udArray1to10OfIntType, data1.seqOfInt);
    }

    public void testUdArrayOfIntTypeBasic() {
        checkBasicType(data1.udArray1to10OfIntType, data1.array1to10OfInt);
    }

    public void testUdSimpleStructPrimitive() {
        checkPrimitive(data1.udSimpleStructType, data1.anonSimpleStruct);
    }

    public void testUdSimpleStructBasicType() {
        checkBasicType(data1.udSimpleStructType, data1.udSimpleStructType);
    }

    public void testUdComplexStructPrimitive() {
        checkPrimitive(data1.udComplexStructType, data1.anonComplexStruct);
    }

    public void testUdComplexStructBasicType() {
        checkBasicType(data1.udComplexStructType, data1.udComplexStructType);
    }

    public void testUdEnumPrimitive() {
        checkPrimitive(data1.udRainbowColours, data1.udRainbowColours);
    }

    public void testUdEnumBasicType() {
        checkBasicType(data1.udRainbowColours, data1.udRainbowColours);
    }

}
