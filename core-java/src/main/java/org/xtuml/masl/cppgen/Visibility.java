//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
package org.xtuml.masl.cppgen;


public enum Visibility
{

  PRIVATE("private"), PROTECTED("protected"), PUBLIC("public");

  private String text;

  private Visibility ( final String text )
  {
    this.text = text;
  }

  @Override
  public String toString ()
  {
    return text;
  }
}
