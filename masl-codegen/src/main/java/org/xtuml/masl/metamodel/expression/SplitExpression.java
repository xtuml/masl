/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.metamodel.expression;

import java.util.List;
import java.util.Set;

public interface SplitExpression extends Expression {

    enum Field {
        CalendarYear, MonthOfYear, DayOfMonth, DayOfYear, WeekYear, WeekOfYear, DayOfWeek, HourOfDay, MinuteOfHour, SecondOfMinute, MilliOfSecond, MicroOfSecond, MicroOfMilli, NanoOfSecond, NanoOfMilli, NanoOfMicro,

        Weeks, Days, Hours, Minutes, Seconds, Millis, Micros, Nanos
    }

    enum Type {
        SPLIT, COMBINE
    }

    List<? extends Expression> getArguments();

    Set<Field> getFields();

    Expression getLhs();

    Type getSplitType();

}
