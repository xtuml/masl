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
