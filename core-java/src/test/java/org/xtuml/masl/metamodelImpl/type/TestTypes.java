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

import org.xtuml.masl.metamodelImpl.common.PragmaList;
import org.xtuml.masl.metamodelImpl.common.Visibility;
import org.xtuml.masl.metamodelImpl.domain.Domain;
import org.xtuml.masl.metamodelImpl.expression.IntegerLiteral;
import org.xtuml.masl.metamodelImpl.expression.MinMaxRange;
import org.xtuml.masl.metamodelImpl.expression.RangeExpression;
import org.xtuml.masl.metamodelImpl.object.ObjectDeclaration;

public final class TestTypes {

    static public final Domain domain = new Domain(null, "TestTypes");

    static private final PragmaList pragmas = new PragmaList();

    public static TestTypes data1 = new TestTypes("data1");
    public static TestTypes data2 = new TestTypes("data2");

    private TestTypes(final String prefix) {

        bBoolean = BooleanType.create(null, false);
        bCharacter = CharacterType.create(null, false);
        bDevice = DeviceType.create(null, false);
        bDuration = DurationType.create(null, false);
        bEvent = EventType.create(null, false);
        bByte = ByteType.create(null, false);
        bInteger = IntegerType.create(null, false);
        bReal = RealType.create(null, false);
        bString = StringType.create(null, false);
        bTimestamp = TimestampType.create(null, false);
        bWCharacter = WCharacterType.create(null, false);
        bWString = WStringType.create(null, false);

        udIntTypeDecl = TypeDeclaration.create(null, domain, prefix + "udIntType", Visibility.PUBLIC, bInteger,
                pragmas);
        udIntType = UserDefinedType.create(domain.getReference(null), prefix + "udIntType");

        udRealTypeDecl = TypeDeclaration.create(null, domain, prefix + "udRealType", Visibility.PUBLIC, bReal, pragmas);
        udRealType = UserDefinedType.create(domain.getReference(null), prefix + "udRealType");

        udCharacterTypeDecl = TypeDeclaration.create(null, domain, prefix + "udCharacterType", Visibility.PUBLIC,
                bCharacter, pragmas);
        udCharacterType = UserDefinedType.create(domain.getReference(null), prefix + "udCharacterType");

        udWCharacterTypeDecl = TypeDeclaration.create(null, domain, prefix + "udWCharacterType", Visibility.PUBLIC,
                bWCharacter, pragmas);
        udWCharacterType = UserDefinedType.create(domain.getReference(null), prefix + "udWCharacterType");

        udStringTypeDecl = TypeDeclaration.create(null, domain, prefix + "udStringType", Visibility.PUBLIC, bString,
                pragmas);
        udStringType = UserDefinedType.create(domain.getReference(null), prefix + "udStringType");

        udWStringTypeDecl = TypeDeclaration.create(null, domain, prefix + "udWStringType", Visibility.PUBLIC, bWString,
                pragmas);
        udWStringType = UserDefinedType.create(domain.getReference(null), prefix + "udWStringType");

        udIntTypeTypeDecl = TypeDeclaration.create(null, domain, prefix + "udIntTypeType", Visibility.PUBLIC, udIntType,
                pragmas);
        udIntTypeType = UserDefinedType.create(domain.getReference(null), prefix + "udIntTypeType");

        oneToTen = new MinMaxRange(new IntegerLiteral(1l), new IntegerLiteral(10l));
        twoToEleven = new MinMaxRange(new IntegerLiteral(2l), new IntegerLiteral(11l));
        threeToThree = new MinMaxRange(new IntegerLiteral(3l), new IntegerLiteral(3l));

        udIntConstrainedTypeDecl = TypeDeclaration.create(null, domain, prefix + "udIntConstrainedType",
                Visibility.PUBLIC, new ConstrainedType(bInteger, new RangeConstraint(oneToTen)), pragmas);
        udIntConstrainedType = UserDefinedType.create(domain.getReference(null), prefix + "udIntConstrainedType");

        udIntTypeConstrainedTypeDecl = TypeDeclaration.create(null, domain, prefix + "udIntTypeConstrainedType",
                Visibility.PUBLIC, new ConstrainedType(udIntType, new RangeConstraint(oneToTen)), pragmas);
        udIntTypeConstrainedType = UserDefinedType.create(domain.getReference(null),
                prefix + "udIntTypeConstrainedType");

        seqOfReal = SequenceType.create(null, bReal, null, false);

        seqOfCharacter = SequenceType.create(null, bCharacter, null, false);

        seqOfWCharacter = SequenceType.create(null, bWCharacter, null, false);

        seqOfInt = SequenceType.create(null, bInteger, null, false);

        setOfInt = SetType.create(null, bInteger, false);

        setOfCharacter = SetType.create(null, bCharacter, false);
        setOfWCharacter = SetType.create(null, bWCharacter, false);

        bagOfInt = BagType.create(null, bInteger, false);

        array1to10OfInt = ArrayType.create(null, bInteger, oneToTen, false);
        array2to11OfInt = ArrayType.create(null, bInteger, twoToEleven, false);
        array3to3OfInt = ArrayType.create(null, bInteger, threeToThree, false);

        seqOfSeqOfReal = SequenceType.create(null, seqOfReal, null, false);

        seqOfSeqOfInt = SequenceType.create(null, seqOfInt, null, false);

        seqOfSetOfInt = SequenceType.create(null, setOfInt, null, false);

        seqOfBagOfInt = SequenceType.create(null, bagOfInt, null, false);

        seqOfArrayOfInt = SequenceType.create(null, array1to10OfInt, null, false);

        seqOfUdIntType = SequenceType.create(null, udIntType, null, false);

        seqOfUdCharacterType = SequenceType.create(null, udCharacterType, null, false);

        seqOfUdWCharacterType = SequenceType.create(null, udWCharacterType, null, false);

        seqOfUdIntTypeType = SequenceType.create(null, udIntTypeType, null, false);

        setOfSeqOfInt = SetType.create(null, seqOfInt, false);

        setOfSetOfInt = SetType.create(null, setOfInt, false);

        setOfBagOfInt = SetType.create(null, bagOfInt, false);

        setOfArrayOfInt = SetType.create(null, array1to10OfInt, false);

        setOfUdIntType = SetType.create(null, udIntType, false);

        setOfUdIntTypeType = SetType.create(null, udIntTypeType, false);

        bagOfSeqOfInt = BagType.create(null, seqOfInt, false);

        bagOfSetOfInt = BagType.create(null, setOfInt, false);

        bagOfBagOfInt = BagType.create(null, bagOfInt, false);

        bagOfArrayOfInt = BagType.create(null, array1to10OfInt, false);

        bagOfUdIntType = BagType.create(null, udIntType, false);

        bagOfUdIntTypeType = BagType.create(null, udIntTypeType, false);

        arrayOfSeqOfInt = ArrayType.create(null, seqOfInt, oneToTen, false);

        arrayOfSetOfInt = ArrayType.create(null, setOfInt, oneToTen, false);

        arrayOfBagOfInt = ArrayType.create(null, bagOfInt, oneToTen, false);

        arrayOfArrayOfInt = ArrayType.create(null, array1to10OfInt, oneToTen, false);

        arrayOfUdIntType = ArrayType.create(null, udIntType, oneToTen, false);

        arrayOfUdIntTypeType = ArrayType.create(null, udIntTypeType, oneToTen, false);

        udSeqOfIntTypeDecl = TypeDeclaration.create(null, domain, prefix + "udSeqOfIntType", Visibility.PUBLIC,
                seqOfInt, pragmas);
        udSeqOfIntType = UserDefinedType.create(domain.getReference(null), prefix + "udSeqOfIntType");

        udSetOfIntTypeDecl = TypeDeclaration.create(null, domain, prefix + "udSetOfIntType", Visibility.PUBLIC,
                setOfInt, pragmas);
        udSetOfIntType = UserDefinedType.create(domain.getReference(null), prefix + "udSetOfIntType");

        udBagOfIntTypeDecl = TypeDeclaration.create(null, domain, prefix + "udBagOfIntType", Visibility.PUBLIC,
                bagOfInt, pragmas);
        udBagOfIntType = UserDefinedType.create(domain.getReference(null), prefix + "udBagOfIntType");

        udArray3to3OfIntTypeDecl = TypeDeclaration.create(null, domain, prefix + "udArray3to3OfIntType",
                Visibility.PUBLIC, array3to3OfInt, pragmas);
        udArray3to3OfIntType = UserDefinedType.create(domain.getReference(null), prefix + "udArray3to3OfIntType");

        udArray1to10OfIntTypeDecl = TypeDeclaration.create(null, domain, prefix + "udArray1to10OfIntType",
                Visibility.PUBLIC, array1to10OfInt, pragmas);
        udArray1to10OfIntType = UserDefinedType.create(domain.getReference(null), prefix + "udArray1to10OfIntType");

        udArray2to11OfIntTypeDecl = TypeDeclaration.create(null, domain, prefix + "udArray2to11OfIntType",
                Visibility.PUBLIC, array1to10OfInt, pragmas);
        udArray2to11OfIntType = UserDefinedType.create(domain.getReference(null), prefix + "udArray2to11OfIntType");

        trivialStruct = StructureType.create(null,
                Arrays.asList(new StructureElement[] { StructureElement.create("elt", udIntType, null, pragmas) }));

        udTrivialStructTypeDecl = TypeDeclaration.create(null, domain, prefix + "udTrivialStructType",
                Visibility.PUBLIC, trivialStruct, pragmas);

        udTrivialStructType = UserDefinedType.create(domain.getReference(null), prefix + "udTrivialStructType");

        anonTrivialStruct = new AnonymousStructure(Arrays.asList(new BasicType[] { udIntType }));

        simpleStruct = StructureType.create(null,
                Arrays.asList(new StructureElement[] { StructureElement.create("elt1", bInteger, null, pragmas),
                        StructureElement.create("elt2", bString, null, pragmas),
                        StructureElement.create("elt3", bWString, null, pragmas),
                        StructureElement.create("elt4", udIntType, null, pragmas),
                        StructureElement.create("elt5", udIntTypeType, null, pragmas), }));

        udSimpleStructTypeDecl = TypeDeclaration.create(null, domain, prefix + "udSimpleStructType", Visibility.PUBLIC,
                simpleStruct, pragmas);

        udSimpleStructType = UserDefinedType.create(domain.getReference(null), prefix + "udSimpleStructType");

        anonSimpleStruct = new AnonymousStructure(
                Arrays.asList(new BasicType[] { bInteger, bString, bWString, udIntType, udIntTypeType, }));

        complexStruct = StructureType.create(null,
                Arrays.asList(
                        new StructureElement[] { StructureElement.create("elt1", udSimpleStructType, null, pragmas),
                                StructureElement.create("elt2", seqOfInt, null, pragmas),
                                StructureElement.create("elt3", seqOfSeqOfInt, null, pragmas) }));

        udComplexStructTypeDecl = TypeDeclaration.create(null, domain, prefix + "udComplexStructType",
                Visibility.PUBLIC, complexStruct, pragmas);

        udComplexStructType = UserDefinedType.create(domain.getReference(null), prefix + "udComplexStructType");

        anonComplexStruct = new AnonymousStructure(
                Arrays.asList(new BasicType[] { udSimpleStructType, seqOfInt, seqOfSeqOfInt }));

        primaryColours = EnumerateType.create(null, Arrays.asList(new EnumerateItem[] { new EnumerateItem("red"),
                new EnumerateItem("green"), new EnumerateItem("blue"), }));

        udPrimaryColoursDecl = TypeDeclaration.create(null, domain, prefix + "udPrimaryColoursType", Visibility.PUBLIC,
                primaryColours, pragmas);

        udPrimaryColours = UserDefinedType.create(domain.getReference(null), prefix + "udPrimaryColoursType");

        rainbowColours = EnumerateType.create(null,
                Arrays.asList(new EnumerateItem[] { new EnumerateItem("red"), new EnumerateItem("orange"),
                        new EnumerateItem("yellow"), new EnumerateItem("green"), new EnumerateItem("blue"),
                        new EnumerateItem("indigo"), new EnumerateItem("violet"), }));

        udRainbowColoursDecl = TypeDeclaration.create(null, domain, prefix + "udRainbowColoursType", Visibility.PUBLIC,
                rainbowColours, pragmas);

        udRainbowColours = UserDefinedType.create(domain.getReference(null), prefix + "udRainbowColoursType");

        object1 = ObjectDeclaration.create(null, domain, prefix + "Object1", pragmas);
        instance1 = InstanceType.create(null, object1.getReference(null), false);

        object2 = ObjectDeclaration.create(null, domain, prefix + "Object2", pragmas);
        instance2 = InstanceType.create(null, object2.getReference(null), false);

        anyInstance = AnyInstanceType.create(null, false);

    }

    public final BooleanType bBoolean;
    public final CharacterType bCharacter;
    public final DeviceType bDevice;
    public final DurationType bDuration;
    public final EventType bEvent;
    public final ByteType bByte;
    public final IntegerType bInteger;
    public final RealType bReal;
    public final StringType bString;
    public final TimestampType bTimestamp;
    public final WCharacterType bWCharacter;
    public final WStringType bWString;

    /**
     * <code>type udIntType is integer;</code>
     */
    public final UserDefinedType udIntType;
    public final TypeDeclaration udIntTypeDecl;

    /**
     * <code>type udIntType is integer;</code>
     */
    public final UserDefinedType udRealType;
    public final TypeDeclaration udRealTypeDecl;

    /**
     * <code>type udCharacterType is integer;</code>
     */
    public final UserDefinedType udCharacterType;
    public final TypeDeclaration udCharacterTypeDecl;

    /**
     * <code>type udWCharacterType is integer;</code>
     */
    public final UserDefinedType udWCharacterType;
    public final TypeDeclaration udWCharacterTypeDecl;

    /**
     * <code>type udCharacterType is integer;</code>
     */
    public final UserDefinedType udStringType;
    public final TypeDeclaration udStringTypeDecl;

    /**
     * <code>type udWCharacterType is integer;</code>
     */
    public final UserDefinedType udWStringType;
    public final TypeDeclaration udWStringTypeDecl;

    /**
     * <code>type udIntTypeType is {@link #udIntType};</code>
     */
    public final UserDefinedType udIntTypeType;
    public final TypeDeclaration udIntTypeTypeDecl;

    /**
     * <code>type udIntTypeConstrainedType is {@link #udIntType} range 1 .. 10;</code>
     */
    public final UserDefinedType udIntTypeConstrainedType;
    public final TypeDeclaration udIntTypeConstrainedTypeDecl;

    /**
     * <code>type udIntConstrainedType is integer range 1 .. 10;</code>
     */
    public final UserDefinedType udIntConstrainedType;
    public final TypeDeclaration udIntConstrainedTypeDecl;

    /**
     * <code>type udIntType is integer;</code>
     */
    /**
     * <code>1 .. 10</code>
     */
    public final RangeExpression oneToTen;

    /**
     * <code>2 .. 11</code>
     */
    public final RangeExpression twoToEleven;

    /**
     * <code>3 .. 3</code>
     */
    public final RangeExpression threeToThree;

    /**
     * <code>sequence of real</code>
     */
    public final SequenceType seqOfReal;

    /**
     * <code>sequence of character</code>
     */
    public final SequenceType seqOfCharacter;

    /**
     * <code>sequence of wcharacter</code>
     */
    public final SequenceType seqOfWCharacter;

    /**
     * <code>sequence of integer</code>
     */
    public final SequenceType seqOfInt;

    /**
     * <code>set of integer</code>
     */
    public final SetType setOfInt;

    /**
     * <code>set of character</code>
     */
    public final SetType setOfCharacter;

    /**
     * <code>set of wcharacter</code>
     */
    public final SetType setOfWCharacter;

    /**
     * <code>bag of integer</code>
     */
    public final BagType bagOfInt;

    /**
     * <code>array[1 .. 10] of integer</code>
     */
    public final ArrayType array1to10OfInt;

    /**
     * <code>array[2 .. 11] of integer</code>
     */
    public final ArrayType array2to11OfInt;

    /**
     * <code>array[3 .. 3] of integer</code>
     */
    public final ArrayType array3to3OfInt;

    /**
     * <code>sequence of sequence of real;</code>
     */
    public final SequenceType seqOfSeqOfReal;

    /**
     * <code>sequence of sequence of integer;</code>
     */
    public final SequenceType seqOfSeqOfInt;

    /**
     * <code>sequence of set of integer;</code>
     */
    public final SequenceType seqOfSetOfInt;

    /**
     * <code>sequence of bag of integer;</code>
     */
    public final SequenceType seqOfBagOfInt;

    /**
     * <code>sequence of array[1..10] of integer;</code>
     */
    public final SequenceType seqOfArrayOfInt;

    /**
     * <code>sequence of {@link #udIntType};</code>
     */
    public final SequenceType seqOfUdIntType;

    /**
     * <code>sequence of {@link #udCharacterType};</code>
     */
    public final SequenceType seqOfUdCharacterType;

    /**
     * <code>sequence of {@link #udWCharacterType};</code>
     */
    public final SequenceType seqOfUdWCharacterType;

    /**
     * <code>sequence of {@link #udIntTypeType};</code>
     */
    public final SequenceType seqOfUdIntTypeType;

    /**
     * <code>set of sequence of integer;</code>
     */
    public final SetType setOfSeqOfInt;

    /**
     * <code>set of set of integer;</code>
     */
    public final SetType setOfSetOfInt;

    /**
     * <code>set of bag of integer;</code>
     */
    public final SetType setOfBagOfInt;

    /**
     * <code>set of array[1..10] of integer;</code>
     */
    public final SetType setOfArrayOfInt;

    /**
     * <code>sequence of {@link #udIntType};</code>
     */
    public final SetType setOfUdIntType;

    /**
     * <code>sequence of {@link #udIntTypeType};</code>
     */
    public final SetType setOfUdIntTypeType;

    /**
     * <code>bag of sequence of integer;</code>
     */
    public final BagType bagOfSeqOfInt;

    /**
     * <code>bag of set of integer;</code>
     */
    public final BagType bagOfSetOfInt;

    /**
     * <code>bag of bag of integer;</code>
     */
    public final BagType bagOfBagOfInt;

    /**
     * <code>bag of array[1..10] of integer;</code>
     */

    public final BagType bagOfArrayOfInt;

    /**
     * <code>bag of {@link #udIntType};</code>
     */

    public final BagType bagOfUdIntType;

    /**
     * <code>bag of {@link #udIntTypeType};</code>
     */
    public final BagType bagOfUdIntTypeType;

    /**
     * <code>array[1..10] of sequence of integer;</code>
     */
    public final ArrayType arrayOfSeqOfInt;

    /**
     * <code>array[1..10] of set of integer;</code>
     */
    public final ArrayType arrayOfSetOfInt;

    /**
     * <code>array[1..10] of bag of integer;</code>
     */
    public final ArrayType arrayOfBagOfInt;

    /**
     * <code>array[1..10] of array[1..10] of integer;</code>
     */
    public final ArrayType arrayOfArrayOfInt;

    /**
     * <code>array[1..10] of {@link #udIntType};</code>
     */
    public final ArrayType arrayOfUdIntType;

    /**
     * <code>array[1..10] of {@link #udIntTypeType};</code>
     */
    public final ArrayType arrayOfUdIntTypeType;

    /**
     * <code>type SeqOfIntSubtype is sequence of integer;</code>
     */
    public final UserDefinedType udSeqOfIntType;
    public final TypeDeclaration udSeqOfIntTypeDecl;

    /**
     * <code>type SetOfIntSubtype is set of integer;</code>
     */
    public final UserDefinedType udSetOfIntType;
    public final TypeDeclaration udSetOfIntTypeDecl;

    /**
     * <code>type BagOfIntSubtype is bag of integer;</code>
     */
    public final UserDefinedType udBagOfIntType;
    public final TypeDeclaration udBagOfIntTypeDecl;

    /**
     * <code>type ArrayOfIntSubtype is array[1..10] of integer;</code>
     */
    public final UserDefinedType udArray1to10OfIntType;
    public final TypeDeclaration udArray1to10OfIntTypeDecl;

    /**
     * <code>type ArrayOfIntSubtype is array[3 .. 3] of integer;</code>
     */
    public final UserDefinedType udArray3to3OfIntType;
    public final TypeDeclaration udArray3to3OfIntTypeDecl;

    /**
     * <code>type ArrayOfIntSubtype is array[1..10] of integer;</code>
     */
    public final UserDefinedType udArray2to11OfIntType;
    public final TypeDeclaration udArray2to11OfIntTypeDecl;

    /**
     * <pre>
     *   structure
     *     elt : {@link #udIntType};
     *   end structure;
     * </pre>
     *
     */
    public final StructureType trivialStruct;

    /**
     * <pre>
     *   type udSimpleStructType is structure
     *     elt : {@link #udIntType};
     *   end structure;
     * </pre>
     *
     */
    public final UserDefinedType udTrivialStructType;
    public final TypeDeclaration udTrivialStructTypeDecl;

    /**
     * <pre>
     *   structure
     *     ? : {@link #udIntType};
     *   end structure;
     * </pre>
     */
    public final AnonymousStructure anonTrivialStruct;

    /**
     * <pre>
     *   structure
     *     elt1 : integer;
     *     elt2 : string;
     *     elt3 : wstring;
     *     elt4 : {@link #udIntType};
     *     elt5 : {@link #udIntTypeType};
     *   end structure;
     * </pre>
     *
     */
    public final StructureType simpleStruct;

    /**
     * <pre>
     *   type udSimpleStructType is structure
     *     elt1 : integer;
     *     elt2 : string;
     *     elt3 : wstring;
     *     elt4 : {@link #udIntType};
     *     elt5 : {@link #udIntTypeType};
     *   end structure;
     * </pre>
     *
     */
    public final UserDefinedType udSimpleStructType;
    public final TypeDeclaration udSimpleStructTypeDecl;

    /**
     * <pre>
     *   structure
     *     ? : integer;
     *     ? : string;
     *     ? : wstring;
     *     ? : {@link #udIntType};
     *     ? : {@link #udIntTypeType};
     *   end structure;
     * </pre>
     */
    public final AnonymousStructure anonSimpleStruct;

    /**
     * <pre>
     *   structure
     *     elt1 : {@link #udSimpleStructType};
     *     elt2 : sequence of integer;
     *     elt3 : sequence of sequence of integer;
     *   end structure;
     * </pre>
     */
    public final StructureType complexStruct;

    /**
     * <pre>
     *   type udComplexStructType is structure
     *     elt1 : {@link #udSimpleStructType};
     *     elt2 : sequence of integer;
     *     elt3 : sequence of sequence of integer;
     *   end structure;
     * </pre>
     */
    public final UserDefinedType udComplexStructType;
    public final TypeDeclaration udComplexStructTypeDecl;

    /**
     * <pre>
     *   structure
     *     ? : {@link #udSimpleStructType};
     *     ? : sequence of integer;
     *     ? : sequence of sequence of integer;
     *   end structure;
     * </pre>
     */
    public final AnonymousStructure anonComplexStruct;

    /**
     * <code>enum ( red, green, blue );</code>
     */
    public final EnumerateType primaryColours;

    /**
     * <code>type udPrimaryColoursType is enum ( red, green, blue );</code>
     */
    public final UserDefinedType udPrimaryColours;
    public final TypeDeclaration udPrimaryColoursDecl;

    /**
     * <code>enum ( red, orange, yellow, green, blue, indigo, violet );</code>
     */
    public final EnumerateType rainbowColours;

    /**
     * <code>type udRainbowColoursType is enum ( red, orange, yellow, green, blue, indigo, violet );</code>
     */
    public final UserDefinedType udRainbowColours;
    public final TypeDeclaration udRainbowColoursDecl;

    public final ObjectDeclaration object1;
    public final ObjectDeclaration object2;

    public final InstanceType instance1;
    public final InstanceType instance2;

    public final AnyInstanceType anyInstance;

}
