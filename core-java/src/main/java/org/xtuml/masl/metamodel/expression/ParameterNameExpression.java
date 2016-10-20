//
// File: ParameterNameExpression.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.expression;

import org.xtuml.masl.metamodel.common.ParameterDefinition;


public interface ParameterNameExpression
    extends Expression
{

  ParameterDefinition getParameter ();
}
