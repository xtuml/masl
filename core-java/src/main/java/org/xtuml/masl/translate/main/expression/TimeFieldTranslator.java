/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 ----------------------------------------------------------------------------
 Classification: UK OFFICIAL
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.translate.main.expression;

import org.xtuml.masl.cppgen.Expression;
import org.xtuml.masl.metamodel.expression.TimeFieldExpression;
import org.xtuml.masl.translate.main.Architecture;
import org.xtuml.masl.translate.main.Scope;

public class TimeFieldTranslator extends ExpressionTranslator {

    TimeFieldTranslator(final TimeFieldExpression expression, final Scope scope) {

        final Expression lhs = ExpressionTranslator.createTranslator(expression.getLhs(), scope).getReadExpression();
        switch (expression.getField()) {
            case CalendarYear:
                setReadExpression(Architecture.Timestamp.toCalendarYear(lhs));
                break;
            case MonthOfYear:
                setReadExpression(Architecture.Timestamp.toMonthOfYear(lhs));
                break;
            case DayOfMonth:
                setReadExpression(Architecture.Timestamp.toDayOfMonth(lhs));
                break;
            case WeekYear:
                setReadExpression(Architecture.Timestamp.toWeekYear(lhs));
                break;
            case WeekOfYear:
                setReadExpression(Architecture.Timestamp.toWeekOfYear(lhs));
                break;
            case DayOfWeek:
                setReadExpression(Architecture.Timestamp.toDayOfWeek(lhs));
                break;
            case DayOfYear:
                setReadExpression(Architecture.Timestamp.toDayOfYear(lhs));
                break;
            case HourOfDay:
                setReadExpression(Architecture.Timestamp.toHourOfDay(lhs));
                break;
            case MinuteOfHour:
                setReadExpression(Architecture.Timestamp.toMinuteOfHour(lhs));
                break;
            case SecondOfMinute:
                setReadExpression(Architecture.Timestamp.toSecondOfMinute(lhs));
                break;
            case MilliOfSecond:
                setReadExpression(Architecture.Timestamp.toMilliOfSecond(lhs));
                break;
            case MicroOfMilli:
                setReadExpression(Architecture.Timestamp.toMicroOfMilli(lhs));
                break;
            case NanoOfMicro:
                setReadExpression(Architecture.Timestamp.toNanoOfMicro(lhs));
                break;
            case MicroOfSecond:
                setReadExpression(Architecture.Timestamp.toMicroOfSecond(lhs));
                break;
            case NanoOfMilli:
                setReadExpression(Architecture.Timestamp.toNanoOfMilli(lhs));
                break;
            case NanoOfSecond:
                setReadExpression(Architecture.Timestamp.toNanoOfSecond(lhs));
                break;
            case Weeks:
                setReadExpression(Architecture.Duration.toWeeks(lhs));
                break;
            case Days:
                setReadExpression(Architecture.Duration.toDays(lhs));
                break;
            case Hours:
                setReadExpression(Architecture.Duration.toHours(lhs));
                break;
            case Minutes:
                setReadExpression(Architecture.Duration.toMinutes(lhs));
                break;
            case Seconds:
                setReadExpression(Architecture.Duration.toSeconds(lhs));
                break;
            case Millis:
                setReadExpression(Architecture.Duration.toMillis(lhs));
                break;
            case Micros:
                setReadExpression(Architecture.Duration.toMicros(lhs));
                break;
            case Nanos:
                setReadExpression(Architecture.Duration.toNanos(lhs));
                break;
            case DurationDayOfWeek:
                setReadExpression(Architecture.Duration.toDayOfWeek(lhs));
                break;
            case DurationHourOfDay:
                setReadExpression(Architecture.Duration.toHourOfDay(lhs));
                break;
            case DurationMinuteOfHour:
                setReadExpression(Architecture.Duration.toMinuteOfHour(lhs));
                break;
            case DurationSecondOfMinute:
                setReadExpression(Architecture.Duration.toSecondOfMinute(lhs));
                break;
            case DurationMilliOfSecond:
                setReadExpression(Architecture.Duration.toMilliOfSecond(lhs));
                break;
            case DurationMicroOfMilli:
                setReadExpression(Architecture.Duration.toMicroOfMilli(lhs));
                break;
            case DurationNanoOfMicro:
                setReadExpression(Architecture.Duration.toNanoOfMicro(lhs));
                break;
            case DurationMicroOfSecond:
                setReadExpression(Architecture.Duration.toMicroOfSecond(lhs));
                break;
            case DurationNanoOfMilli:
                setReadExpression(Architecture.Duration.toNanoOfMilli(lhs));
                break;
            case DurationNanoOfSecond:
                setReadExpression(Architecture.Duration.toNanoOfSecond(lhs));
                break;
        }
    }

}
