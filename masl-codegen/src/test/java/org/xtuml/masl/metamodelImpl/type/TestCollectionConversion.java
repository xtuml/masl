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

public abstract class TestCollectionConversion extends TestTypeConversion {

    public static class TestSequence extends TestCollectionConversion {

        public TestSequence() {
            super(data1.seqOfInt, data1.seqOfSeqOfInt, data1.seqOfSetOfInt, data1.seqOfBagOfInt, data1.seqOfArrayOfInt,
                    data1.seqOfUdIntType, data1.seqOfUdIntTypeType);

        }
    }

    public static class TestArray1to10 extends TestCollectionConversion {

        public TestArray1to10() {
            super(data1.array1to10OfInt, data1.arrayOfSeqOfInt, data1.arrayOfSetOfInt, data1.arrayOfBagOfInt,
                    data1.arrayOfArrayOfInt, data1.arrayOfUdIntType, data1.arrayOfUdIntTypeType);

        }
    }

    public static class TestSet extends TestCollectionConversion {

        public TestSet() {
            super(data1.setOfInt, data1.setOfSeqOfInt, data1.setOfSetOfInt, data1.setOfBagOfInt, data1.setOfArrayOfInt,
                    data1.setOfUdIntType, data1.setOfUdIntTypeType);

        }
    }

    public static class TestBag extends TestCollectionConversion {

        public TestBag() {
            super(data1.bagOfInt, data1.bagOfSeqOfInt, data1.bagOfSetOfInt, data1.bagOfBagOfInt, data1.bagOfArrayOfInt,
                    data1.bagOfUdIntType, data1.bagOfUdIntTypeType);

        }
    }

    private final CollectionType collOfInt;
    private final CollectionType collOfSeqOfInt;
    private final CollectionType collOfSetOfInt;
    private final CollectionType collOfBagOfInt;
    private final CollectionType collOfArrayOfInt;
    private final CollectionType collOfUdIntType;
    private final CollectionType collOfUdIntTypeType;

    protected TestCollectionConversion(final CollectionType collOfInt, final CollectionType collOfSeqOfInt,
            final CollectionType collOfSetOfInt, final CollectionType collOfBagOfInt,
            final CollectionType collOfArrayOfInt, final CollectionType collOfUdIntType,
            final CollectionType collOfUdIntTypeType) {
        this.collOfInt = collOfInt;
        this.collOfSeqOfInt = collOfSeqOfInt;
        this.collOfSetOfInt = collOfSetOfInt;
        this.collOfBagOfInt = collOfBagOfInt;
        this.collOfArrayOfInt = collOfArrayOfInt;
        this.collOfUdIntType = collOfUdIntType;
        this.collOfUdIntTypeType = collOfUdIntTypeType;
    }

    public void testCollOfIntPrimitive() {
        checkPrimitive(collOfInt, data1.seqOfInt);
    }

    public void testCollOfIntBasic() {
        checkBasicType(collOfInt, collOfInt);
    }

    public void testCollOfSeqOfIntPrimitive() {
        checkPrimitive(collOfSeqOfInt, data1.seqOfSeqOfInt);
    }

    public void testCollOfSeqOfIntBasic() {
        checkBasicType(collOfSeqOfInt, collOfSeqOfInt);
    }

    public void testCollOfSetOfIntPrimitive() {
        checkPrimitive(collOfSetOfInt, data1.seqOfSetOfInt);
    }

    public void testCollOfSetOfIntBasic() {
        checkBasicType(collOfSetOfInt, collOfSetOfInt);
    }

    public void testCollOfBagOfIntPrimitive() {
        checkPrimitive(collOfBagOfInt, data1.seqOfBagOfInt);
    }

    public void testCollOfBagOfIntBasic() {
        checkBasicType(collOfBagOfInt, collOfBagOfInt);
    }

    public void testCollOfArrayOfIntPrimitive() {
        checkPrimitive(collOfArrayOfInt, data1.seqOfArrayOfInt);
    }

    public void testCollOfArrayOfIntBasic() {
        checkBasicType(collOfArrayOfInt, collOfArrayOfInt);
    }

    public void testCollOfUdIntTypePrimitive() {
        checkPrimitive(collOfUdIntType, data1.seqOfUdIntType);
    }

    public void testCollOfUdIntTypeBasic() {
        checkBasicType(collOfUdIntType, collOfInt);
    }

    public void testCollOfUdIntTypeTypePrimitive() {
        checkPrimitive(collOfUdIntTypeType, data1.seqOfUdIntTypeType);
    }

    public void testCollOfUdIntTypeTypeBasic() {
        checkBasicType(collOfUdIntTypeType, collOfInt);
    }

}
