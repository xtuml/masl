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
import org.xtuml.masl.unittest.ErrorLog;

import junit.framework.TestCase;

public class TestCharacterLiteral extends TestCase {

    public void checkLiteral(final String literal, final char expected) {
        ErrorLog.getInstance().reset();
        assertEquals(expected, CharacterLiteral.create(null, "\'" + literal + "\'").getValue());
        ErrorLog.getInstance().checkErrors();
    }

    public void checkLiteral(final String literal, final ErrorCode... errors) {
        ErrorLog.getInstance().reset();
        assertNull(CharacterLiteral.create(null, "\'" + literal + "\'"));
        ErrorLog.getInstance().checkErrors(errors);
    }

    public void testNL() {
        checkLiteral("\\n", '\n');
    }

    public void testLF() {
        checkLiteral("\\r", '\r');
    }

    public void testBackspace() {
        checkLiteral("\\b", '\b');
    }

    public void testFF() {
        checkLiteral("\\f", '\f');
    }

    public void testEscapedDoubleQuote() {
        checkLiteral("\\\"", '"');
    }

    public void testUnEscapedDoubleQuote() {
        checkLiteral("\\\"", '"');
    }

    public void testSingleQuote() {
        checkLiteral("\\\"", '"');
    }

    public void testBackslash() {
        checkLiteral("\\\\", '\\');
    }

    public void testSpace() {
        checkLiteral(" ", ' ');
    }

    public void testNormal() {
        checkLiteral("A", 'A');
    }

    public void testOctal() {
        checkLiteral("\000", '\000');
    }

    public void testUnicode() {
        checkLiteral("\u1234", '\u1234');
    }

    public void testInvalidBackslash() {
        checkLiteral("\\", SemanticErrorCode.InvalidEscapeSequence);
    }

    public void testInvalidEscaped() {
        checkLiteral("\\4", SemanticErrorCode.InvalidEscapeSequence);
    }

    public void testTooLongOctal() {
        checkLiteral("\\1234", SemanticErrorCode.InvalidEscapeSequence);
    }

    public void testInvalidOctal() {
        checkLiteral("\\128", SemanticErrorCode.InvalidEscapeSequence);
    }

    public void testTooShortOctal() {
        checkLiteral("\\0", SemanticErrorCode.InvalidEscapeSequence);
    }

    public void testTooLongUnicode() {
        checkLiteral("\\u12341", SemanticErrorCode.InvalidEscapeSequence);
    }

    public void testInvalidUnicode() {
        checkLiteral("\\u123h", SemanticErrorCode.InvalidEscapeSequence);
    }

    public void testTooShortUnicode() {
        checkLiteral("\\u123", SemanticErrorCode.InvalidEscapeSequence);
    }

    public void testTooLong() {
        checkLiteral("AA", SemanticErrorCode.CharacterLiteralInvalidLength);
    }

    public void testTooShort() {
        checkLiteral("", SemanticErrorCode.CharacterLiteralInvalidLength);
    }
}
