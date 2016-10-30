//
// File: TestMultiplicativeExpression.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.expression;

import junit.framework.TestCase;

import org.xtuml.masl.error.ErrorCode;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.expression.BinaryExpression;
import org.xtuml.masl.metamodelImpl.expression.Expression;
import org.xtuml.masl.metamodelImpl.type.BasicType;

import org.xtuml.masl.unittest.ErrorLog;


public class TestConcatenateExpression extends TestCase
{

  private static final TestExpressions exprA  = TestExpressions.expr1;
  private static final TestExpressions exprB  = TestExpressions.expr2;
  private static final TestTypes       typesA = TestTypes.data1;

  public void checkResult ( final Expression lhs, final Expression rhs, final BasicType expected, final boolean expectedAnon )
  {
    ErrorLog.getInstance().reset();
    final Expression expression = BinaryExpression.create(lhs,
                                                          new BinaryExpression.OperatorRef(null,
                                                                                           BinaryExpression.ImplOperator.CONCATENATE),
                                                          rhs);
    assertNotNull(expression);
    final BasicType result = expression.getType();
    assertEquals(expected, result);

    if ( expectedAnon )
    {
      assertTrue("Expected Anonymous", result.isAnonymousType());
    }
    else
    {
      assertFalse("Expected Named", result.isAnonymousType());
    }

    ErrorLog.getInstance().checkErrors();
  }

  public void checkResult ( final Expression lhs, final Expression rhs, final ErrorCode... errors )
  {
    ErrorLog.getInstance().reset();
    assertNull(BinaryExpression.create(lhs, new BinaryExpression.OperatorRef(null, BinaryExpression.ImplOperator.CONCATENATE), rhs));

    ErrorLog.getInstance().checkErrors(errors);
  }

  public void test_StringA_StringA ()
  {
    checkResult(exprA.namedUdStringType, exprA.namedUdStringType, typesA.udStringType, false);
  }

  public void test_StringA_StringB ()
  {
    checkResult(exprA.namedUdStringType, exprB.namedUdStringType, SemanticErrorCode.OperatorOperandsNotCompatible);
  }

  public void test_StringA_AnonString ()
  {
    checkResult(exprA.namedUdStringType, exprA.anonBString, typesA.udStringType, false);
  }

  public void test_AnonString_StringA ()
  {
    checkResult(exprA.anonBString, exprA.namedUdStringType, typesA.udStringType, false);
  }

  public void test_AnonString_AnonString ()
  {
    checkResult(exprA.anonBString, exprA.anonBString, typesA.bString, true);
  }


  public void test_CharA_CharA ()
  {
    checkResult(exprA.namedUdCharacterType, exprA.namedUdCharacterType, typesA.seqOfUdCharacterType, true);
  }

  public void test_CharA_CharB ()
  {
    checkResult(exprA.namedUdCharacterType, exprB.namedUdCharacterType, SemanticErrorCode.OperatorOperandsNotCompatible);
  }

  public void test_CharA_AnonChar ()
  {
    checkResult(exprA.namedUdCharacterType, exprA.anonBCharacter, typesA.seqOfUdCharacterType, true);
  }

  public void test_AnonChar_CharA ()
  {
    checkResult(exprA.anonBCharacter, exprA.namedUdCharacterType, typesA.seqOfUdCharacterType, true);
  }

  public void test_AnonChar_AnonChar ()
  {
    checkResult(exprA.anonBCharacter, exprA.anonBCharacter, typesA.seqOfCharacter, true);
  }


  public void test_StringA_CharA ()
  {
    checkResult(exprA.namedUdStringType, exprA.namedUdCharacterType, SemanticErrorCode.OperatorOperandsNotCompatible);
  }

  public void test_StringA_AnonChar ()
  {
    checkResult(exprA.namedUdStringType, exprA.anonBCharacter, typesA.udStringType, false);
  }

  public void test_AnonString_CharA ()
  {
    checkResult(exprA.anonBString, exprA.namedUdCharacterType, typesA.seqOfUdCharacterType, true);
  }

  public void test_AnonString_AnonChar ()
  {
    checkResult(exprA.anonBString, exprA.anonBCharacter, typesA.bString, true);
  }


  public void test_CharA_StringA ()
  {
    checkResult(exprA.namedUdCharacterType, exprB.namedUdStringType, SemanticErrorCode.OperatorOperandsNotCompatible);
  }

  public void test_CharA_AnonString ()
  {
    checkResult(exprA.namedUdCharacterType, exprA.anonBString, typesA.seqOfUdCharacterType, true);
  }

  public void test_AnonChar_StringA ()
  {
    checkResult(exprA.anonBCharacter, exprA.namedUdStringType, typesA.udStringType, false);
  }

  public void test_AnonChar_AnonString ()
  {
    checkResult(exprA.anonBCharacter, exprA.anonBString, typesA.bString, true);
  }

  public void test_UdSeqOfUdString_UdString ()
  {
    checkResult(exprA.namedUdSeqOfUdStringType, exprA.namedUdStringType, typesA.udSeqOfUdStringType, false);
  }

  public void test_UdSeqOfUdString_AnonChar ()
  {
    checkResult(exprA.namedUdSeqOfUdStringType, exprA.anonBCharacter, typesA.udSeqOfUdStringType, false);
  }

  public void test_UdUdString_UdSeqOfString ()
  {
    checkResult(exprA.namedUdSeqOfUdStringType, exprA.namedUdStringType, typesA.udSeqOfUdStringType, false);
  }

  public void test_AnonChar_UdSeqOfUdString ()
  {
    checkResult(exprA.anonBCharacter, exprA.namedUdSeqOfUdStringType, typesA.udSeqOfUdStringType, false);
  }

}
