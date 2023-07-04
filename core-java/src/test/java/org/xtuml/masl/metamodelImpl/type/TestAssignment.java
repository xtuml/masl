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

import junit.framework.TestCase;
import org.xtuml.masl.metamodelImpl.expression.TypedExpression;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public abstract class TestAssignment extends TestCase {

    public static class BBooleanAssignment extends TestAssignment {

        public BBooleanAssignment() {
            super(types.bBoolean, expr.namedBBoolean, expr.anonBBoolean);
        }
    }

    public static class BCharacterAssignment extends TestAssignment {

        public BCharacterAssignment() {
            super(types.bCharacter, expr.namedBCharacter, expr.anonBCharacter, expr.anonBWCharacter);
        }
    }

    public static class BDeviceAssignment extends TestAssignment {

        public BDeviceAssignment() {
            super(types.bDevice, expr.namedBDevice, expr.anonBDevice);
        }
    }

    public static class BDurationAssignment extends TestAssignment {

        public BDurationAssignment() {
            super(types.bDuration, expr.namedBDuration, expr.anonBDuration);
        }
    }

    public static class BEventAssignment extends TestAssignment {

        public BEventAssignment() {
            super(types.bEvent, expr.namedBEvent, expr.anonBEvent);
        }
    }

    public static class BByteAssignment extends TestAssignment {

        public BByteAssignment() {
            super(types.bByte, expr.namedBByte, expr.anonBByte, expr.anonBInteger);
        }
    }

    public static class BIntegerAssignment extends TestAssignment {

        public BIntegerAssignment() {
            super(types.bInteger, expr.namedBInteger, expr.anonBByte, expr.anonBInteger);
        }
    }

    public static class BRealAssignment extends TestAssignment {

        public BRealAssignment() {
            super(types.bReal, expr.namedBReal, expr.anonBReal, expr.anonBInteger, expr.anonBByte);
        }
    }

    public static class BStringAssignment extends TestAssignment {

        public BStringAssignment() {
            super(types.bString,
                  expr.namedBString,
                  expr.anonBString,
                  expr.anonBWString,
                  expr.anonSeqOfCharacter,
                  expr.anonSeqOfAnonCharacter,
                  expr.anonSeqOfAnonWCharacter,
                  expr.anonSetOfCharacter);
        }
    }

    public static class BTimestampAssignment extends TestAssignment {

        public BTimestampAssignment() {
            super(types.bTimestamp, expr.namedBTimestamp, expr.anonBTimestamp);
        }
    }

    public static class BWCharacterAssignment extends TestAssignment {

        public BWCharacterAssignment() {
            super(types.bWCharacter, expr.namedBWCharacter, expr.anonBCharacter, expr.anonBWCharacter);
        }
    }

    public static class BWStringAssignment extends TestAssignment {

        public BWStringAssignment() {
            super(types.bWString,
                  expr.namedBWString,
                  expr.anonBString,
                  expr.anonBWString,
                  expr.anonSeqOfWCharacter,
                  expr.anonSeqOfAnonCharacter,
                  expr.anonSeqOfAnonWCharacter,
                  expr.anonSetOfWCharacter);
        }
    }

    public static class UdIntTypeAssignment extends TestAssignment {

        public UdIntTypeAssignment() {
            super(types.udIntType, expr.namedUdIntType, expr.anonBInteger, expr.anonBByte);
        }
    }

    public static class UdCharacterTypeAssignment extends TestAssignment {

        public UdCharacterTypeAssignment() {
            super(types.udCharacterType, expr.namedUdCharacterType, expr.anonBCharacter, expr.anonBWCharacter);
        }
    }

    public static class UdWCharacterTypeAssignment extends TestAssignment {

        public UdWCharacterTypeAssignment() {
            super(types.udWCharacterType, expr.namedUdWCharacterType, expr.anonBCharacter, expr.anonBWCharacter);
        }
    }

    public static class UdIntTypeTypeAssignment extends TestAssignment {

        public UdIntTypeTypeAssignment() {
            super(types.udIntTypeType, expr.namedUdIntTypeType, expr.anonBInteger, expr.anonBByte);
        }
    }

    public static class UdIntConstrainedTypeAssignment extends TestAssignment {

        public UdIntConstrainedTypeAssignment() {
            super(types.udIntConstrainedType, expr.namedUdIntConstrainedType, expr.anonBInteger, expr.anonBByte);
        }
    }

    public static class UdIntTypeConstrainedTypeAssignment extends TestAssignment {

        public UdIntTypeConstrainedTypeAssignment() {
            super(types.udIntTypeConstrainedType,
                  expr.namedUdIntTypeConstrainedType,
                  expr.anonBInteger,
                  expr.anonBByte);
        }
    }

    public static class SeqOfRealAssignment extends TestAssignment {

        public SeqOfRealAssignment() {
            super(types.seqOfReal,
                  expr.namedSeqOfReal,
                  expr.anonSeqOfReal,
                  expr.anonSeqOfAnonReal,
                  expr.anonSeqOfAnonInt,
                  expr.anonSetOfAnonInt,
                  expr.anonBagOfAnonInt);
        }
    }

    public static class SeqOfIntAssignment extends TestAssignment {

        public SeqOfIntAssignment() {
            super(types.seqOfInt,
                  expr.namedSeqOfInt,
                  expr.anonSeqOfInt,
                  expr.anonSetOfInt,
                  expr.anonBagOfInt,
                  expr.anonSeqOfAnonInt,
                  expr.anonSetOfAnonInt,
                  expr.anonBagOfAnonInt);
        }
    }

    public static class SeqOfCharacterAssignment extends TestAssignment {

        public SeqOfCharacterAssignment() {
            super(types.seqOfCharacter,
                  expr.namedSeqOfCharacter,
                  expr.anonSeqOfCharacter,
                  expr.anonSeqOfAnonCharacter,
                  expr.anonSeqOfAnonWCharacter,
                  expr.anonSetOfCharacter,
                  expr.anonBString,
                  expr.anonBWString);
        }
    }

    public static class SeqOfWCharacterAssignment extends TestAssignment {

        public SeqOfWCharacterAssignment() {
            super(types.seqOfWCharacter,
                  expr.namedSeqOfWCharacter,
                  expr.anonSeqOfWCharacter,
                  expr.anonSeqOfAnonCharacter,
                  expr.anonSeqOfAnonWCharacter,
                  expr.anonSetOfWCharacter,
                  expr.anonBString,
                  expr.anonBWString);
        }
    }

    public static class SetOfIntAssignment extends TestAssignment {

        public SetOfIntAssignment() {
            super(types.setOfInt,
                  expr.namedSetOfInt,
                  expr.anonSeqOfInt,
                  expr.anonSetOfInt,
                  expr.anonBagOfInt,
                  expr.anonSeqOfAnonInt,
                  expr.anonSetOfAnonInt,
                  expr.anonBagOfAnonInt);
        }
    }

    public static class SetOfCharacterAssignment extends TestAssignment {

        public SetOfCharacterAssignment() {
            super(types.setOfCharacter,
                  expr.namedSetOfCharacter,
                  expr.anonSeqOfCharacter,
                  expr.anonSeqOfAnonCharacter,
                  expr.anonSeqOfAnonWCharacter,
                  expr.anonSetOfCharacter,
                  expr.anonBString,
                  expr.anonBWString);
        }
    }

    public static class SetOfWCharacterAssignment extends TestAssignment {

        public SetOfWCharacterAssignment() {
            super(types.setOfWCharacter,
                  expr.namedSetOfWCharacter,
                  expr.anonSeqOfWCharacter,
                  expr.anonSeqOfAnonCharacter,
                  expr.anonSeqOfAnonWCharacter,
                  expr.anonSetOfWCharacter,
                  expr.anonBString,
                  expr.anonBWString);
        }
    }

    public static class BagOfIntAssignment extends TestAssignment {

        public BagOfIntAssignment() {
            super(types.bagOfInt,
                  expr.namedBagOfInt,
                  expr.anonSeqOfInt,
                  expr.anonSetOfInt,
                  expr.anonBagOfInt,
                  expr.anonSeqOfAnonInt,
                  expr.anonSetOfAnonInt,
                  expr.anonBagOfAnonInt);
        }
    }

    public static class Array1OfIntAssignment extends TestAssignment {

        public Array1OfIntAssignment() {
            super(types.array1to10OfInt,
                  expr.namedArray1to10OfInt,
                  expr.anonSeqOfInt,
                  expr.anonSetOfInt,
                  expr.anonBagOfInt,
                  expr.anonSeqOfAnonInt,
                  expr.anonSetOfAnonInt,
                  expr.anonBagOfAnonInt);
        }
    }

    public static class Array2OfIntAssignment extends TestAssignment {

        public Array2OfIntAssignment() {
            super(types.array2to11OfInt,
                  expr.namedArray2to11OfInt,
                  expr.anonSeqOfInt,
                  expr.anonSetOfInt,
                  expr.anonBagOfInt,
                  expr.anonSeqOfAnonInt,
                  expr.anonSetOfAnonInt,
                  expr.anonBagOfAnonInt);
        }
    }

    public static class SeqOfSeqOfRealAssignment extends TestAssignment {

        public SeqOfSeqOfRealAssignment() {
            super(types.seqOfSeqOfReal, expr.namedSeqOfSeqOfReal, expr.anonSeqOfSeqOfReal);
        }
    }

    public static class SeqOfSeqOfIntAssignment extends TestAssignment {

        public SeqOfSeqOfIntAssignment() {
            super(types.seqOfSeqOfInt,
                  expr.namedSeqOfSeqOfInt,
                  expr.anonSeqOfSeqOfInt,
                  expr.anonSetOfSeqOfInt,
                  expr.anonBagOfSeqOfInt);
        }
    }

    public static class SeqOfSetOfIntAssignment extends TestAssignment {

        public SeqOfSetOfIntAssignment() {
            super(types.seqOfSetOfInt,
                  expr.namedSeqOfSetOfInt,
                  expr.anonSeqOfSetOfInt,
                  expr.anonSetOfSetOfInt,
                  expr.anonBagOfSetOfInt);
        }
    }

    public static class SeqOfBagOfIntAssignment extends TestAssignment {

        public SeqOfBagOfIntAssignment() {
            super(types.seqOfBagOfInt,
                  expr.namedSeqOfBagOfInt,
                  expr.anonSeqOfBagOfInt,
                  expr.anonSetOfBagOfInt,
                  expr.anonBagOfBagOfInt);
        }
    }

    public static class SeqOfArrayOfIntAssignment extends TestAssignment {

        public SeqOfArrayOfIntAssignment() {
            super(types.seqOfArrayOfInt,
                  expr.namedSeqOfArrayOfInt,
                  expr.anonSeqOfArrayOfInt,
                  expr.anonSetOfArrayOfInt,
                  expr.anonBagOfArrayOfInt);
        }
    }

    public static class SeqOfUdIntTypeAssignment extends TestAssignment {

        public SeqOfUdIntTypeAssignment() {
            super(types.seqOfUdIntType,
                  expr.namedSeqOfUdIntType,
                  expr.anonSeqOfUdIntType,
                  expr.anonSetOfUdIntType,
                  expr.anonBagOfUdIntType,
                  expr.anonSeqOfAnonInt,
                  expr.anonSetOfAnonInt,
                  expr.anonBagOfAnonInt);
        }
    }

    public static class SeqOfUdCharacterTypeAssignment extends TestAssignment {

        public SeqOfUdCharacterTypeAssignment() {
            super(types.seqOfUdCharacterType,
                  expr.namedSeqOfUdCharacterType,
                  expr.anonSeqOfUdCharacterType,
                  expr.anonSeqOfAnonCharacter,
                  expr.anonSeqOfAnonWCharacter,
                  expr.anonBString,
                  expr.anonBWString);
        }
    }

    public static class SeqOfUdIntTypeTypeAssignment extends TestAssignment {

        public SeqOfUdIntTypeTypeAssignment() {
            super(types.seqOfUdIntTypeType,
                  expr.namedSeqOfUdIntTypeType,
                  expr.anonSeqOfUdIntTypeType,
                  expr.anonSetOfUdIntTypeType,
                  expr.anonBagOfUdIntTypeType,
                  expr.anonSeqOfAnonInt,
                  expr.anonSetOfAnonInt,
                  expr.anonBagOfAnonInt);
        }
    }

    public static class SetOfSeqOfIntAssignment extends TestAssignment {

        public SetOfSeqOfIntAssignment() {
            super(types.setOfSeqOfInt,
                  expr.namedSetOfSeqOfInt,
                  expr.anonSeqOfSeqOfInt,
                  expr.anonSetOfSeqOfInt,
                  expr.anonBagOfSeqOfInt);
        }
    }

    public static class SetOfSetOfIntAssignment extends TestAssignment {

        public SetOfSetOfIntAssignment() {
            super(types.setOfSetOfInt,
                  expr.namedSetOfSetOfInt,
                  expr.anonSeqOfSetOfInt,
                  expr.anonSetOfSetOfInt,
                  expr.anonBagOfSetOfInt);
        }
    }

    public static class SetOfBagOfIntAssignment extends TestAssignment {

        public SetOfBagOfIntAssignment() {
            super(types.setOfBagOfInt,
                  expr.namedSetOfBagOfInt,
                  expr.anonSeqOfBagOfInt,
                  expr.anonSetOfBagOfInt,
                  expr.anonBagOfBagOfInt);
        }
    }

    public static class SetOfArrayOfIntAssignment extends TestAssignment {

        public SetOfArrayOfIntAssignment() {
            super(types.setOfArrayOfInt,
                  expr.namedSetOfArrayOfInt,
                  expr.anonSeqOfArrayOfInt,
                  expr.anonSetOfArrayOfInt,
                  expr.anonBagOfArrayOfInt);
        }
    }

    public static class SetOfUdIntTypeAssignment extends TestAssignment {

        public SetOfUdIntTypeAssignment() {
            super(types.setOfUdIntType,
                  expr.namedSetOfUdIntType,
                  expr.anonSeqOfUdIntType,
                  expr.anonSetOfUdIntType,
                  expr.anonBagOfUdIntType,
                  expr.anonSeqOfAnonInt,
                  expr.anonSetOfAnonInt,
                  expr.anonBagOfAnonInt);
        }
    }

    public static class SetOfUdIntTypeTypeAssignment extends TestAssignment {

        public SetOfUdIntTypeTypeAssignment() {
            super(types.setOfUdIntTypeType,
                  expr.namedSetOfUdIntTypeType,
                  expr.anonSeqOfUdIntTypeType,
                  expr.anonSetOfUdIntTypeType,
                  expr.anonBagOfUdIntTypeType,
                  expr.anonSeqOfAnonInt,
                  expr.anonSetOfAnonInt,
                  expr.anonBagOfAnonInt);
        }
    }

    public static class BagOfSeqOfIntAssignment extends TestAssignment {

        public BagOfSeqOfIntAssignment() {
            super(types.bagOfSeqOfInt,
                  expr.namedBagOfSeqOfInt,
                  expr.anonSeqOfSeqOfInt,
                  expr.anonSetOfSeqOfInt,
                  expr.anonBagOfSeqOfInt);
        }
    }

    public static class BagOfSetOfIntAssignment extends TestAssignment {

        public BagOfSetOfIntAssignment() {
            super(types.bagOfSetOfInt,
                  expr.namedBagOfSetOfInt,
                  expr.anonSeqOfSetOfInt,
                  expr.anonSetOfSetOfInt,
                  expr.anonBagOfSetOfInt);
        }
    }

    public static class BagOfBagOfIntAssignment extends TestAssignment {

        public BagOfBagOfIntAssignment() {
            super(types.bagOfBagOfInt,
                  expr.namedBagOfBagOfInt,
                  expr.anonSeqOfBagOfInt,
                  expr.anonSetOfBagOfInt,
                  expr.anonBagOfBagOfInt);
        }
    }

    public static class BagOfArrayOfIntAssignment extends TestAssignment {

        public BagOfArrayOfIntAssignment() {
            super(types.bagOfArrayOfInt,
                  expr.namedBagOfArrayOfInt,
                  expr.anonSeqOfArrayOfInt,
                  expr.anonSetOfArrayOfInt,
                  expr.anonBagOfArrayOfInt);
        }
    }

    public static class BagOfUdIntTypeAssignment extends TestAssignment {

        public BagOfUdIntTypeAssignment() {
            super(types.bagOfUdIntType,
                  expr.namedBagOfUdIntType,
                  expr.anonSeqOfUdIntType,
                  expr.anonSetOfUdIntType,
                  expr.anonBagOfUdIntType,
                  expr.anonSeqOfAnonInt,
                  expr.anonSetOfAnonInt,
                  expr.anonBagOfAnonInt);
        }
    }

    public static class BagOfUdIntTypeTypeAssignment extends TestAssignment {

        public BagOfUdIntTypeTypeAssignment() {
            super(types.bagOfUdIntTypeType,
                  expr.namedBagOfUdIntTypeType,
                  expr.anonSeqOfUdIntTypeType,
                  expr.anonSetOfUdIntTypeType,
                  expr.anonBagOfUdIntTypeType,
                  expr.anonSeqOfAnonInt,
                  expr.anonSetOfAnonInt,
                  expr.anonBagOfAnonInt);
        }
    }

    public static class ArrayOfSeqOfIntAssignment extends TestAssignment {

        public ArrayOfSeqOfIntAssignment() {
            super(types.arrayOfSeqOfInt,
                  expr.namedArrayOfSeqOfInt,
                  expr.anonSeqOfSeqOfInt,
                  expr.anonSetOfSeqOfInt,
                  expr.anonBagOfSeqOfInt);
        }
    }

    public static class ArrayOfSetOfIntAssignment extends TestAssignment {

        public ArrayOfSetOfIntAssignment() {
            super(types.arrayOfSetOfInt,
                  expr.namedArrayOfSetOfInt,
                  expr.anonSeqOfSetOfInt,
                  expr.anonSetOfSetOfInt,
                  expr.anonBagOfSetOfInt);
        }
    }

    public static class ArrayOfBagOfIntAssignment extends TestAssignment {

        public ArrayOfBagOfIntAssignment() {
            super(types.arrayOfBagOfInt,
                  expr.namedArrayOfBagOfInt,
                  expr.anonSeqOfBagOfInt,
                  expr.anonSetOfBagOfInt,
                  expr.anonBagOfBagOfInt);
        }
    }

    public static class ArrayOfArrayOfIntAssignment extends TestAssignment {

        public ArrayOfArrayOfIntAssignment() {
            super(types.arrayOfArrayOfInt,
                  expr.namedArrayOfArrayOfInt,
                  expr.anonSeqOfArrayOfInt,
                  expr.anonSetOfArrayOfInt,
                  expr.anonBagOfArrayOfInt);
        }
    }

    public static class ArrayOfUdIntTypeAssignment extends TestAssignment {

        public ArrayOfUdIntTypeAssignment() {
            super(types.arrayOfUdIntType,
                  expr.namedArrayOfUdIntType,
                  expr.anonSeqOfUdIntType,
                  expr.anonSetOfUdIntType,
                  expr.anonBagOfUdIntType,
                  expr.anonSeqOfAnonInt,
                  expr.anonSetOfAnonInt,
                  expr.anonBagOfAnonInt);
        }
    }

    public static class ArrayOfUdIntTypeTypeAssignment extends TestAssignment {

        public ArrayOfUdIntTypeTypeAssignment() {
            super(types.arrayOfUdIntTypeType,
                  expr.namedArrayOfUdIntTypeType,
                  expr.anonSeqOfUdIntTypeType,
                  expr.anonSetOfUdIntTypeType,
                  expr.anonBagOfUdIntTypeType,
                  expr.anonSeqOfAnonInt,
                  expr.anonSetOfAnonInt,
                  expr.anonBagOfAnonInt);
        }
    }

    public static class UdSeqOfIntAssignment extends TestAssignment {

        public UdSeqOfIntAssignment() {
            super(types.udSeqOfIntType,
                  expr.namedUdSeqOfIntType,
                  expr.anonSeqOfInt,
                  expr.anonSetOfInt,
                  expr.anonBagOfInt,
                  expr.anonSeqOfAnonInt,
                  expr.anonSetOfAnonInt,
                  expr.anonBagOfAnonInt);
        }
    }

    public static class UdSetOfIntAssignment extends TestAssignment {

        public UdSetOfIntAssignment() {
            super(types.udSetOfIntType,
                  expr.namedUdSetOfIntType,
                  expr.anonSeqOfInt,
                  expr.anonSetOfInt,
                  expr.anonBagOfInt,
                  expr.anonSeqOfAnonInt,
                  expr.anonSetOfAnonInt,
                  expr.anonBagOfAnonInt);
        }
    }

    public static class UdBagOfIntAssignment extends TestAssignment {

        public UdBagOfIntAssignment() {
            super(types.udBagOfIntType,
                  expr.namedUdBagOfIntType,
                  expr.anonSeqOfInt,
                  expr.anonSetOfInt,
                  expr.anonBagOfInt,
                  expr.anonSeqOfAnonInt,
                  expr.anonSetOfAnonInt,
                  expr.anonBagOfAnonInt);
        }
    }

    public static class UdArrayOfIntAssignment extends TestAssignment {

        public UdArrayOfIntAssignment() {
            super(types.udArray1to10OfIntType,
                  expr.namedUdArray1to10OfIntType,
                  expr.anonSeqOfInt,
                  expr.anonSetOfInt,
                  expr.anonBagOfInt,
                  expr.anonSeqOfAnonInt,
                  expr.anonSetOfAnonInt,
                  expr.anonBagOfAnonInt);
        }
    }

    public static class UdTrivialStructAssignment extends TestAssignment {

        public UdTrivialStructAssignment() {
            super(types.udTrivialStructType,
                  expr.namedUdTrivialStructType,
                  expr.anonAnonTrivialStruct,
                  expr.anonAnonTrivialAnonStruct,
                  expr.namedUdIntType,
                  expr.anonBInteger,
                  expr.anonBByte);
        }
    }

    public static class UdSimpleStructAssignment extends TestAssignment {

        public UdSimpleStructAssignment() {
            super(types.udSimpleStructType,
                  expr.namedUdSimpleStructType,
                  expr.anonAnonSimpleStruct,
                  expr.anonAnonSimpleAnonStruct);
        }
    }

    public static class UdComplexStructAssignment extends TestAssignment {

        public UdComplexStructAssignment() {
            super(types.udComplexStructType,
                  expr.namedUdComplexStructType,
                  expr.anonAnonComplexStruct,
                  expr.anonAnonComplexAnonStruct);
        }
    }

    public static class InstanceAssignment extends TestAssignment {

        public InstanceAssignment() {
            super(types.instance1, expr.namedInstance1, expr.anonInstance1, expr.anonAnyInstance);
        }
    }

    public static class AnyInstanceAssignment extends TestAssignment {

        public AnyInstanceAssignment() {
            super(types.anyInstance,
                  expr.namedInstance1,
                  expr.anonInstance1,
                  expr.anonInstance2,
                  expr.namedInstance2,
                  expr.anonAnyInstance,
                  expr.namedAnyInstance);
        }
    }

    public static class PrimaryColoursAssignment extends TestAssignment {

        public PrimaryColoursAssignment() {
            super(types.udPrimaryColours, expr.namedUdPrimaryColours, expr.anonUdPrimaryColours);
        }
    }

    public static class RainbowColoursAssignment extends TestAssignment {

        public RainbowColoursAssignment() {
            super(types.udRainbowColours, expr.namedUdRainbowColours, expr.anonUdRainbowColours);
        }
    }

    private static final TestTypes types = TestTypes.data1;
    private static final TestExpressions expr = TestExpressions.expr1;

    private final BasicType targetType;
    private final Set<TypedExpression> assignableExpressions = new HashSet<TypedExpression>();

    protected TestAssignment(final BasicType targetType, final TypedExpression... assignables) {
        this.targetType = targetType;
        assignableExpressions.addAll(Arrays.asList(assignables));
    }

    private void checkResult(final TypedExpression assignable) {
        final String
                message =
                targetType + " := " + (assignable.getType().isAnonymousType() ? "anon " : "") + assignable.getType();
        if (assignableExpressions.contains(assignable)) {
            assertTrue(message + " should be assignable", targetType.isAssignableFrom(assignable));
        } else {
            assertFalse(message + " should not be assignable", targetType.isAssignableFrom(assignable));
        }
    }

    public void testNamedBBoolean() {
        checkResult(expr.namedBBoolean);
    }

    public void testNamedBCharacter() {
        checkResult(expr.namedBCharacter);
    }

    public void testNamedBDevice() {
        checkResult(expr.namedBDevice);
    }

    public void testNamedBDuration() {
        checkResult(expr.namedBDuration);
    }

    public void testNamedBEvent() {
        checkResult(expr.namedBEvent);
    }

    public void testNamedBByte() {
        checkResult(expr.namedBByte);
    }

    public void testNamedBInteger() {
        checkResult(expr.namedBInteger);
    }

    public void testNamedBReal() {
        checkResult(expr.namedBReal);
    }

    public void testNamedBString() {
        checkResult(expr.namedBString);
    }

    public void testNamedBTimestamp() {
        checkResult(expr.namedBTimestamp);
    }

    public void testNamedBWCharacter() {
        checkResult(expr.namedBWCharacter);
    }

    public void testNamedBWString() {
        checkResult(expr.namedBWString);
    }

    public void testNamedUdIntType() {
        checkResult(expr.namedUdIntType);
    }

    public void testNamedUdRealType() {
        checkResult(expr.namedUdRealType);
    }

    public void testNamedUdIntConstrainedType() {
        checkResult(expr.namedUdIntConstrainedType);
    }

    public void testNamedUdIntTypeConstrainedType() {
        checkResult(expr.namedUdIntTypeConstrainedType);
    }

    public void testNamedUdCharacterType() {
        checkResult(expr.namedUdCharacterType);
    }

    public void testNamedUdWCharacterType() {
        checkResult(expr.namedUdWCharacterType);
    }

    public void testNamedUdStringType() {
        checkResult(expr.namedUdStringType);
    }

    public void testNamedUdWStringType() {
        checkResult(expr.namedUdWStringType);
    }

    public void testNamedUdIntTypeType() {
        checkResult(expr.namedUdIntTypeType);
    }

    public void testNamedSeqOfReal() {
        checkResult(expr.namedSeqOfReal);
    }

    public void testNamedSeqOfInt() {
        checkResult(expr.namedSeqOfInt);
    }

    public void testNamedSeqOfCharacter() {
        checkResult(expr.namedSeqOfCharacter);
    }

    public void testNamedSeqOfWCharacter() {
        checkResult(expr.namedSeqOfWCharacter);
    }

    public void testNamedSetOfInt() {
        checkResult(expr.namedSetOfInt);
    }

    public void testNamedSetOfCharacter() {
        checkResult(expr.namedSetOfCharacter);
    }

    public void testNamedSetOfWCharacter() {
        checkResult(expr.namedSetOfWCharacter);
    }

    public void testNamedBagOfInt() {
        checkResult(expr.namedBagOfInt);
    }

    public void testNamedArray1to10OfInt() {
        checkResult(expr.namedArray1to10OfInt);
    }

    public void testNamedArray2to11OfInt() {
        checkResult(expr.namedArray2to11OfInt);
    }

    public void testNamedSeqOfSeqOfReal() {
        checkResult(expr.namedSeqOfSeqOfReal);
    }

    public void testNamedSeqOfSeqOfInt() {
        checkResult(expr.namedSeqOfSeqOfInt);
    }

    public void testNamedSeqOfSetOfInt() {
        checkResult(expr.namedSeqOfSetOfInt);
    }

    public void testNamedSeqOfBagOfInt() {
        checkResult(expr.namedSeqOfBagOfInt);
    }

    public void testNamedSeqOfArrayOfInt() {
        checkResult(expr.namedSeqOfArrayOfInt);
    }

    public void testNamedSeqOfUdIntType() {
        checkResult(expr.namedSeqOfUdIntType);
    }

    public void testNamedSeqOfUdCharacterType() {
        checkResult(expr.namedSeqOfUdCharacterType);
    }

    public void testNamedSeqOfUdWCharacterType() {
        checkResult(expr.namedSeqOfUdWCharacterType);
    }

    public void testNamedSeqOfUdIntTypeType() {
        checkResult(expr.namedSeqOfUdIntTypeType);
    }

    public void testNamedSetOfSeqOfInt() {
        checkResult(expr.namedSetOfSeqOfInt);
    }

    public void testNamedSetOfSetOfInt() {
        checkResult(expr.namedSetOfSetOfInt);
    }

    public void testNamedSetOfBagOfInt() {
        checkResult(expr.namedSetOfBagOfInt);
    }

    public void testNamedSetOfArrayOfInt() {
        checkResult(expr.namedSetOfArrayOfInt);
    }

    public void testNamedSetOfUdIntType() {
        checkResult(expr.namedSetOfUdIntType);
    }

    public void testNamedSetOfUdIntTypeType() {
        checkResult(expr.namedSetOfUdIntTypeType);
    }

    public void testNamedBagOfSeqOfInt() {
        checkResult(expr.namedBagOfSeqOfInt);
    }

    public void testNamedBagOfSetOfInt() {
        checkResult(expr.namedBagOfSetOfInt);
    }

    public void testNamedBagOfBagOfInt() {
        checkResult(expr.namedBagOfBagOfInt);
    }

    public void testNamedBagOfArrayOfInt() {
        checkResult(expr.namedBagOfArrayOfInt);
    }

    public void testNamedBagOfUdIntType() {
        checkResult(expr.namedBagOfUdIntType);
    }

    public void testNamedBagOfUdIntTypeType() {
        checkResult(expr.namedBagOfUdIntTypeType);
    }

    public void testNamedArrayOfSeqOfInt() {
        checkResult(expr.namedArrayOfSeqOfInt);
    }

    public void testNamedArrayOfSetOfInt() {
        checkResult(expr.namedArrayOfSetOfInt);
    }

    public void testNamedArrayOfBagOfInt() {
        checkResult(expr.namedArrayOfBagOfInt);
    }

    public void testNamedArrayOfArrayOfInt() {
        checkResult(expr.namedArrayOfArrayOfInt);
    }

    public void testNamedArrayOfUdIntType() {
        checkResult(expr.namedArrayOfUdIntType);
    }

    public void testNamedArrayOfUdIntTypeType() {
        checkResult(expr.namedArrayOfUdIntTypeType);
    }

    public void testNamedUdSeqOfIntType() {
        checkResult(expr.namedUdSeqOfIntType);
    }

    public void testNamedUdSetOfIntType() {
        checkResult(expr.namedUdSetOfIntType);
    }

    public void testNamedUdBagOfIntType() {
        checkResult(expr.namedUdBagOfIntType);
    }

    public void testNamedUdArray1to10OfIntType() {
        checkResult(expr.namedUdArray1to10OfIntType);
    }

    public void testNamedUdSimpleStructType() {
        checkResult(expr.namedUdSimpleStructType);
    }

    public void testNamedUdTrivialStructType() {
        checkResult(expr.namedUdTrivialStructType);
    }

    public void testNamedUdComplexStructType() {
        checkResult(expr.namedUdComplexStructType);
    }

    public void testNamedUdPrimaryColours() {
        checkResult(expr.namedUdPrimaryColours);
    }

    public void testNamedUdRainbowColours() {
        checkResult(expr.namedUdRainbowColours);
    }

    public void testNamedInstance1() {
        checkResult(expr.namedInstance1);
    }

    public void testNamedInstance2() {
        checkResult(expr.namedInstance2);
    }

    public void testNamedAnyInstance() {
        checkResult(expr.namedAnyInstance);
    }

    public void testAnonBBoolean() {
        checkResult(expr.anonBBoolean);
    }

    public void testAnonBCharacter() {
        checkResult(expr.anonBCharacter);
    }

    public void testAnonBDevice() {
        checkResult(expr.anonBDevice);
    }

    public void testAnonBDuration() {
        checkResult(expr.anonBDuration);
    }

    public void testAnonBEvent() {
        checkResult(expr.anonBEvent);
    }

    public void testAnonBByte() {
        checkResult(expr.anonBByte);
    }

    public void testAnonBInteger() {
        checkResult(expr.anonBInteger);
    }

    public void testAnonBReal() {
        checkResult(expr.anonBReal);
    }

    public void testAnonBString() {
        checkResult(expr.anonBString);
    }

    public void testAnonBTimestamp() {
        checkResult(expr.anonBTimestamp);
    }

    public void testAnonBWCharacter() {
        checkResult(expr.anonBWCharacter);
    }

    public void testAnonBWString() {
        checkResult(expr.anonBWString);
    }

    public void testAnonSeqOfReal() {
        checkResult(expr.anonSeqOfReal);
    }

    public void testAnonSeqOfInt() {
        checkResult(expr.anonSeqOfInt);
    }

    public void testAnonSeqOfCharacter() {
        checkResult(expr.anonSeqOfCharacter);
    }

    public void testAnonSeqOfWCharacter() {
        checkResult(expr.anonSeqOfWCharacter);
    }

    public void testAnonSetOfInt() {
        checkResult(expr.anonSetOfInt);
    }

    public void testAnonSetOfCharacter() {
        checkResult(expr.anonSetOfCharacter);
    }

    public void testAnonSetOfWCharacter() {
        checkResult(expr.anonSetOfWCharacter);
    }

    public void testAnonBagOfInt() {
        checkResult(expr.anonBagOfInt);
    }

    public void testAnonSeqOfSeqOfReal() {
        checkResult(expr.anonSeqOfSeqOfReal);
    }

    public void testAnonSeqOfSeqOfInt() {
        checkResult(expr.anonSeqOfSeqOfInt);
    }

    public void testAnonSeqOfSetOfInt() {
        checkResult(expr.anonSeqOfSetOfInt);
    }

    public void testAnonSeqOfBagOfInt() {
        checkResult(expr.anonSeqOfBagOfInt);
    }

    public void testAnonSeqOfArrayOfInt() {
        checkResult(expr.anonSeqOfArrayOfInt);
    }

    public void testAnonSeqOfUdIntType() {
        checkResult(expr.anonSeqOfUdIntType);
    }

    public void testAnonSeqOfUdCharacterType() {
        checkResult(expr.anonSeqOfUdCharacterType);
    }

    public void testAnonSeqOfUdWCharacterType() {
        checkResult(expr.anonSeqOfUdWCharacterType);
    }

    public void testAnonSeqOfUdIntTypeType() {
        checkResult(expr.anonSeqOfUdIntTypeType);
    }

    public void testAnonSetOfSeqOfInt() {
        checkResult(expr.anonSetOfSeqOfInt);
    }

    public void testAnonSetOfSetOfInt() {
        checkResult(expr.anonSetOfSetOfInt);
    }

    public void testAnonSetOfBagOfInt() {
        checkResult(expr.anonSetOfBagOfInt);
    }

    public void testAnonSetOfArrayOfInt() {
        checkResult(expr.anonSetOfArrayOfInt);
    }

    public void testAnonSetOfUdIntType() {
        checkResult(expr.anonSetOfUdIntType);
    }

    public void testAnonSetOfUdIntTypeType() {
        checkResult(expr.anonSetOfUdIntTypeType);
    }

    public void testAnonBagOfSeqOfInt() {
        checkResult(expr.anonBagOfSeqOfInt);
    }

    public void testAnonBagOfSetOfInt() {
        checkResult(expr.anonBagOfSetOfInt);
    }

    public void testAnonBagOfBagOfInt() {
        checkResult(expr.anonBagOfBagOfInt);
    }

    public void testAnonBagOfArrayOfInt() {
        checkResult(expr.anonBagOfArrayOfInt);
    }

    public void testAnonBagOfUdIntType() {
        checkResult(expr.anonBagOfUdIntType);
    }

    public void testAnonBagOfUdIntTypeType() {
        checkResult(expr.anonBagOfUdIntTypeType);
    }

    public void testAnonAnonTrivialStruct() {
        checkResult(expr.anonAnonTrivialStruct);
    }

    public void testAnonAnonSimpleStruct() {
        checkResult(expr.anonAnonSimpleStruct);
    }

    public void testAnonAnonComplexStruct() {
        checkResult(expr.anonAnonComplexStruct);
    }

    public void testAnonAnonTrivialAnonStruct() {
        checkResult(expr.anonAnonTrivialAnonStruct);
    }

    public void testAnonAnonSimpleAnonStruct() {
        checkResult(expr.anonAnonSimpleAnonStruct);
    }

    public void testAnonAnonSimpleAnonStruct2() {
        checkResult(expr.anonAnonSimpleAnonStruct2);
    }

    public void testAnonAnonComplexAnonStruct() {
        checkResult(expr.anonAnonComplexAnonStruct);
    }

    public void testAnonUdPrimaryColours() {
        checkResult(expr.anonUdPrimaryColours);
    }

    public void testAnonUdRainbowColours() {
        checkResult(expr.anonUdRainbowColours);
    }

    public void testAnonSeqOfAnonReal() {
        checkResult(expr.anonSeqOfAnonReal);
    }

    public void testAnonSeqOfAnonInt() {
        checkResult(expr.anonSeqOfAnonInt);
    }

    public void testAnonSeqOfAnonCharacter() {
        checkResult(expr.anonSeqOfAnonCharacter);
    }

    public void testAnonSeqOfAnonWCharacter() {
        checkResult(expr.anonSeqOfAnonWCharacter);
    }

    public void testAnonSetOfAnonInt() {
        checkResult(expr.anonSetOfAnonInt);
    }

    public void testAnonBagOfAnonInt() {
        checkResult(expr.anonBagOfAnonInt);
    }

    public void testAnonInstance1() {
        checkResult(expr.anonInstance1);
    }

    public void testAnonInstance2() {
        checkResult(expr.anonInstance2);
    }

    public void testAnonAnyInstance() {
        checkResult(expr.anonAnyInstance);
    }

    // Check All Expressions Covered
    static {
        final java.lang.Class<TestExpressions> testExps = TestExpressions.class;

        for (final Field var : testExps.getDeclaredFields()) {
            if (!java.lang.reflect.Modifier.isStatic(var.getModifiers())) {
                final String fieldName = var.getName();
                final String reqdMethod = "test" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                try {
                    TestAssignment.class.getDeclaredMethod(reqdMethod);
                } catch (final NoSuchMethodException e) {
                    System.err.println("Warning : " + fieldName + " not tested for in TestAssignment");
                }
            }
        }
    }

}
