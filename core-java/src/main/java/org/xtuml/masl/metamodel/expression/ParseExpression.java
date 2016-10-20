//
// File: SplitExpression.java
//
// UK Crown Copyright (c) 2009. All Rights Reserved.
//
package org.xtuml.masl.metamodel.expression;


public interface ParseExpression
    extends Expression
{

  public Expression getArgument ();

  public Expression getBase ();
}
