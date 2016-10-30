//
// File: SplitExpression.java
//
// UK Crown Copyright (c) 2009. All Rights Reserved.
//
package org.xtuml.masl.metamodel.expression;


public interface TimeFieldExpression
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

    DurationDayOfWeek,
    DurationHourOfDay,
    DurationMinuteOfHour,
    DurationSecondOfMinute,
    DurationMilliOfSecond,
    DurationMicroOfSecond,
    DurationMicroOfMilli,
    DurationNanoOfSecond,
    DurationNanoOfMilli,
    DurationNanoOfMicro,

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

  public Expression getLhs ();

}
