//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.common;

public enum ParameterModeType
{
  IN("in", ParameterDefinition.Mode.IN),
  OUT("out", ParameterDefinition.Mode.OUT);

  private final String                   text;
  private final ParameterDefinition.Mode mode;

  private ParameterModeType ( final String text, final ParameterDefinition.Mode mode )
  {
    this.text = text;
    this.mode = mode;
  }

  public ParameterDefinition.Mode getMode ()
  {
    return mode;
  }

  @Override
  public String toString ()
  {
    return text;
  }
}
