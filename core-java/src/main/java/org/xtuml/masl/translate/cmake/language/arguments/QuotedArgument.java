//
// File: QuotedArgument.java
//
// UK Crown Copyright (c) 2015. All Rights Reserved.
//
package org.xtuml.masl.translate.cmake.language.arguments;

import com.google.common.escape.CharEscaperBuilder;
import com.google.common.escape.Escaper;

public class QuotedArgument extends SingleArgument
{

  private static Escaper escaper = new CharEscaperBuilder()
                                                           .addEscape('\\', "\\\\")
                                                           .addEscape('\"', "\\\"")
                                                           .addEscape('\n', "\\n")
                                                           .addEscape('\t', "\\t")
                                                           .addEscape(';', "\\;")
                                                           .toEscaper();

  public QuotedArgument ( final String value )
  {
    super(value);
  }

  @Override
  public String getText ()
  {
    return "\"" + escaper.escape(super.getText()) + "\"";
  }
}