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

import junit.framework.TestCase;
import org.xtuml.masl.error.ErrorCode;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.unittest.ErrorLog;

public class TestCharacterLiteral extends TestCase {

    public void checkLiteral(final String literal, final char expected) {
        ErrorLog.getInstance().reset();
        assertEquals(expected, CharacterLiteral.create(null, "'" + literal + "'").getValue());
        ErrorLog.getInstance().checkErrors();
    }

    public void checkLiteral(final String literal, final ErrorCode... errors) {
        ErrorLog.getInstance().reset();
        assertNull(CharacterLiteral.create(null, "'" + literal + "'"));
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
