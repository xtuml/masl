//
// File: ParameterNameExpression.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.expression;

import org.xtuml.masl.metamodel.code.VariableDefinition;


public interface VariableNameExpression
    extends Expression
{

  VariableDefinition getVariable ();
}
