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
package org.xtuml.masl.metamodelImpl.expression;
import org.xtuml.masl.metamodelImpl.type.BasicType;
import org.xtuml.masl.unittest.ErrorLog;

import junit.framework.TestCase;

public class TestMultiplicativeExpression extends TestCase {

    private static final TestExpressions exprA = TestExpressions.expr1;
    private static final TestExpressions exprB = TestExpressions.expr2;
    private static final TestTypes typesA = TestTypes.data1;

    public void checkResult(final Expression lhs, final Expression rhs, final BasicType expected,
            final boolean expectedAnon) {
        ErrorLog.getInstance().reset();
        final Expression expression = BinaryExpression.create(lhs,
                new BinaryExpression.OperatorRef(null, BinaryExpression.ImplOperator.TIMES), rhs);
        assertNotNull(expression);
        final BasicType result = expression.getType();
        assertEquals(expected, result);

        if (expectedAnon) {
            assertTrue("Expected Anonymous", result.isAnonymousType());
        } else {
            assertFalse("Expected Named", result.isAnonymousType());
        }

        ErrorLog.getInstance().checkErrors();
    }

    public void test_IntA_IntA() {
        checkResult(exprA.namedUdIntType, exprA.namedUdIntType, typesA.udIntType, false);
    }

    public void test_IntA_IntB() {
        checkResult(exprA.namedUdIntType, exprB.namedUdIntType, typesA.bInteger, true);
    }

    public void test_IntA_AnonInt() {
        checkResult(exprA.namedUdIntType, exprA.anonBInteger, typesA.udIntType, false);
    }

    public void test_AnonInt_IntA() {
        checkResult(exprA.anonBInteger, exprA.namedUdIntType, typesA.udIntType, false);
    }

    public void test_AnonInt_AnonInt() {
        checkResult(exprA.anonBInteger, exprA.anonBInteger, typesA.bInteger, true);
    }

    public void test_RealA_RealA() {
        checkResult(exprA.namedUdRealType, exprA.namedUdRealType, typesA.udRealType, false);
    }

    public void test_RealA_RealB() {
        checkResult(exprA.namedUdRealType, exprB.namedUdRealType, typesA.bReal, true);
    }

    public void test_RealA_AnonReal() {
        checkResult(exprA.namedUdRealType, exprA.anonBReal, typesA.udRealType, false);
    }

    public void test_AnonReal_RealA() {
        checkResult(exprA.anonBReal, exprA.namedUdRealType, typesA.udRealType, false);
    }

    public void test_AnonReal_AnonReal() {
        checkResult(exprA.anonBReal, exprA.anonBReal, typesA.bReal, true);
    }

    public void test_IntA_RealA() {
        checkResult(exprA.namedUdIntType, exprA.namedUdRealType, typesA.bReal, true);
    }

    public void test_IntA_AnonReal() {
        checkResult(exprA.namedUdIntType, exprA.anonBReal, typesA.bReal, true);
    }

    public void test_AnonInt_RealA() {
        checkResult(exprA.anonBInteger, exprA.namedUdRealType, typesA.udRealType, false);
    }

    public void test_AnonInt_AnonReal() {
        checkResult(exprA.anonBInteger, exprA.anonBReal, typesA.bReal, true);
    }

    public void test_RealA_IntA() {
        checkResult(exprA.namedUdRealType, exprB.namedUdIntType, typesA.bReal, true);
    }

    public void test_RealA_AnonInt() {
        checkResult(exprA.namedUdRealType, exprA.anonBInteger, typesA.udRealType, false);
    }

    public void test_AnonReal_IntA() {
        checkResult(exprA.anonBReal, exprA.namedUdIntType, typesA.bReal, true);
    }

    public void test_AnonReal_AnonInt() {
        checkResult(exprA.anonBReal, exprA.anonBInteger, typesA.bReal, true);
    }

}
