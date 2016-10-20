//
// File: SingleArgument.java
//
// UK Crown Copyright (c) 2015. All Rights Reserved.
//
package org.xtuml.masl.translate.cmake.language.arguments;

public class SingleArgument
    implements SimpleArgument
{

  public SingleArgument ( final String value )
  {
    this.value = value;
  }

  private final String value;


  public String getValue ()
  {
    return value;
  }

  @Override
  public String getText ()
  {
    return getValue();
  }

}