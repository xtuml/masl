//
// File: NameExpressionTranslator.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.translate.main.expression;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.xtuml.masl.cppgen.Expression;
import org.xtuml.masl.metamodel.expression.SplitExpression;
import org.xtuml.masl.metamodel.type.TypeDefinition;
import org.xtuml.masl.translate.main.Architecture;
import org.xtuml.masl.translate.main.Scope;



public class SplitTranslator extends ExpressionTranslator
{

  private final Expression       lhs;
  private final List<Expression> arguments = new ArrayList<Expression>();

  SplitTranslator ( final SplitExpression splitExpression, final Scope scope )
  {
    for ( final org.xtuml.masl.metamodel.expression.Expression arg : splitExpression.getArguments() )
    {
      arguments.add(ExpressionTranslator.createTranslator(arg, scope).getReadExpression());
    }

    switch ( splitExpression.getSplitType() )
    {
      case COMBINE:
        lhs = null;
        translateCombine(splitExpression);
        break;
      case SPLIT:
        lhs = ExpressionTranslator.createTranslator(splitExpression.getLhs(), scope).getReadExpression();
        translateSplit(splitExpression);
        break;
      default:
        lhs = null;
        assert false;
    }
  }

  void translateCombine ( final SplitExpression splitExpression )
  {
    if ( splitExpression.getType().getBasicType().getActualType() == TypeDefinition.ActualType.DURATION )
    {
      final List<Expression> fields = new ArrayList<Expression>();
      for ( final SplitExpression.Field field : splitExpression.getFields() )
      {
        fields.add(durationFieldLookup.get(field));
      }

      setReadExpression(Architecture.Duration.getCombine(fields, arguments));
    }
    else
    {
      final List<Expression> fields = new ArrayList<Expression>();
      for ( final SplitExpression.Field field : splitExpression.getFields() )
      {
        fields.add(timestampFieldLookup.get(field));
      }

      setReadExpression(Architecture.Timestamp.getCombine(fields, arguments));
    }
  }


  void translateSplit ( final SplitExpression splitExpression )
  {
    if ( splitExpression.getLhs().getType().getBasicType().getActualType() == TypeDefinition.ActualType.DURATION )
    {
      final List<Expression> fields = new ArrayList<Expression>();
      for ( final SplitExpression.Field field : splitExpression.getFields() )
      {
        fields.add(durationFieldLookup.get(field));
      }

      setReadExpression(Architecture.Duration.getSplit(lhs, fields));
    }
    else
    {
      final List<Expression> fields = new ArrayList<Expression>();
      for ( final SplitExpression.Field field : splitExpression.getFields() )
      {
        fields.add(timestampFieldLookup.get(field));
      }

      setReadExpression(Architecture.Timestamp.getSplit(lhs, fields));
    }

  }


  private static Map<SplitExpression.Field, Expression> durationFieldLookup  = new EnumMap<SplitExpression.Field, Expression>(SplitExpression.Field.class);
  private static Map<SplitExpression.Field, Expression> timestampFieldLookup = new EnumMap<SplitExpression.Field, Expression>(SplitExpression.Field.class);

  static
  {
    timestampFieldLookup.put(SplitExpression.Field.CalendarYear, Architecture.Timestamp.splitCalendarYear);
    timestampFieldLookup.put(SplitExpression.Field.MonthOfYear, Architecture.Timestamp.splitMonthOfYear);
    timestampFieldLookup.put(SplitExpression.Field.DayOfMonth, Architecture.Timestamp.splitDayOfMonth);
    timestampFieldLookup.put(SplitExpression.Field.WeekYear, Architecture.Timestamp.splitWeekYear);
    timestampFieldLookup.put(SplitExpression.Field.WeekOfYear, Architecture.Timestamp.splitWeekOfYear);
    timestampFieldLookup.put(SplitExpression.Field.DayOfWeek, Architecture.Timestamp.splitDayOfWeek);
    timestampFieldLookup.put(SplitExpression.Field.DayOfYear, Architecture.Timestamp.splitDayOfYear);
    timestampFieldLookup.put(SplitExpression.Field.HourOfDay, Architecture.Timestamp.splitHourOfDay);
    timestampFieldLookup.put(SplitExpression.Field.MinuteOfHour, Architecture.Timestamp.splitMinOfHour);
    timestampFieldLookup.put(SplitExpression.Field.SecondOfMinute, Architecture.Timestamp.splitSecOfMin);
    timestampFieldLookup.put(SplitExpression.Field.MilliOfSecond, Architecture.Timestamp.splitMilliOfSec);
    timestampFieldLookup.put(SplitExpression.Field.MicroOfSecond, Architecture.Timestamp.splitMicroOfSec);
    timestampFieldLookup.put(SplitExpression.Field.NanoOfSecond, Architecture.Timestamp.splitNanoOfSec);
    timestampFieldLookup.put(SplitExpression.Field.MicroOfMilli, Architecture.Timestamp.splitMicroOfMilli);
    timestampFieldLookup.put(SplitExpression.Field.NanoOfMilli, Architecture.Timestamp.splitNanoOfMilli);
    timestampFieldLookup.put(SplitExpression.Field.NanoOfMicro, Architecture.Timestamp.splitNanoOfMicro);

    durationFieldLookup.put(SplitExpression.Field.Weeks, Architecture.Duration.splitWeeks);
    durationFieldLookup.put(SplitExpression.Field.Days, Architecture.Duration.splitDays);
    durationFieldLookup.put(SplitExpression.Field.Hours, Architecture.Duration.splitHours);
    durationFieldLookup.put(SplitExpression.Field.Minutes, Architecture.Duration.splitMinutes);
    durationFieldLookup.put(SplitExpression.Field.Seconds, Architecture.Duration.splitSeconds);
    durationFieldLookup.put(SplitExpression.Field.Millis, Architecture.Duration.splitMillis);
    durationFieldLookup.put(SplitExpression.Field.Micros, Architecture.Duration.splitMicros);
    durationFieldLookup.put(SplitExpression.Field.Nanos, Architecture.Duration.splitNanos);

  }

}
