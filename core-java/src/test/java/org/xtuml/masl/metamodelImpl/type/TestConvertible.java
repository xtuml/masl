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

public abstract class TestConvertible extends TestCase {

    private static final TestTypes types = TestTypes.data1;
    private static final TestExpressions expr = TestExpressions.expr1;

    private static final Set<TypedExpression>
            charExpressions =
            new HashSet<TypedExpression>(Arrays.asList(expr.namedBCharacter,
                                                       expr.anonBCharacter,
                                                       expr.anonBWCharacter,
                                                       expr.namedBWCharacter,
                                                       expr.namedUdCharacterType,
                                                       expr.namedUdWCharacterType));

    private static final Set<TypedExpression>
            integerExpressions =
            new HashSet<TypedExpression>(Arrays.asList(expr.anonBByte,
                                                       expr.anonBInteger,
                                                       expr.anonBReal,
                                                       expr.namedBByte,
                                                       expr.namedBInteger,
                                                       expr.namedBReal,
                                                       expr.namedUdIntType,
                                                       expr.namedUdRealType,
                                                       expr.namedUdIntTypeType,
                                                       expr.namedUdIntConstrainedType,
                                                       expr.namedUdIntTypeConstrainedType,
                                                       expr.namedUdPrimaryColours,
                                                       expr.namedUdRainbowColours,
                                                       expr.anonUdPrimaryColours,
                                                       expr.anonUdRainbowColours));

    private static final Set<TypedExpression>
            realExpressions =
            new HashSet<TypedExpression>(Arrays.asList(expr.anonBByte,
                                                       expr.anonBInteger,
                                                       expr.anonBReal,
                                                       expr.namedBByte,
                                                       expr.namedBInteger,
                                                       expr.namedBReal,
                                                       expr.namedUdIntType,
                                                       expr.namedUdRealType,
                                                       expr.namedUdIntTypeType,
                                                       expr.namedUdIntConstrainedType,
                                                       expr.namedUdIntTypeConstrainedType));

    private static final Set<TypedExpression>
            seqOfIntegerExpressions =
            new HashSet<TypedExpression>(Arrays.asList(expr.anonBagOfAnonInt,
                                                       expr.anonBagOfInt,
                                                       expr.anonBagOfUdIntType,
                                                       expr.anonBagOfUdIntTypeType,
                                                       expr.anonBByte,
                                                       expr.anonBInteger,
                                                       expr.anonBReal,
                                                       expr.anonSeqOfAnonInt,
                                                       expr.anonSeqOfAnonReal,
                                                       expr.anonSeqOfInt,
                                                       expr.anonSeqOfReal,
                                                       expr.anonSeqOfUdIntType,
                                                       expr.anonSeqOfUdIntTypeType,
                                                       expr.anonSetOfAnonInt,
                                                       expr.anonSetOfInt,
                                                       expr.anonSetOfUdIntType,
                                                       expr.anonSetOfUdIntTypeType,
                                                       expr.namedArray1to10OfInt,
                                                       expr.namedArray2to11OfInt,
                                                       expr.namedArrayOfUdIntType,
                                                       expr.namedArrayOfUdIntTypeType,
                                                       expr.namedBagOfInt,
                                                       expr.namedBagOfUdIntType,
                                                       expr.namedBagOfUdIntTypeType,
                                                       expr.namedBByte,
                                                       expr.namedBInteger,
                                                       expr.namedBReal,
                                                       expr.namedSeqOfInt,
                                                       expr.namedSeqOfReal,
                                                       expr.namedSeqOfUdIntType,
                                                       expr.namedSeqOfUdIntTypeType,
                                                       expr.namedSetOfInt,
                                                       expr.namedSetOfUdIntType,
                                                       expr.namedSetOfUdIntTypeType,
                                                       expr.namedUdArray1to10OfIntType,
                                                       expr.namedUdBagOfIntType,
                                                       expr.namedUdIntConstrainedType,
                                                       expr.namedUdRealType,
                                                       expr.namedUdIntType,
                                                       expr.namedUdIntTypeConstrainedType,
                                                       expr.namedUdIntTypeType,
                                                       expr.namedUdSeqOfIntType,
                                                       expr.namedUdSetOfIntType,
                                                       expr.namedUdPrimaryColours,
                                                       expr.namedUdRainbowColours,
                                                       expr.anonUdPrimaryColours,
                                                       expr.anonUdRainbowColours));

    private static final Set<TypedExpression>
            seqOfRealExpressions =
            new HashSet<TypedExpression>(Arrays.asList(expr.anonBagOfAnonInt,
                                                       expr.anonBagOfInt,
                                                       expr.anonBagOfUdIntType,
                                                       expr.anonBagOfUdIntTypeType,
                                                       expr.anonBByte,
                                                       expr.anonBInteger,
                                                       expr.anonBReal,
                                                       expr.anonSeqOfAnonInt,
                                                       expr.anonSeqOfAnonReal,
                                                       expr.anonSeqOfInt,
                                                       expr.anonSeqOfReal,
                                                       expr.anonSeqOfUdIntType,
                                                       expr.anonSeqOfUdIntTypeType,
                                                       expr.anonSetOfAnonInt,
                                                       expr.anonSetOfInt,
                                                       expr.anonSetOfUdIntType,
                                                       expr.anonSetOfUdIntTypeType,
                                                       expr.namedArray1to10OfInt,
                                                       expr.namedArray2to11OfInt,
                                                       expr.namedArrayOfUdIntType,
                                                       expr.namedArrayOfUdIntTypeType,
                                                       expr.namedBagOfInt,
                                                       expr.namedBagOfUdIntType,
                                                       expr.namedBagOfUdIntTypeType,
                                                       expr.namedBByte,
                                                       expr.namedBInteger,
                                                       expr.namedBReal,
                                                       expr.namedSeqOfInt,
                                                       expr.namedSeqOfReal,
                                                       expr.namedSeqOfUdIntType,
                                                       expr.namedSeqOfUdIntTypeType,
                                                       expr.namedSetOfInt,
                                                       expr.namedSetOfUdIntType,
                                                       expr.namedSetOfUdIntTypeType,
                                                       expr.namedUdArray1to10OfIntType,
                                                       expr.namedUdBagOfIntType,
                                                       expr.namedUdIntConstrainedType,
                                                       expr.namedUdRealType,
                                                       expr.namedUdIntType,
                                                       expr.namedUdIntTypeConstrainedType,
                                                       expr.namedUdIntTypeType,
                                                       expr.namedUdSeqOfIntType,
                                                       expr.namedUdSetOfIntType));

    private static final Set<TypedExpression>
            seqOfCharExpressions =
            new HashSet<TypedExpression>(Arrays.asList(expr.anonBCharacter,
                                                       expr.anonBCharacter,
                                                       expr.anonBString,
                                                       expr.anonBWCharacter,
                                                       expr.anonBWString,
                                                       expr.anonSeqOfAnonCharacter,
                                                       expr.anonSeqOfAnonWCharacter,
                                                       expr.anonSeqOfCharacter,
                                                       expr.anonSeqOfUdCharacterType,
                                                       expr.anonSeqOfUdWCharacterType,
                                                       expr.anonSeqOfWCharacter,
                                                       expr.anonSetOfCharacter,
                                                       expr.anonSetOfWCharacter,
                                                       expr.namedBCharacter,
                                                       expr.namedBString,
                                                       expr.namedBWCharacter,
                                                       expr.namedBWString,
                                                       expr.namedSeqOfCharacter,
                                                       expr.namedSeqOfUdCharacterType,
                                                       expr.namedSeqOfUdWCharacterType,
                                                       expr.namedSeqOfWCharacter,
                                                       expr.namedSetOfCharacter,
                                                       expr.namedSetOfWCharacter,
                                                       expr.namedUdCharacterType,
                                                       expr.namedUdWCharacterType,
                                                       expr.namedUdStringType,
                                                       expr.namedUdWStringType));

    private static final Set<TypedExpression>
            seqOfSeqOfIntegerExpressions =
            new HashSet<TypedExpression>(Arrays.asList(expr.anonBByte,
                                                       expr.anonBInteger,
                                                       expr.anonBReal,
                                                       expr.namedBByte,
                                                       expr.namedBInteger,
                                                       expr.namedBReal,
                                                       expr.namedUdIntType,
                                                       expr.namedUdRealType,
                                                       expr.namedUdIntTypeType,
                                                       expr.namedUdIntConstrainedType,
                                                       expr.namedUdIntTypeConstrainedType,
                                                       expr.anonBagOfAnonInt,
                                                       expr.anonBagOfArrayOfInt,
                                                       expr.anonBagOfBagOfInt,
                                                       expr.anonBagOfInt,
                                                       expr.anonBagOfSeqOfInt,
                                                       expr.anonBagOfSetOfInt,
                                                       expr.anonBagOfUdIntType,
                                                       expr.anonBagOfUdIntTypeType,
                                                       expr.anonSeqOfAnonInt,
                                                       expr.anonSeqOfAnonReal,
                                                       expr.anonSeqOfArrayOfInt,
                                                       expr.anonSeqOfBagOfInt,
                                                       expr.anonSeqOfInt,
                                                       expr.anonSeqOfReal,
                                                       expr.anonSeqOfSeqOfInt,
                                                       expr.anonSeqOfSeqOfReal,
                                                       expr.anonSeqOfSetOfInt,
                                                       expr.anonSeqOfUdIntType,
                                                       expr.anonSeqOfUdIntTypeType,
                                                       expr.anonSetOfAnonInt,
                                                       expr.anonSetOfArrayOfInt,
                                                       expr.anonSetOfBagOfInt,
                                                       expr.anonSetOfInt,
                                                       expr.anonSetOfSeqOfInt,
                                                       expr.anonSetOfSetOfInt,
                                                       expr.anonSetOfUdIntType,
                                                       expr.anonSetOfUdIntTypeType,
                                                       expr.namedArray1to10OfInt,
                                                       expr.namedArray2to11OfInt,
                                                       expr.namedArrayOfArrayOfInt,
                                                       expr.namedArrayOfBagOfInt,
                                                       expr.namedArrayOfSeqOfInt,
                                                       expr.namedArrayOfSetOfInt,
                                                       expr.namedArrayOfUdIntType,
                                                       expr.namedArrayOfUdIntTypeType,
                                                       expr.namedBagOfArrayOfInt,
                                                       expr.namedBagOfBagOfInt,
                                                       expr.namedBagOfInt,
                                                       expr.namedBagOfSeqOfInt,
                                                       expr.namedBagOfSetOfInt,
                                                       expr.namedBagOfUdIntType,
                                                       expr.namedBagOfUdIntTypeType,
                                                       expr.namedSeqOfArrayOfInt,
                                                       expr.namedSeqOfBagOfInt,
                                                       expr.namedSeqOfInt,
                                                       expr.namedSeqOfReal,
                                                       expr.namedSeqOfSeqOfInt,
                                                       expr.namedSeqOfSeqOfReal,
                                                       expr.namedSeqOfSetOfInt,
                                                       expr.namedSeqOfUdIntType,
                                                       expr.namedSeqOfUdIntTypeType,
                                                       expr.namedSetOfArrayOfInt,
                                                       expr.namedSetOfBagOfInt,
                                                       expr.namedSetOfInt,
                                                       expr.namedSetOfSeqOfInt,
                                                       expr.namedSetOfSetOfInt,
                                                       expr.namedSetOfUdIntType,
                                                       expr.namedSetOfUdIntTypeType,
                                                       expr.namedUdArray1to10OfIntType,
                                                       expr.namedUdBagOfIntType,
                                                       expr.namedUdSeqOfIntType,
                                                       expr.namedUdSetOfIntType,
                                                       expr.namedUdPrimaryColours,
                                                       expr.namedUdRainbowColours,
                                                       expr.anonUdPrimaryColours,
                                                       expr.anonUdRainbowColours));

    private static final Set<TypedExpression>
            seqOfSeqOfRealExpressions =
            new HashSet<TypedExpression>(Arrays.asList(expr.anonBByte,
                                                       expr.anonBInteger,
                                                       expr.anonBReal,
                                                       expr.namedBByte,
                                                       expr.namedBInteger,
                                                       expr.namedBReal,
                                                       expr.namedUdIntType,
                                                       expr.namedUdRealType,
                                                       expr.namedUdIntTypeType,
                                                       expr.namedUdIntConstrainedType,
                                                       expr.namedUdIntTypeConstrainedType,
                                                       expr.anonBagOfAnonInt,
                                                       expr.anonBagOfArrayOfInt,
                                                       expr.anonBagOfBagOfInt,
                                                       expr.anonBagOfInt,
                                                       expr.anonBagOfSeqOfInt,
                                                       expr.anonBagOfSetOfInt,
                                                       expr.anonBagOfUdIntType,
                                                       expr.anonBagOfUdIntTypeType,
                                                       expr.anonSeqOfAnonInt,
                                                       expr.anonSeqOfAnonReal,
                                                       expr.anonSeqOfArrayOfInt,
                                                       expr.anonSeqOfBagOfInt,
                                                       expr.anonSeqOfInt,
                                                       expr.anonSeqOfReal,
                                                       expr.anonSeqOfSeqOfInt,
                                                       expr.anonSeqOfSeqOfReal,
                                                       expr.anonSeqOfSetOfInt,
                                                       expr.anonSeqOfUdIntType,
                                                       expr.anonSeqOfUdIntTypeType,
                                                       expr.anonSetOfAnonInt,
                                                       expr.anonSetOfArrayOfInt,
                                                       expr.anonSetOfBagOfInt,
                                                       expr.anonSetOfInt,
                                                       expr.anonSetOfSeqOfInt,
                                                       expr.anonSetOfSetOfInt,
                                                       expr.anonSetOfUdIntType,
                                                       expr.anonSetOfUdIntTypeType,
                                                       expr.namedArray1to10OfInt,
                                                       expr.namedArray2to11OfInt,
                                                       expr.namedArrayOfArrayOfInt,
                                                       expr.namedArrayOfBagOfInt,
                                                       expr.namedArrayOfSeqOfInt,
                                                       expr.namedArrayOfSetOfInt,
                                                       expr.namedArrayOfUdIntType,
                                                       expr.namedArrayOfUdIntTypeType,
                                                       expr.namedBagOfArrayOfInt,
                                                       expr.namedBagOfBagOfInt,
                                                       expr.namedBagOfInt,
                                                       expr.namedBagOfSeqOfInt,
                                                       expr.namedBagOfSetOfInt,
                                                       expr.namedBagOfUdIntType,
                                                       expr.namedBagOfUdIntTypeType,
                                                       expr.namedSeqOfArrayOfInt,
                                                       expr.namedSeqOfBagOfInt,
                                                       expr.namedSeqOfInt,
                                                       expr.namedSeqOfReal,
                                                       expr.namedSeqOfSeqOfInt,
                                                       expr.namedSeqOfSeqOfReal,
                                                       expr.namedSeqOfSetOfInt,
                                                       expr.namedSeqOfUdIntType,
                                                       expr.namedSeqOfUdIntTypeType,
                                                       expr.namedSetOfArrayOfInt,
                                                       expr.namedSetOfBagOfInt,
                                                       expr.namedSetOfInt,
                                                       expr.namedSetOfSeqOfInt,
                                                       expr.namedSetOfSetOfInt,
                                                       expr.namedSetOfUdIntType,
                                                       expr.namedSetOfUdIntTypeType,
                                                       expr.namedUdArray1to10OfIntType,
                                                       expr.namedUdBagOfIntType,
                                                       expr.namedUdSeqOfIntType,
                                                       expr.namedUdSetOfIntType));

    public static class BBooleanConvertible extends TestConvertible {

        public BBooleanConvertible() {
            super(types.bBoolean, expr.namedBBoolean, expr.anonBBoolean);
        }
    }

    public static class BCharacterConvertible extends TestConvertible {

        public BCharacterConvertible() {
            super(types.bCharacter, charExpressions);
        }
    }

    public static class BDeviceConvertible extends TestConvertible {

        public BDeviceConvertible() {
            super(types.bDevice, expr.namedBDevice, expr.anonBDevice);
        }
    }

    public static class BDurationConvertible extends TestConvertible {

        public BDurationConvertible() {
            super(types.bDuration, expr.namedBDuration, expr.anonBDuration);
        }
    }

    public static class BEventConvertible extends TestConvertible {

        public BEventConvertible() {
            super(types.bEvent, expr.namedBEvent, expr.anonBEvent);
        }
    }

    public static class BByteConvertible extends TestConvertible {

        public BByteConvertible() {
            super(types.bByte, integerExpressions);
        }
    }

    public static class BIntegerConvertible extends TestConvertible {

        public BIntegerConvertible() {
            super(types.bInteger, integerExpressions);
        }
    }

    public static class BRealConvertible extends TestConvertible {

        public BRealConvertible() {
            super(types.bReal, realExpressions);
        }
    }

    public static class BStringConvertible extends TestConvertible {

        public BStringConvertible() {
            super(types.bString, seqOfCharExpressions);
        }
    }

    public static class BTimestampConvertible extends TestConvertible {

        public BTimestampConvertible() {
            super(types.bTimestamp, expr.namedBTimestamp, expr.anonBTimestamp);
        }
    }

    public static class BWCharacterConvertible extends TestConvertible {

        public BWCharacterConvertible() {
            super(types.bWCharacter, charExpressions);
        }
    }

    public static class BWStringConvertible extends TestConvertible {

        public BWStringConvertible() {
            super(types.bWString, seqOfCharExpressions);
        }
    }

    public static class UdIntTypeConvertible extends TestConvertible {

        public UdIntTypeConvertible() {
            super(types.udIntType, integerExpressions);
        }
    }

    public static class UdCharacterTypeConvertible extends TestConvertible {

        public UdCharacterTypeConvertible() {
            super(types.udCharacterType, charExpressions);
        }
    }

    public static class UdWCharacterTypeConvertible extends TestConvertible {

        public UdWCharacterTypeConvertible() {
            super(types.udWCharacterType, charExpressions);
        }
    }

    public static class UdIntTypeTypeConvertible extends TestConvertible {

        public UdIntTypeTypeConvertible() {
            super(types.udIntTypeType, integerExpressions);
        }
    }

    public static class UdIntConstrainedTypeConvertible extends TestConvertible {

        public UdIntConstrainedTypeConvertible() {
            super(types.udIntConstrainedType, integerExpressions);
        }
    }

    public static class UdIntTypeConstrainedTypeConvertible extends TestConvertible {

        public UdIntTypeConstrainedTypeConvertible() {
            super(types.udIntTypeConstrainedType, integerExpressions);
        }
    }

    public static class SeqOfRealConvertible extends TestConvertible {

        public SeqOfRealConvertible() {
            super(types.seqOfReal, seqOfRealExpressions);
        }
    }

    public static class SeqOfIntConvertible extends TestConvertible {

        public SeqOfIntConvertible() {
            super(types.seqOfInt, seqOfIntegerExpressions);
        }
    }

    public static class SeqOfCharacterConvertible extends TestConvertible {

        public SeqOfCharacterConvertible() {
            super(types.seqOfCharacter, seqOfCharExpressions);
        }
    }

    public static class SeqOfWCharacterConvertible extends TestConvertible {

        public SeqOfWCharacterConvertible() {
            super(types.seqOfWCharacter, seqOfCharExpressions);
        }
    }

    public static class SetOfIntConvertible extends TestConvertible {

        public SetOfIntConvertible() {
            super(types.setOfInt, seqOfIntegerExpressions);
        }
    }

    public static class SetOfCharacterConvertible extends TestConvertible {

        public SetOfCharacterConvertible() {
            super(types.setOfCharacter, seqOfCharExpressions);
        }
    }

    public static class SetOfWCharacterConvertible extends TestConvertible {

        public SetOfWCharacterConvertible() {
            super(types.setOfWCharacter, seqOfCharExpressions);
        }
    }

    public static class BagOfIntConvertible extends TestConvertible {

        public BagOfIntConvertible() {
            super(types.bagOfInt, seqOfIntegerExpressions);
        }
    }

    public static class Array1OfIntConvertible extends TestConvertible {

        public Array1OfIntConvertible() {
            super(types.array1to10OfInt, seqOfIntegerExpressions);
        }
    }

    public static class Array2OfIntConvertible extends TestConvertible {

        public Array2OfIntConvertible() {
            super(types.array2to11OfInt, seqOfIntegerExpressions);
        }
    }

    public static class SeqOfSeqOfRealConvertible extends TestConvertible {

        public SeqOfSeqOfRealConvertible() {
            super(types.seqOfSeqOfReal, seqOfSeqOfRealExpressions);
        }
    }

    public static class SeqOfSeqOfIntConvertible extends TestConvertible {

        public SeqOfSeqOfIntConvertible() {
            super(types.seqOfSeqOfInt, seqOfSeqOfIntegerExpressions);
        }
    }

    public static class SeqOfSetOfIntConvertible extends TestConvertible {

        public SeqOfSetOfIntConvertible() {
            super(types.seqOfSetOfInt, seqOfSeqOfIntegerExpressions);
        }
    }

    public static class SeqOfBagOfIntConvertible extends TestConvertible {

        public SeqOfBagOfIntConvertible() {
            super(types.seqOfBagOfInt, seqOfSeqOfIntegerExpressions);
        }
    }

    public static class SeqOfArrayOfIntConvertible extends TestConvertible {

        public SeqOfArrayOfIntConvertible() {
            super(types.seqOfArrayOfInt, seqOfSeqOfIntegerExpressions);
        }
    }

    public static class SeqOfUdIntTypeConvertible extends TestConvertible {

        public SeqOfUdIntTypeConvertible() {
            super(types.seqOfUdIntType, seqOfIntegerExpressions);
        }
    }

    public static class SeqOfUdCharacterTypeConvertible extends TestConvertible {

        public SeqOfUdCharacterTypeConvertible() {
            super(types.seqOfUdCharacterType, seqOfCharExpressions);
        }
    }

    public static class SeqOfUdIntTypeTypeConvertible extends TestConvertible {

        public SeqOfUdIntTypeTypeConvertible() {
            super(types.seqOfUdIntTypeType, seqOfIntegerExpressions);
        }
    }

    public static class SetOfSeqOfIntConvertible extends TestConvertible {

        public SetOfSeqOfIntConvertible() {
            super(types.setOfSeqOfInt, seqOfSeqOfIntegerExpressions);
        }
    }

    public static class SetOfSetOfIntConvertible extends TestConvertible {

        public SetOfSetOfIntConvertible() {
            super(types.setOfSetOfInt, seqOfSeqOfIntegerExpressions);
        }
    }

    public static class SetOfBagOfIntConvertible extends TestConvertible {

        public SetOfBagOfIntConvertible() {
            super(types.setOfBagOfInt, seqOfSeqOfIntegerExpressions);
        }
    }

    public static class SetOfArrayOfIntConvertible extends TestConvertible {

        public SetOfArrayOfIntConvertible() {
            super(types.setOfArrayOfInt, seqOfSeqOfIntegerExpressions);
        }
    }

    public static class SetOfUdIntTypeConvertible extends TestConvertible {

        public SetOfUdIntTypeConvertible() {
            super(types.setOfUdIntType, seqOfIntegerExpressions);
        }
    }

    public static class SetOfUdIntTypeTypeConvertible extends TestConvertible {

        public SetOfUdIntTypeTypeConvertible() {
            super(types.setOfUdIntTypeType, seqOfIntegerExpressions);
        }
    }

    public static class BagOfSeqOfIntConvertible extends TestConvertible {

        public BagOfSeqOfIntConvertible() {
            super(types.bagOfSeqOfInt, seqOfSeqOfIntegerExpressions);
        }
    }

    public static class BagOfSetOfIntConvertible extends TestConvertible {

        public BagOfSetOfIntConvertible() {
            super(types.bagOfSetOfInt, seqOfSeqOfIntegerExpressions);
        }
    }

    public static class BagOfBagOfIntConvertible extends TestConvertible {

        public BagOfBagOfIntConvertible() {
            super(types.bagOfBagOfInt, seqOfSeqOfIntegerExpressions);
        }
    }

    public static class BagOfArrayOfIntConvertible extends TestConvertible {

        public BagOfArrayOfIntConvertible() {
            super(types.bagOfArrayOfInt, seqOfSeqOfIntegerExpressions);
        }
    }

    public static class BagOfUdIntTypeConvertible extends TestConvertible {

        public BagOfUdIntTypeConvertible() {
            super(types.bagOfUdIntType, seqOfIntegerExpressions);
        }
    }

    public static class BagOfUdIntTypeTypeConvertible extends TestConvertible {

        public BagOfUdIntTypeTypeConvertible() {
            super(types.bagOfUdIntTypeType, seqOfIntegerExpressions);
        }
    }

    public static class ArrayOfSeqOfIntConvertible extends TestConvertible {

        public ArrayOfSeqOfIntConvertible() {
            super(types.arrayOfSeqOfInt, seqOfSeqOfIntegerExpressions);
        }
    }

    public static class ArrayOfSetOfIntConvertible extends TestConvertible {

        public ArrayOfSetOfIntConvertible() {
            super(types.arrayOfSetOfInt, seqOfSeqOfIntegerExpressions);
        }
    }

    public static class ArrayOfBagOfIntConvertible extends TestConvertible {

        public ArrayOfBagOfIntConvertible() {
            super(types.arrayOfBagOfInt, seqOfSeqOfIntegerExpressions);
        }
    }

    public static class ArrayOfArrayOfIntConvertible extends TestConvertible {

        public ArrayOfArrayOfIntConvertible() {
            super(types.arrayOfArrayOfInt, seqOfSeqOfIntegerExpressions);
        }
    }

    public static class ArrayOfUdIntTypeConvertible extends TestConvertible {

        public ArrayOfUdIntTypeConvertible() {
            super(types.arrayOfUdIntType, seqOfIntegerExpressions);
        }
    }

    public static class ArrayOfUdIntTypeTypeConvertible extends TestConvertible {

        public ArrayOfUdIntTypeTypeConvertible() {
            super(types.arrayOfUdIntTypeType, seqOfIntegerExpressions);
        }
    }

    public static class UdSeqOfIntConvertible extends TestConvertible {

        public UdSeqOfIntConvertible() {
            super(types.udSeqOfIntType, seqOfIntegerExpressions);
        }
    }

    public static class UdSetOfIntConvertible extends TestConvertible {

        public UdSetOfIntConvertible() {
            super(types.udSetOfIntType, seqOfIntegerExpressions);
        }
    }

    public static class UdBagOfIntConvertible extends TestConvertible {

        public UdBagOfIntConvertible() {
            super(types.udBagOfIntType, seqOfIntegerExpressions);
        }
    }

    public static class UdArrayOfIntConvertible extends TestConvertible {

        public UdArrayOfIntConvertible() {
            super(types.udArray1to10OfIntType, seqOfIntegerExpressions);
        }
    }

    public static class UdTrivialStructConvertible extends TestConvertible {

        public UdTrivialStructConvertible() {
            super(types.udTrivialStructType,
                  expr.namedUdTrivialStructType,
                  expr.anonAnonTrivialStruct,
                  expr.anonAnonTrivialAnonStruct,
                  expr.anonBByte,
                  expr.anonBInteger,
                  expr.anonBReal,
                  expr.namedBByte,
                  expr.namedBInteger,
                  expr.namedBReal,
                  expr.namedUdRealType,
                  expr.namedUdIntType,
                  expr.namedUdIntTypeType,
                  expr.namedUdIntConstrainedType,
                  expr.namedUdIntTypeConstrainedType,
                  expr.namedUdPrimaryColours,
                  expr.namedUdRainbowColours,
                  expr.anonUdPrimaryColours,
                  expr.anonUdRainbowColours);
        }
    }

    public static class UdSimpleStructConvertible extends TestConvertible {

        public UdSimpleStructConvertible() {
            super(types.udSimpleStructType,
                  expr.namedUdSimpleStructType,
                  expr.anonAnonSimpleStruct,
                  expr.anonAnonSimpleAnonStruct,
                  expr.anonAnonSimpleAnonStruct2);
        }
    }

    public static class UdComplexStructConvertible extends TestConvertible {

        public UdComplexStructConvertible() {
            super(types.udComplexStructType,
                  expr.namedUdComplexStructType,
                  expr.anonAnonComplexStruct,
                  expr.anonAnonComplexAnonStruct);
        }
    }

    public static class InstanceConvertible extends TestConvertible {

        public InstanceConvertible() {
            super(types.instance1, expr.namedInstance1, expr.anonInstance1, expr.anonAnyInstance);
        }
    }

    public static class AnyInstanceConvertible extends TestConvertible {

        public AnyInstanceConvertible() {
            super(types.anyInstance,
                  expr.namedInstance1,
                  expr.anonInstance1,
                  expr.anonInstance2,
                  expr.namedInstance2,
                  expr.anonAnyInstance,
                  expr.namedAnyInstance);
        }
    }

    public static class PrimaryColoursConvertible extends TestConvertible {

        public PrimaryColoursConvertible() {
            super(types.udPrimaryColours,
                  expr.anonBByte,
                  expr.anonBInteger,
                  expr.namedBByte,
                  expr.namedBInteger,
                  expr.namedUdIntType,
                  expr.namedUdIntTypeType,
                  expr.namedUdIntConstrainedType,
                  expr.namedUdIntTypeConstrainedType,
                  expr.namedUdPrimaryColours,
                  expr.anonUdPrimaryColours);
        }
    }

    public static class RainbowColoursConvertible extends TestConvertible {

        public RainbowColoursConvertible() {
            super(types.udRainbowColours,
                  expr.anonBByte,
                  expr.anonBInteger,
                  expr.namedBByte,
                  expr.namedBInteger,
                  expr.namedUdIntType,
                  expr.namedUdIntTypeType,
                  expr.namedUdIntConstrainedType,
                  expr.namedUdIntTypeConstrainedType,
                  expr.namedUdRainbowColours,
                  expr.anonUdRainbowColours);
        }
    }

    private final BasicType targetType;
    private final Set<TypedExpression> convertibleExpressions;

    protected TestConvertible(final BasicType targetType, final TypedExpression... convertibles) {
        this.targetType = targetType;
        this.convertibleExpressions = new HashSet<TypedExpression>(Arrays.asList(convertibles));
    }

    protected TestConvertible(final BasicType targetType, final Set<TypedExpression> convertibles) {
        this.targetType = targetType;
        this.convertibleExpressions = convertibles;
    }

    private void checkResult(final TypedExpression convertible) {
        final String
                message =
                targetType +
                " ( " +
                (convertible.getType().isAnonymousType() ? "anon " : "") +
                convertible.getType() +
                " )";
        if (convertibleExpressions.contains(convertible)) {
            assertTrue(message + " should be convertible", targetType.isConvertibleFrom(convertible.getType()));
        } else {
            assertFalse(message + " should not be convertible", targetType.isConvertibleFrom(convertible.getType()));
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
                    TestConvertible.class.getDeclaredMethod(reqdMethod);
                } catch (final NoSuchMethodException e) {
                    System.err.println("Warning : " + fieldName + " not tested for in TestConvertible");
                }
            }
        }
    }

}
