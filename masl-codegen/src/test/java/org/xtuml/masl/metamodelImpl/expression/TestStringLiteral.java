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

public class TestStringLiteral extends TestCase {

    public void checkLiteral(final String literal, final String expected) {
        ErrorLog.getInstance().reset();
        assertEquals(expected, StringLiteral.create(null, "\"" + literal + "\"").getValue());
        ErrorLog.getInstance().checkErrors();
    }

    public void checkLiteral(final String literal, final ErrorCode... errors) {
        ErrorLog.getInstance().reset();
        assertNull(StringLiteral.create(null, "\"" + literal + "\""));
        ErrorLog.getInstance().checkErrors(errors);
    }

    public void testGood() {
        checkLiteral("\\n\\r\\b\\f\\\"'\\'\\\\ A\\123\\12\\1\u1234", "\n\r\b\f\"''\\ A\123\012\001\u1234");
    }

    public void testInvalidBackslash() {
        checkLiteral("\\", SemanticErrorCode.InvalidEscapeSequence);
    }

    public void testInvalidEscaped() {
        checkLiteral("\\4", SemanticErrorCode.InvalidEscapeSequence);
    }

    public void testInvalidUnicode() {
        checkLiteral("\\u123h", SemanticErrorCode.InvalidEscapeSequence);
    }

    public void testTooShortUnicode() {
        checkLiteral("\\u123", SemanticErrorCode.InvalidEscapeSequence);
    }

    public void testInvalidBackslashSub() {
        checkLiteral("A\\A", SemanticErrorCode.InvalidEscapeSequence);
    }

    public void testInvalidEscapedSub() {
        checkLiteral("A\\4A", SemanticErrorCode.InvalidEscapeSequence);
    }

    public void testInvalidUnicodeSub() {
        checkLiteral("A\\u123hA", SemanticErrorCode.InvalidEscapeSequence);
    }

    public void testTooShortUnicodeSub() {
        checkLiteral("A\\u123Q", SemanticErrorCode.InvalidEscapeSequence);
    }

}
