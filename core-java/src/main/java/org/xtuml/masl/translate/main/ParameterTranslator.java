//
// File: ParameterTranslator.java
//
// UK Crown Copyright (c) 2007. All Rights Reserved.
//
package org.xtuml.masl.translate.main;

import org.xtuml.masl.cppgen.Function;
import org.xtuml.masl.cppgen.TypeUsage;
import org.xtuml.masl.cppgen.Variable;
import org.xtuml.masl.metamodel.common.ParameterDefinition;


public class ParameterTranslator
{

  /**
   * This constructor is used during the translation of service parameters as
   * part of a domain build.
   * 

   *          The definition of the terminator service parameter.

   *          The cpp function that the service will be translated into.
   */
  public ParameterTranslator ( final ParameterDefinition param, final Function function )
  {
    this.param = param;
    final TypeUsage type = Types.getInstance().getType(param.getType());
    variable = function.createParameter(type.getOptimalParameterType(param.getMode() == ParameterDefinition.Mode.OUT),
                                        Mangler
                                               .mangleName(param));
  }


  private final ParameterDefinition param;
  private final Variable            variable;

  public ParameterDefinition getParam ()
  {
    return param;
  }

  public Variable getVariable ()
  {
    return variable;
  }
}
