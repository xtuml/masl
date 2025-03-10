/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.metamodelImpl.expression;
import org.xtuml.masl.metamodelImpl.type.CharacterType;
import org.xtuml.masl.metamodelImpl.type.IntegerType;
import org.xtuml.masl.metamodelImpl.type.RealType;
import org.xtuml.masl.metamodelImpl.type.StringType;

public class TestExpressions {

    public static final TestExpressions expr1 = new TestExpressions(TestTypes.data1);
    public static final TestExpressions expr2 = new TestExpressions(TestTypes.data2);

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
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#udCharacterType
     * udCharacterType}
     */
    public final TypedExpression namedUdCharacterType;

    /**
     * Named expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#udStringType udStringType}
     */
    public final TypedExpression namedUdStringType;

    /**
     * Named expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#udSeqOfUdStringType
     * udSeqOfUdStringType}
     */
    public final TypedExpression namedUdSeqOfUdStringType;

    /**
     * Anonymous expression of type
     * {@link org.xtuml.masl.metamodelImpl.TestTypes#bCharacter bCharacter}
     */
    public final TypedExpression anonBCharacter;

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

    private TestExpressions(final TestTypes types) {
        namedUdIntType = new TypedExpression(types.udIntType);
        namedUdRealType = new TypedExpression(types.udRealType);
        namedUdCharacterType = new TypedExpression(types.udCharacterType);
        namedUdStringType = new TypedExpression(types.udStringType);
        namedUdSeqOfUdStringType = new TypedExpression(types.udSeqOfUdStringType);
        anonBCharacter = new TypedExpression(CharacterType.createAnonymous());
        anonBInteger = new TypedExpression(IntegerType.createAnonymous());
        anonBReal = new TypedExpression(RealType.createAnonymous());
        anonBString = new TypedExpression(StringType.createAnonymous());

    }

}
