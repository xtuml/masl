/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
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
import java.util.Arrays;

import org.xtuml.masl.metamodelImpl.expression.TypedExpression;

public class TestExpressions {

    public static final TestExpressions expr1 = new TestExpressions(TestTypes.data1);
    public static final TestExpressions expr2 = new TestExpressions(TestTypes.data2);

    /**
     * Named expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#bBoolean bBoolean}
     */
    public final TypedExpression namedBBoolean;
    /**
     * Named expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#bCharacter bCharacter}
     */
    public final TypedExpression namedBCharacter;
    /**
     * Named expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#bDevice bDevice}
     */
    public final TypedExpression namedBDevice;
    /**
     * Named expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#bDuration bDuration}
     */
    public final TypedExpression namedBDuration;
    /**
     * Named expression of type {@link org.xtuml.masl.metamodelImpl.TestTypes#bEvent
     * bEvent}
     */
    public final TypedExpression namedBEvent;

    /**
     * Named expression of type {@link org.xtuml.masl.metamodelImpl.TestTypes#bByte
     * bByte}
     */
    public final TypedExpression namedBByte;
    /**
     * Named expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#bInteger bInteger}
     */
    public final TypedExpression namedBInteger;
    /**
     * Named expression of type {@link org.xtuml.masl.metamodelImpl.TestTypes#bReal
     * bReal}
     */
    public final TypedExpression namedBReal;
    /**
     * Named expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#bString bString}
     */
    public final TypedExpression namedBString;
    /**
     * Named expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#bTimestamp bTimestamp}
     */
    public final TypedExpression namedBTimestamp;
    /**
     * Named expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#bWCharacter bWCharacter}
     */
    public final TypedExpression namedBWCharacter;

    /**
     * Named expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#bWString bWString}
     */
    public final TypedExpression namedBWString;

    /**
     * Named expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#udIntType udIntType}
     */
    public final TypedExpression namedUdIntType;

    /**
     * Named expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#udRealType udRealType}
     */
    public final TypedExpression namedUdRealType;

    /**
     * Named expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#udIntConstrainedType
     * udIntConstrainedType}
     */
    public final TypedExpression namedUdIntConstrainedType;

    /**
     * Named expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#udIntTypeConstrainedType
     * udIntTypeConstrainedType}
     */
    public final TypedExpression namedUdIntTypeConstrainedType;

    /**
     * Named expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#udCharacterType
     * udCharacterType}
     */
    public final TypedExpression namedUdCharacterType;

    /**
     * Named expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#udCharacterType
     * udWCharacterType}
     */
    public final TypedExpression namedUdWCharacterType;

    /**
     * Named expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#udCharacterType
     * udCharacterType}
     */
    public final TypedExpression namedUdStringType;

    /**
     * Named expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#udCharacterType
     * udWCharacterType}
     */
    public final TypedExpression namedUdWStringType;

    /**
     * Named expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#udIntTypeType udIntTypeType}
     */
    public final TypedExpression namedUdIntTypeType;

    /**
     * Named expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#seqOfReal seqOfReal}
     */
    public final TypedExpression namedSeqOfReal;

    /**
     * Named expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#seqOfCharacter seqOfCharacter}
     */
    public final TypedExpression namedSeqOfCharacter;

    /**
     * Named expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#seqOfCharacter seqOfWCharacter}
     */
    public final TypedExpression namedSeqOfWCharacter;

    /**
     * Named expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#seqOfInt seqOfInt}
     */
    public final TypedExpression namedSeqOfInt;

    /**
     * Named expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#setOfInt setOfInt}
     */
    public final TypedExpression namedSetOfInt;

    /**
     * Named expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#setOfCharacter setOfCharacter}
     */
    public final TypedExpression namedSetOfCharacter;

    /**
     * Named expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#setOfWCharacter
     * setOfWCharacter}
     */
    public final TypedExpression namedSetOfWCharacter;

    /**
     * Named expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#bagOfInt bagOfInt}
     */
    public final TypedExpression namedBagOfInt;

    /**
     * Named expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#array1to10OfInt
     * array1to10OfInt}
     */
    public final TypedExpression namedArray1to10OfInt;

    /**
     * Named expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#array2to11OfInt
     * array2to11OfInt}
     */
    public final TypedExpression namedArray2to11OfInt;

    /**
     * Named expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#seqOfSeqOfReal seqOfSeqOfReal}
     */
    public final TypedExpression namedSeqOfSeqOfReal;

    /**
     * Named expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#seqOfSeqOfInt seqOfSeqOfInt}
     */
    public final TypedExpression namedSeqOfSeqOfInt;

    /**
     * Named expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#seqOfSetOfInt seqOfSetOfInt}
     */
    public final TypedExpression namedSeqOfSetOfInt;

    /**
     * Named expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#seqOfBagOfInt seqOfBagOfInt}
     */
    public final TypedExpression namedSeqOfBagOfInt;

    /**
     * Named expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#seqOfArrayOfInt
     * seqOfArrayOfInt}
     */
    public final TypedExpression namedSeqOfArrayOfInt;

    /**
     * Named expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#seqOfUdIntType seqOfUdIntType}
     */
    public final TypedExpression namedSeqOfUdIntType;

    /**
     * Named expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#seqOfUdCharacterType
     * seqOfUdCharacterType}
     */
    public final TypedExpression namedSeqOfUdCharacterType;

    /**
     * Named expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#seqOfUdWCharacterType
     * seqOfUdWCharacterype}
     */
    public final TypedExpression namedSeqOfUdWCharacterType;

    /**
     * Named expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#seqOfUdIntTypeType
     * seqOfUdIntTypeType}
     */
    public final TypedExpression namedSeqOfUdIntTypeType;

    /**
     * Named expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#setOfSeqOfInt setOfSeqOfInt}
     */
    public final TypedExpression namedSetOfSeqOfInt;

    /**
     * Named expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#setOfSetOfInt setOfSetOfInt}
     */
    public final TypedExpression namedSetOfSetOfInt;

    /**
     * Named expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#setOfBagOfInt setOfBagOfInt}
     */
    public final TypedExpression namedSetOfBagOfInt;

    /**
     * Named expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#setOfArrayOfInt
     * setOfArrayOfInt}
     */
    public final TypedExpression namedSetOfArrayOfInt;

    /**
     * Named expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#setOfUdIntType setOfUdIntType}
     */
    public final TypedExpression namedSetOfUdIntType;

    /**
     * Named expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#setOfUdIntTypeType
     * setOfUdIntTypeType}
     */
    public final TypedExpression namedSetOfUdIntTypeType;

    /**
     * Named expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#bagOfSeqOfInt bagOfSeqOfInt}
     */
    public final TypedExpression namedBagOfSeqOfInt;

    /**
     * Named expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#bagOfSetOfInt bagOfSetOfInt}
     */
    public final TypedExpression namedBagOfSetOfInt;

    /**
     * Named expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#bagOfBagOfInt bagOfBagOfInt}
     */
    public final TypedExpression namedBagOfBagOfInt;

    /**
     * Named expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#bagOfArrayOfInt
     * bagOfArrayOfInt}
     */
    public final TypedExpression namedBagOfArrayOfInt;

    /**
     * Named expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#bagOfUdIntType bagOfUdIntType}
     */
    public final TypedExpression namedBagOfUdIntType;

    /**
     * Named expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#bagOfUdIntTypeType
     * bagOfUdIntTypeType}
     */
    public final TypedExpression namedBagOfUdIntTypeType;

    /**
     * Named expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#arrayOfSeqOfInt
     * arrayOfSeqOfInt}
     */
    public final TypedExpression namedArrayOfSeqOfInt;

    /**
     * Named expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#arrayOfSetOfInt
     * arrayOfSetOfInt}
     */
    public final TypedExpression namedArrayOfSetOfInt;

    /**
     * Named expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#arrayOfBagOfInt
     * arrayOfBagOfInt}
     */
    public final TypedExpression namedArrayOfBagOfInt;

    /**
     * Named expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#arrayOfArrayOfInt
     * arrayOfArrayOfInt}
     */
    public final TypedExpression namedArrayOfArrayOfInt;

    /**
     * Named expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#arrayOfUdIntType
     * arrayOfUdIntType}
     */
    public final TypedExpression namedArrayOfUdIntType;

    /**
     * Named expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#arrayOfUdIntTypeType
     * arrayOfUdIntTypeType}
     */
    public final TypedExpression namedArrayOfUdIntTypeType;

    /**
     * Named expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#udSeqOfIntType udSeqOfIntType}
     */
    public final TypedExpression namedUdSeqOfIntType;

    /**
     * Named expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#udSetOfIntType udSetOfIntType}
     */
    public final TypedExpression namedUdSetOfIntType;

    /**
     * Named expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#udBagOfIntType udBagOfIntType}
     */
    public final TypedExpression namedUdBagOfIntType;

    /**
     * Named expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#udArrayOfIntType
     * udArrayOfIntType}
     */
    public final TypedExpression namedUdArray1to10OfIntType;

    /**
     * Named expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#udTrivialStructType
     * udTrivialStructType}
     */
    public final TypedExpression namedUdTrivialStructType;

    /**
     * Named expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#udSimpleStructType
     * udSimpleStructType}
     */
    public final TypedExpression namedUdSimpleStructType;

    /**
     * Named expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#udComplexStructType
     * udComplexStructType}
     */
    public final TypedExpression namedUdComplexStructType;

    /**
     * Named expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#udPrimaryColours
     * udPrimaryColours}
     */
    public final TypedExpression namedUdPrimaryColours;

    /**
     * Named expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#udRainbowColours
     * udRainbowColours}
     */
    public final TypedExpression namedUdRainbowColours;

    /**
     * Named expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#instance1 instance1}
     */
    public final TypedExpression namedInstance1;

    /**
     * Named expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#instance2 instance2}
     */
    public final TypedExpression namedInstance2;

    /**
     * Named expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#anyInstance anyInstance}
     */
    public final TypedExpression namedAnyInstance;

    /**
     * Anonymous expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#bBoolean bBoolean}
     */
    public final TypedExpression anonBBoolean;
    /**
     * Anonymous expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#bCharacter bCharacter}
     */
    public final TypedExpression anonBCharacter;
    /**
     * Anonymous expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#bDevice bDevice}
     */
    public final TypedExpression anonBDevice;
    /**
     * Anonymous expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#bDuration bDuration}
     */
    public final TypedExpression anonBDuration;
    /**
     * Anonymous expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#bEvent bEvent}
     */
    public final TypedExpression anonBEvent;
    /**
     * Anonymous expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#bByte bByte}
     */
    public final TypedExpression anonBByte;
    /**
     * Anonymous expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#bInteger bInteger}
     */
    public final TypedExpression anonBInteger;
    /**
     * Anonymous expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#bReal bReal}
     */
    public final TypedExpression anonBReal;
    /**
     * Anonymous expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#bString bString}
     */
    public final TypedExpression anonBString;
    /**
     * Anonymous expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#bTimestamp bTimestamp}
     */
    public final TypedExpression anonBTimestamp;
    /**
     * Anonymous expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#bWCharacter bWCharacter}
     */
    public final TypedExpression anonBWCharacter;

    /**
     * Anonymous expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#bWString bWString}
     */
    public final TypedExpression anonBWString;

    /**
     * Anonymous expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#seqOfReal seqOfReal}
     */
    public final TypedExpression anonSeqOfReal;

    /**
     * Anonymous expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#seqOfInt seqOfInt}
     */
    public final TypedExpression anonSeqOfInt;

    /**
     * Anonymous expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#seqOfCharacter seqOfCharacter}
     */
    public final TypedExpression anonSeqOfCharacter;

    /**
     * Anonymous expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#seqOfWCharacter
     * seqOfWCharacter}
     */
    public final TypedExpression anonSeqOfWCharacter;

    /**
     * Anonymous expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#setOfInt setOfInt}
     */
    public final TypedExpression anonSetOfInt;

    /**
     * Anonymous expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#setOfCharacter setOfCharacter}
     */
    public final TypedExpression anonSetOfCharacter;

    /**
     * Anonymous expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#setOfWCharacter
     * setOfWCharacter}
     */
    public final TypedExpression anonSetOfWCharacter;

    /**
     * Anonymous expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#bagOfInt bagOfInt}
     */
    public final TypedExpression anonBagOfInt;

    /**
     * Anonymous expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#seqOfReal seqOfReal}
     */
    public final TypedExpression anonSeqOfAnonReal;

    /**
     * Anonymous expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#seqOfInt seqOfInt}
     */
    public final TypedExpression anonSeqOfAnonInt;

    /**
     * Anonymous expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#seqOfCharacter seqOfCharacter}
     */
    public final TypedExpression anonSeqOfAnonCharacter;

    /**
     * Anonymous expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#seqOfWCharacter
     * seqOfWCharacter}
     */
    public final TypedExpression anonSeqOfAnonWCharacter;

    /**
     * Anonymous expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#setOfInt setOfInt}
     */
    public final TypedExpression anonSetOfAnonInt;

    /**
     * Anonymous expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#bagOfInt bagOfInt}
     */
    public final TypedExpression anonBagOfAnonInt;

    /**
     * Anonymous expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#seqOfSeqOfReal seqOfSeqOfReal}
     */
    public final TypedExpression anonSeqOfSeqOfReal;

    /**
     * Anonymous expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#seqOfSeqOfInt seqOfSeqOfInt}
     */
    public final TypedExpression anonSeqOfSeqOfInt;

    /**
     * Anonymous expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#seqOfSetOfInt seqOfSetOfInt}
     */
    public final TypedExpression anonSeqOfSetOfInt;

    /**
     * Anonymous expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#seqOfBagOfInt seqOfBagOfInt}
     */
    public final TypedExpression anonSeqOfBagOfInt;

    /**
     * Anonymous expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#seqOfArrayOfInt
     * seqOfArrayOfInt}
     */
    public final TypedExpression anonSeqOfArrayOfInt;

    /**
     * Anonymous expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#seqOfUdIntType seqOfUdIntType}
     */
    public final TypedExpression anonSeqOfUdIntType;

    /**
     * Anonymous expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#seqOfUdCharacterType
     * seqOfUdCharacterType}
     */
    public final TypedExpression anonSeqOfUdCharacterType;

    /**
     * Anonymous expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#seqOfUdWCharacterType
     * seqOfUdWCharacterType}
     */
    public final TypedExpression anonSeqOfUdWCharacterType;

    /**
     * Anonymous expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#seqOfUdIntTypeType
     * seqOfUdIntTypeType}
     */
    public final TypedExpression anonSeqOfUdIntTypeType;

    /**
     * Anonymous expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#setOfSeqOfInt setOfSeqOfInt}
     */
    public final TypedExpression anonSetOfSeqOfInt;

    /**
     * Anonymous expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#setOfSetOfInt setOfSetOfInt}
     */
    public final TypedExpression anonSetOfSetOfInt;

    /**
     * Anonymous expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#setOfBagOfInt setOfBagOfInt}
     */
    public final TypedExpression anonSetOfBagOfInt;

    /**
     * Anonymous expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#setOfArrayOfInt
     * setOfArrayOfInt}
     */
    public final TypedExpression anonSetOfArrayOfInt;

    /**
     * Anonymous expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#setOfUdIntType setOfUdIntType}
     */
    public final TypedExpression anonSetOfUdIntType;

    /**
     * Anonymous expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#setOfUdIntTypeType
     * setOfUdIntTypeType}
     */
    public final TypedExpression anonSetOfUdIntTypeType;

    /**
     * Anonymous expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#bagOfSeqOfInt bagOfSeqOfInt}
     */
    public final TypedExpression anonBagOfSeqOfInt;

    /**
     * Anonymous expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#bagOfSetOfInt bagOfSetOfInt}
     */
    public final TypedExpression anonBagOfSetOfInt;

    /**
     * Anonymous expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#bagOfBagOfInt bagOfBagOfInt}
     */
    public final TypedExpression anonBagOfBagOfInt;

    /**
     * Anonymous expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#bagOfArrayOfInt
     * bagOfArrayOfInt}
     */
    public final TypedExpression anonBagOfArrayOfInt;

    /**
     * Anonymous expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#bagOfUdIntType bagOfUdIntType}
     */
    public final TypedExpression anonBagOfUdIntType;

    /**
     * Anonymous expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#bagOfUdIntTypeType
     * bagOfUdIntTypeType}
     */
    public final TypedExpression anonBagOfUdIntTypeType;

    /**
     * Anonymous expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#anonSimpleStruct
     * anonSimpleStruct}
     */
    public final TypedExpression anonAnonTrivialStruct;

    /**
     * Anonymous expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#anonSimpleStruct
     * anonSimpleStruct}
     */
    public final TypedExpression anonAnonSimpleStruct;

    /**
     * Anonymous expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#anonComplexStruct
     * anonComplexStruct}
     */
    public final TypedExpression anonAnonComplexStruct;

    /**
     * Anonymous expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#anonSimpleStruct
     * anonSimpleStruct}
     */
    public final TypedExpression anonAnonTrivialAnonStruct;

    /**
     * Anonymous expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#anonSimpleStruct
     * anonSimpleStruct}
     */
    public final TypedExpression anonAnonSimpleAnonStruct;

    /**
     * Anonymous expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#anonSimpleStruct
     * anonSimpleStruct}
     */
    public final TypedExpression anonAnonSimpleAnonStruct2;

    /**
     * Anonymous expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#anonComplexAnonStruct
     * anonComplexStruct}
     */
    public final TypedExpression anonAnonComplexAnonStruct;

    /**
     * Anonymous expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#udPrimaryColours
     * udPrimaryColours}
     */
    public final TypedExpression anonUdPrimaryColours;

    /**
     * Anonymous expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#udRainbowColours
     * udRainbowColours}
     */
    public final TypedExpression anonUdRainbowColours;

    /**
     * Anonymous expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#instance1 instance1}
     */
    public final TypedExpression anonInstance1;

    /**
     * Anonymous expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#instance2 instance2}
     */
    public final TypedExpression anonInstance2;

    /**
     * Anonymous expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#anyInstance anyInstance}
     */
    public final TypedExpression anonAnyInstance;

    private TestExpressions(final TestTypes types) {
        namedBBoolean = new TypedExpression(types.bBoolean);
        namedBCharacter = new TypedExpression(types.bCharacter);
        namedBDevice = new TypedExpression(types.bDevice);
        namedBDuration = new TypedExpression(types.bDuration);
        namedBEvent = new TypedExpression(types.bEvent);
        namedBByte = new TypedExpression(types.bByte);
        namedBInteger = new TypedExpression(types.bInteger);
        namedBReal = new TypedExpression(types.bReal);
        namedBString = new TypedExpression(types.bString);
        namedBTimestamp = new TypedExpression(types.bTimestamp);
        namedBWCharacter = new TypedExpression(types.bWCharacter);
        namedBWString = new TypedExpression(types.bWString);
        namedUdIntType = new TypedExpression(types.udIntType);
        namedUdRealType = new TypedExpression(types.udRealType);
        namedUdCharacterType = new TypedExpression(types.udCharacterType);
        namedUdWCharacterType = new TypedExpression(types.udWCharacterType);
        namedUdStringType = new TypedExpression(types.udStringType);
        namedUdWStringType = new TypedExpression(types.udWStringType);
        namedUdIntTypeType = new TypedExpression(types.udIntTypeType);
        namedUdIntConstrainedType = new TypedExpression(types.udIntConstrainedType);
        namedUdIntTypeConstrainedType = new TypedExpression(types.udIntTypeConstrainedType);
        namedSeqOfReal = new TypedExpression(types.seqOfReal);
        namedSeqOfInt = new TypedExpression(types.seqOfInt);
        namedSeqOfCharacter = new TypedExpression(types.seqOfCharacter);
        namedSeqOfWCharacter = new TypedExpression(types.seqOfWCharacter);
        namedSetOfInt = new TypedExpression(types.setOfInt);
        namedSetOfCharacter = new TypedExpression(types.setOfCharacter);
        namedSetOfWCharacter = new TypedExpression(types.setOfWCharacter);
        namedBagOfInt = new TypedExpression(types.bagOfInt);
        namedArray1to10OfInt = new TypedExpression(types.array1to10OfInt);
        namedArray2to11OfInt = new TypedExpression(types.array2to11OfInt);
        namedSeqOfSeqOfReal = new TypedExpression(types.seqOfSeqOfReal);
        namedSeqOfSeqOfInt = new TypedExpression(types.seqOfSeqOfInt);
        namedSeqOfSetOfInt = new TypedExpression(types.seqOfSetOfInt);
        namedSeqOfBagOfInt = new TypedExpression(types.seqOfBagOfInt);
        namedSeqOfArrayOfInt = new TypedExpression(types.seqOfArrayOfInt);
        namedSeqOfUdIntType = new TypedExpression(types.seqOfUdIntType);
        namedSeqOfUdCharacterType = new TypedExpression(types.seqOfUdCharacterType);
        namedSeqOfUdWCharacterType = new TypedExpression(types.seqOfUdWCharacterType);
        namedSeqOfUdIntTypeType = new TypedExpression(types.seqOfUdIntTypeType);
        namedSetOfSeqOfInt = new TypedExpression(types.setOfSeqOfInt);
        namedSetOfSetOfInt = new TypedExpression(types.setOfSetOfInt);
        namedSetOfBagOfInt = new TypedExpression(types.setOfBagOfInt);
        namedSetOfArrayOfInt = new TypedExpression(types.setOfArrayOfInt);
        namedSetOfUdIntType = new TypedExpression(types.setOfUdIntType);
        namedSetOfUdIntTypeType = new TypedExpression(types.setOfUdIntTypeType);
        namedBagOfSeqOfInt = new TypedExpression(types.bagOfSeqOfInt);
        namedBagOfSetOfInt = new TypedExpression(types.bagOfSetOfInt);
        namedBagOfBagOfInt = new TypedExpression(types.bagOfBagOfInt);
        namedBagOfArrayOfInt = new TypedExpression(types.bagOfArrayOfInt);
        namedBagOfUdIntType = new TypedExpression(types.bagOfUdIntType);
        namedBagOfUdIntTypeType = new TypedExpression(types.bagOfUdIntTypeType);
        namedArrayOfSeqOfInt = new TypedExpression(types.arrayOfSeqOfInt);
        namedArrayOfSetOfInt = new TypedExpression(types.arrayOfSetOfInt);
        namedArrayOfBagOfInt = new TypedExpression(types.arrayOfBagOfInt);
        namedArrayOfArrayOfInt = new TypedExpression(types.arrayOfArrayOfInt);
        namedArrayOfUdIntType = new TypedExpression(types.arrayOfUdIntType);
        namedArrayOfUdIntTypeType = new TypedExpression(types.arrayOfUdIntTypeType);
        namedUdSeqOfIntType = new TypedExpression(types.udSeqOfIntType);
        namedUdSetOfIntType = new TypedExpression(types.udSetOfIntType);
        namedUdBagOfIntType = new TypedExpression(types.udBagOfIntType);
        namedUdArray1to10OfIntType = new TypedExpression(types.udArray1to10OfIntType);
        namedUdTrivialStructType = new TypedExpression(types.udTrivialStructType);
        namedUdSimpleStructType = new TypedExpression(types.udSimpleStructType);
        namedUdComplexStructType = new TypedExpression(types.udComplexStructType);
        namedUdPrimaryColours = new TypedExpression(types.udPrimaryColours);
        namedUdRainbowColours = new TypedExpression(types.udRainbowColours);

        namedInstance1 = new TypedExpression(types.instance1);
        namedInstance2 = new TypedExpression(types.instance2);
        namedAnyInstance = new TypedExpression(types.anyInstance);

        anonBBoolean = new TypedExpression(BooleanType.createAnonymous());
        anonBCharacter = new TypedExpression(CharacterType.createAnonymous());
        anonBDevice = new TypedExpression(DeviceType.createAnonymous());
        anonBDuration = new TypedExpression(DurationType.createAnonymous());
        anonBEvent = new TypedExpression(EventType.createAnonymous());
        anonBByte = new TypedExpression(ByteType.createAnonymous());
        anonBInteger = new TypedExpression(IntegerType.createAnonymous());
        anonBReal = new TypedExpression(RealType.createAnonymous());
        anonBString = new TypedExpression(StringType.createAnonymous());
        anonBTimestamp = new TypedExpression(TimestampType.createAnonymous());
        anonBWCharacter = new TypedExpression(WCharacterType.createAnonymous());
        anonBWString = new TypedExpression(WStringType.createAnonymous());

        anonSeqOfReal = new TypedExpression(SequenceType.createAnonymous(types.bReal));
        anonSeqOfInt = new TypedExpression(SequenceType.createAnonymous(types.bInteger));
        anonSeqOfCharacter = new TypedExpression(SequenceType.createAnonymous(types.bCharacter));
        anonSeqOfWCharacter = new TypedExpression(SequenceType.createAnonymous(types.bWCharacter));

        anonSetOfInt = new TypedExpression(SetType.createAnonymous(types.bInteger));
        anonSetOfCharacter = new TypedExpression(SetType.createAnonymous(types.bCharacter));
        anonSetOfWCharacter = new TypedExpression(SetType.createAnonymous(types.bWCharacter));

        anonBagOfInt = new TypedExpression(BagType.createAnonymous(types.bInteger));

        anonSeqOfSeqOfReal = new TypedExpression(SequenceType.createAnonymous(types.seqOfReal));

        anonSeqOfSeqOfInt = new TypedExpression(SequenceType.createAnonymous(types.seqOfInt));
        anonSeqOfSetOfInt = new TypedExpression(SequenceType.createAnonymous(types.setOfInt));
        anonSeqOfBagOfInt = new TypedExpression(SequenceType.createAnonymous(types.bagOfInt));
        anonSeqOfArrayOfInt = new TypedExpression(SequenceType.createAnonymous(types.array1to10OfInt));
        anonSeqOfUdIntType = new TypedExpression(SequenceType.createAnonymous(types.udIntType));
        anonSeqOfUdCharacterType = new TypedExpression(SequenceType.createAnonymous(types.udCharacterType));
        anonSeqOfUdWCharacterType = new TypedExpression(SequenceType.createAnonymous(types.udWCharacterType));
        anonSeqOfUdIntTypeType = new TypedExpression(SequenceType.createAnonymous(types.udIntTypeType));

        anonSetOfSeqOfInt = new TypedExpression(SetType.createAnonymous(types.seqOfInt));
        anonSetOfSetOfInt = new TypedExpression(SetType.createAnonymous(types.setOfInt));
        anonSetOfBagOfInt = new TypedExpression(SetType.createAnonymous(types.bagOfInt));
        anonSetOfArrayOfInt = new TypedExpression(SetType.createAnonymous(types.array1to10OfInt));
        anonSetOfUdIntType = new TypedExpression(SetType.createAnonymous(types.udIntType));
        anonSetOfUdIntTypeType = new TypedExpression(SetType.createAnonymous(types.udIntTypeType));

        anonBagOfSeqOfInt = new TypedExpression(BagType.createAnonymous(types.seqOfInt));
        anonBagOfSetOfInt = new TypedExpression(BagType.createAnonymous(types.setOfInt));
        anonBagOfBagOfInt = new TypedExpression(BagType.createAnonymous(types.bagOfInt));
        anonBagOfArrayOfInt = new TypedExpression(BagType.createAnonymous(types.array1to10OfInt));
        anonBagOfUdIntType = new TypedExpression(BagType.createAnonymous(types.udIntType));
        anonBagOfUdIntTypeType = new TypedExpression(BagType.createAnonymous(types.udIntTypeType));

        anonAnonTrivialStruct = new TypedExpression(types.anonTrivialStruct);
        anonAnonSimpleStruct = new TypedExpression(types.anonSimpleStruct);
        anonAnonComplexStruct = new TypedExpression(types.anonComplexStruct);

        anonAnonTrivialAnonStruct = new TypedExpression(
                new AnonymousStructure(Arrays.asList(new BasicType[] { IntegerType.createAnonymous() })));

        anonAnonSimpleAnonStruct = new TypedExpression(new AnonymousStructure(
                Arrays.asList(new BasicType[] { IntegerType.createAnonymous(), StringType.createAnonymous(),
                        WStringType.createAnonymous(), IntegerType.createAnonymous(), ByteType.createAnonymous() })));

        anonAnonSimpleAnonStruct2 = new TypedExpression(new AnonymousStructure(
                Arrays.asList(new BasicType[] { IntegerType.createAnonymous(), StringType.createAnonymous(),
                        WStringType.createAnonymous(), types.udIntTypeType, ByteType.createAnonymous() })));

        anonAnonComplexAnonStruct = new TypedExpression(new AnonymousStructure(
                Arrays.asList(new BasicType[] { types.anonSimpleStruct, SequenceType.createAnonymous(types.bInteger),
                        SetType.createAnonymous(BagType.createAnonymous(IntegerType.createAnonymous())) })));

        anonUdPrimaryColours = new TypedExpression(types.udPrimaryColoursDecl.getDeclaredType());
        anonUdRainbowColours = new TypedExpression(types.udRainbowColoursDecl.getDeclaredType());

        anonSeqOfAnonReal = new TypedExpression(SequenceType.createAnonymous(RealType.createAnonymous()));
        anonSeqOfAnonInt = new TypedExpression(SequenceType.createAnonymous(IntegerType.createAnonymous()));
        anonSeqOfAnonCharacter = new TypedExpression(SequenceType.createAnonymous(CharacterType.createAnonymous()));
        anonSeqOfAnonWCharacter = new TypedExpression(SequenceType.createAnonymous(WCharacterType.createAnonymous()));
        anonSetOfAnonInt = new TypedExpression(SetType.createAnonymous(IntegerType.createAnonymous()));
        anonBagOfAnonInt = new TypedExpression(BagType.createAnonymous(IntegerType.createAnonymous()));

        anonInstance1 = new TypedExpression(InstanceType.createAnonymous(types.object1));
        anonInstance2 = new TypedExpression(InstanceType.createAnonymous(types.object2));
        anonAnyInstance = new TypedExpression(AnyInstanceType.createAnonymous());

    }

}
