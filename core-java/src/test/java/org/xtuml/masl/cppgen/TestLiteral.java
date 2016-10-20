//
// File: TestLiteral.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.cppgen;

import junit.framework.TestCase;


public class TestLiteral extends TestCase
{

  public void checkChar ( final String expected, final char ch )
  {
    assertEquals(expected, Literal.createCharLiteral(ch).getCode(null, null));
  }

  public void checkString ( final String expected, final String str )
  {
    assertEquals(expected, Literal.createStringLiteral(str).getCode(null, null));
  }

  public void testNull ()
  {
    checkChar("'\\000'", '\0');
  }

  public void testControl ()
  {
    checkChar("'\\003'", '\3');
  }

  public void testBell ()
  {
    checkChar("'\\a'", '\007');
  }

  public void testBackspace ()
  {
    checkChar("'\\b'", '\b');
  }

  public void testTab ()
  {
    checkChar("'\\t'", '\t');
  }

  public void testLF ()
  {
    checkChar("'\\n'", '\n');
  }

  public void testVerticalTab ()
  {
    checkChar("'\\v'", '\013');
  }

  public void testFF ()
  {
    checkChar("'\\f'", '\f');
  }

  public void testCR ()
  {
    checkChar("'\\r'", '\r');
  }

  public void testSpace ()
  {
    checkChar("' '", ' ');
  }

  public void testAlpha ()
  {
    checkChar("'Q'", 'Q');
  }

  public void testTilde ()
  {
    checkChar("'~'", '\176');
  }

  public void testDel ()
  {
    checkChar("'\\177'", '\177');
  }

  public void testHighControl ()
  {
    checkChar("'\\202'", '\u0082');
  }

  public void testExtendedLatin ()
  {
    checkChar("'À'", 'À');
  }

  public void testMaxNormal ()
  {
    checkChar("'ÿ'", 'ÿ');
  }

  public void testMinUnicode ()
  {
    checkChar("L'\\u0100'", '\u0100');
  }

  public void testMidUnicode ()
  {
    checkChar("L'\\u1234'", '\u1234');
  }

  public void testMaxUnicode ()
  {
    checkChar("L'\\uffff'", '\uffff');
  }

  public void testNarrowString ()
  {
    checkString("\"\\000ABC123abc\\177\\n\\r\\vÀÿ\"", "\000ABC123abc\177\n\r\013Àÿ");
  }

  public void testWideString ()
  {
    checkString("L\"\\000ABC123abc\\177\\n\\r\\vÀÿ\\u1234\"", "\000ABC123abc\177\n\r\013Àÿ\u1234");

  }

}
