//
// File: SplitExpression.java
//
// UK Crown Copyright (c) 2009. All Rights Reserved.
//
package org.xtuml.masl.metamodel.expression;


public interface CreateDurationExpression
    extends Expression
{

  public enum Field
  {
    Weeks,
    Days,
    Hours,
    Minutes,
    Seconds,
    Millis,
    Micros,
    Nanos
  }

  public Field getField ();

  public Expression getArgument ();

}
