//
// File: TimerFieldExpression.java
//
// UK Crown Copyright (c) 2010. All Rights Reserved.
//
package org.xtuml.masl.metamodel.expression;

public interface TimerFieldExpression
{

  enum Field
  {
    scheduled, expired, scheduled_at, expired_at, delta, missed
  }

  public Field getField ();

  public Expression getLhs ();

}
