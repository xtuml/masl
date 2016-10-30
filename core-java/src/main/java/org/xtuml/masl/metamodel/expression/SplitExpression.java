//
// File: SplitExpression.java
//
// UK Crown Copyright (c) 2009. All Rights Reserved.
//
package org.xtuml.masl.metamodel.expression;

import java.util.List;
import java.util.Set;


public interface SplitExpression
    extends Expression
{

  public enum Field
  {
    CalendarYear,
    MonthOfYear,
    DayOfMonth,
    DayOfYear,
    WeekYear,
    WeekOfYear,
    DayOfWeek,
    HourOfDay,
    MinuteOfHour,
    SecondOfMinute,
    MilliOfSecond,
    MicroOfSecond,
    MicroOfMilli,
    NanoOfSecond,
    NanoOfMilli,
    NanoOfMicro,

    Weeks,
    Days,
    Hours,
    Minutes,
    Seconds,
    Millis,
    Micros,
    Nanos
  }

  public enum Type
  {
    SPLIT,
    COMBINE
  }


  public List<? extends Expression> getArguments ();

  public Set<Field> getFields ();

  public Expression getLhs ();

  public Type getSplitType ();

}
