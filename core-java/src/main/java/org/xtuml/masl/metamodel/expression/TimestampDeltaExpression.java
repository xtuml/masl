//
// File: SplitExpression.java
//
// UK Crown Copyright (c) 2009. All Rights Reserved.
//
package org.xtuml.masl.metamodel.expression;


public interface TimestampDeltaExpression
    extends Expression
{

  public enum Type
  {
    YEARS,
    MONTHS
  }

  public Expression getArgument ();

  public Expression getLhs ();

  public Type getDeltaType ();

}
