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
import org.xtuml.masl.error.ErrorCode;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.type.BasicType;
import org.xtuml.masl.unittest.ErrorLog;

import junit.framework.TestCase;

public class TestAdditiveExpression extends TestCase {

    private static final TestExpressions exprA = TestExpressions.expr1;
    private static final TestExpressions exprB = TestExpressions.expr2;
    private static final TestTypes typesA = TestTypes.data1;

    public void checkResultType(final Expression lhs, final Expression rhs, final BasicType expected,
            final boolean expectedAnon) {
        ErrorLog.getInstance().reset();
        final Expression expression = BinaryExpression.create(lhs,
                new BinaryExpression.OperatorRef(null, BinaryExpression.ImplOperator.PLUS), rhs);
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

    public void checkError(final Expression lhs, final Expression rhs, final ErrorCode... errors) {
        ErrorLog.getInstance().reset();
        assertNull(BinaryExpression.create(lhs,
                new BinaryExpression.OperatorRef(null, BinaryExpression.ImplOperator.PLUS), rhs));

        ErrorLog.getInstance().checkErrors(errors);
    }

    public void test_IntA_IntA() {
        checkResultType(exprA.namedUdIntType, exprA.namedUdIntType, typesA.udIntType, false);
    }

    public void test_IntA_IntB() {
        checkError(exprA.namedUdIntType, exprB.namedUdIntType, SemanticErrorCode.OperatorOperandsNotCompatible);
    }

    public void test_IntA_AnonInt() {
        checkResultType(exprA.namedUdIntType, exprA.anonBInteger, typesA.udIntType, false);
    }

    public void test_AnonInt_IntA() {
        checkResultType(exprA.anonBInteger, exprA.namedUdIntType, typesA.udIntType, false);
    }

    public void test_AnonInt_AnonInt() {
        checkResultType(exprA.anonBInteger, exprA.anonBInteger, typesA.bInteger, true);
    }

    public void test_RealA_RealA() {
        checkResultType(exprA.namedUdRealType, exprA.namedUdRealType, typesA.udRealType, false);
    }

    public void test_RealA_RealB() {
        checkError(exprA.namedUdRealType, exprB.namedUdRealType, SemanticErrorCode.OperatorOperandsNotCompatible);
    }

    public void test_RealA_AnonReal() {
        checkResultType(exprA.namedUdRealType, exprA.anonBReal, typesA.udRealType, false);
    }

    public void test_AnonReal_RealA() {
        checkResultType(exprA.anonBReal, exprA.namedUdRealType, typesA.udRealType, false);
    }

    public void test_AnonReal_AnonReal() {
        checkResultType(exprA.anonBReal, exprA.anonBReal, typesA.bReal, true);
    }

    public void test_IntA_RealA() {
        checkError(exprA.namedUdIntType, exprA.namedUdRealType, SemanticErrorCode.OperatorOperandsNotCompatible);
    }

    public void test_IntA_AnonReal() {
        checkError(exprA.namedUdIntType, exprA.anonBReal, SemanticErrorCode.OperatorOperandsNotCompatible);
    }

    public void test_AnonInt_RealA() {
        checkResultType(exprA.anonBInteger, exprA.namedUdRealType, typesA.udRealType, false);
    }

    public void test_AnonInt_AnonReal() {
        checkResultType(exprA.anonBInteger, exprA.anonBReal, typesA.bReal, true);
    }

    public void test_RealA_IntA() {
        checkError(exprA.namedUdRealType, exprB.namedUdIntType, SemanticErrorCode.OperatorOperandsNotCompatible);
    }

    public void test_RealA_AnonInt() {
        checkResultType(exprA.namedUdRealType, exprA.anonBInteger, typesA.udRealType, false);
    }

    public void test_AnonReal_IntA() {
        checkError(exprA.anonBReal, exprA.namedUdIntType, SemanticErrorCode.OperatorOperandsNotCompatible);
    }

    public void test_AnonReal_AnonInt() {
        checkResultType(exprA.anonBReal, exprA.anonBInteger, typesA.bReal, true);
    }

}
