/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.cppgen;
import junit.framework.TestCase;

public class TestLiteral extends TestCase {

    public void checkChar(final String expected, final char ch) {
        assertEquals(expected, Literal.createCharLiteral(ch).getCode(null, null));
    }

    public void checkString(final String expected, final String str) {
        assertEquals(expected, Literal.createStringLiteral(str).getCode(null, null));
    }

    public void testNull() {
        checkChar("'\\000'", '\0');
    }

    public void testControl() {
        checkChar("'\\003'", '\3');
    }

    public void testBell() {
        checkChar("'\\a'", '\007');
    }

    public void testBackspace() {
        checkChar("'\\b'", '\b');
    }

    public void testTab() {
        checkChar("'\\t'", '\t');
    }

    public void testLF() {
        checkChar("'\\n'", '\n');
    }

    public void testVerticalTab() {
        checkChar("'\\v'", '\013');
    }

    public void testFF() {
        checkChar("'\\f'", '\f');
    }

    public void testCR() {
        checkChar("'\\r'", '\r');
    }

    public void testSpace() {
        checkChar("' '", ' ');
    }

    public void testAlpha() {
        checkChar("'Q'", 'Q');
    }

    public void testTilde() {
        checkChar("'~'", '\176');
    }

    public void testDel() {
        checkChar("'\\177'", '\177');
    }

    public void testHighControl() {
        checkChar("'\\202'", '\u0082');
    }

    public void testExtendedLatin() {
        checkChar("'\u00C0'", '\u00C0');
    }

    public void testMaxNormal() {
        checkChar("'\u00FF'", '\u00FF');
    }

    public void testMinUnicode() {
        checkChar("L'\\u0100'", '\u0100');
    }

    public void testMidUnicode() {
        checkChar("L'\\u1234'", '\u1234');
    }

    public void testMaxUnicode() {
        checkChar("L'\\uffff'", '\uffff');
    }

    public void testNarrowString() {
        checkString("\"\\000ABC123abc\\177\\n\\r\\v\u00C0\u00FF\"", "\000ABC123abc\177\n\r\013\u00C0\u00FF");
    }

    public void testWideString() {
        checkString("L\"\\000ABC123abc\\177\\n\\r\\v\u00C0\u00FF\\u1234\"",
                "\000ABC123abc\177\n\r\013\u00C0\u00FF\u1234");

    }

}
