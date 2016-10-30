//
// File: VariableReference.java
//
// UK Crown Copyright (c) 2015. All Rights Reserved.
//
package org.xtuml.masl.translate.cmake.language;

import org.xtuml.masl.translate.cmake.Variable;
import org.xtuml.masl.translate.cmake.language.arguments.SimpleArgument;

public class VariableReference
    implements SimpleArgument
{

  private final Variable variable;

  public VariableReference ( final Variable variable )
  {
    this.variable = variable;
  }

  @Override
  public String getText ()
  {
    return "${" + variable.getName() + "}";
  }

}