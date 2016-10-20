//
// File: ParameterDefinition.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.common;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.type.BasicType;


public interface ParameterDefinition
    extends ASTNode
{

  enum Mode
  {
    IN, OUT
  }

  Mode getMode ();

  String getName ();

  BasicType getType ();

}
