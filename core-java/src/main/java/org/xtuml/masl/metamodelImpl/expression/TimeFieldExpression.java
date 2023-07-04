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
package org.xtuml.masl.metamodelImpl.expression;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.type.BasicType;
import org.xtuml.masl.metamodelImpl.type.DurationType;
import org.xtuml.masl.metamodelImpl.type.IntegerType;
import org.xtuml.masl.metamodelImpl.type.TimestampType;
import org.xtuml.masl.utils.HashCode;

import java.util.*;

public class TimeFieldExpression extends Expression implements org.xtuml.masl.metamodel.expression.TimeFieldExpression {

    private final static Map<String, Field> tsFieldLookup = new HashMap<String, Field>();
    private final static Map<String, Field> durFieldLookup = new HashMap<String, Field>();

    static {
        tsFieldLookup.put("year", Field.CalendarYear);
        tsFieldLookup.put("month_of_year", Field.MonthOfYear);
        tsFieldLookup.put("day_of_month", Field.DayOfMonth);
        tsFieldLookup.put("week_year", Field.WeekYear);
        tsFieldLookup.put("week_of_year", Field.WeekOfYear);
        tsFieldLookup.put("day_of_week", Field.DayOfWeek);
        tsFieldLookup.put("day_of_year", Field.DayOfYear);
        tsFieldLookup.put("hour_of_day", Field.HourOfDay);
        tsFieldLookup.put("minute_of_hour", Field.MinuteOfHour);
        tsFieldLookup.put("second_of_minute", Field.SecondOfMinute);
        tsFieldLookup.put("second_of_minute", Field.SecondOfMinute);
        tsFieldLookup.put("millisecond_of_second", Field.MilliOfSecond);
        tsFieldLookup.put("microsecond_of_second", Field.MicroOfSecond);
        tsFieldLookup.put("nanosecond_of_second", Field.NanoOfSecond);
        tsFieldLookup.put("microsecond_of_millisecond", Field.MicroOfMilli);
        tsFieldLookup.put("nanosecond_of_millisecond", Field.NanoOfMilli);
        tsFieldLookup.put("nanosecond_of_microsecond", Field.NanoOfMicro);

        durFieldLookup.put("weeks", Field.Weeks);
        durFieldLookup.put("days", Field.Days);

        durFieldLookup.put("hours", Field.Hours);
        durFieldLookup.put("minutes", Field.Minutes);
        durFieldLookup.put("seconds", Field.Seconds);

        durFieldLookup.put("milliseconds", Field.Millis);
        durFieldLookup.put("microseconds", Field.Micros);
        durFieldLookup.put("nanoseconds", Field.Nanos);

        durFieldLookup.put("day_of_week", Field.DurationDayOfWeek);
        durFieldLookup.put("hour_of_day", Field.DurationHourOfDay);
        durFieldLookup.put("minute_of_hour", Field.DurationMinuteOfHour);
        durFieldLookup.put("second_of_minute", Field.DurationSecondOfMinute);
        durFieldLookup.put("second_of_minute", Field.DurationSecondOfMinute);
        durFieldLookup.put("millisecond_of_second", Field.DurationMilliOfSecond);
        durFieldLookup.put("microsecond_of_second", Field.DurationMicroOfSecond);
        durFieldLookup.put("nanosecond_of_second", Field.DurationNanoOfSecond);
        durFieldLookup.put("microsecond_of_millisecond", Field.DurationMicroOfMilli);
        durFieldLookup.put("nanosecond_of_millisecond", Field.DurationNanoOfMilli);
        durFieldLookup.put("nanosecond_of_microsecond", Field.DurationNanoOfMicro);
    }

    TimeFieldExpression(final Position position, final Expression lhs, final String characteristic) throws
                                                                                                    SemanticError {
        super(position);
        this.lhs = lhs;
        this.characteristic = characteristic;

        if (TimestampType.createAnonymous().isAssignableFrom(lhs)) {
            field = decodeTimestampSingle(characteristic);
        } else if (DurationType.createAnonymous().isAssignableFrom(lhs)) {
            field = decodeDurationSingle(characteristic);
        } else {
            throw new SemanticError(SemanticErrorCode.CharacteristicNotValid, position, characteristic, lhs.getType());
        }

    }

    private TimeFieldExpression(final Position position,
                                final Expression lhs,
                                final String characteristic,
                                final Field field) {
        super(position);
        this.characteristic = characteristic;
        this.lhs = lhs;
        this.field = field;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj != null) {
            if (this == obj) {
                return true;
            }
            if (obj.getClass() == getClass()) {
                final TimeFieldExpression obj2 = ((TimeFieldExpression) obj);
                return lhs.equals(obj2.lhs) && characteristic == obj2.characteristic;
            } else {
                return false;
            }
        }
        return false;
    }

    @Override
    public Field getField() {
        return field;
    }

    @Override
    public int getFindAttributeCount() {
        return lhs.getFindAttributeCount();
    }

    @Override
    public Expression getFindSkeletonInner() {
        return new TimeFieldExpression(getPosition(), lhs.getFindSkeleton(), characteristic, field);
    }

    @Override
    public Expression getLhs() {
        return lhs;
    }

    @Override
    public BasicType getType() {
        return IntegerType.createAnonymous();
    }

    @Override
    public int hashCode() {
        return HashCode.combineHashes(field.hashCode(), lhs.hashCode());
    }

    @Override
    public String toString() {
        return lhs + "'" + characteristic;
    }

    @Override
    protected List<Expression> getFindArgumentsInner() {
        return new ArrayList<Expression>(lhs.getFindArguments());

    }

    @Override
    protected List<FindParameterExpression> getFindParametersInner() {
        return new ArrayList<FindParameterExpression>(lhs.getConcreteFindParameters());

    }

    private Field decodeDurationSingle(final String name) throws SemanticError {
        final Field field = durFieldLookup.get(name);
        if (field == null) {
            throw new SemanticError(SemanticErrorCode.CharacteristicNotValid, getPosition(), name, lhs.getType());
        }
        return field;
    }

    private Field decodeTimestampSingle(final String name) throws SemanticError {
        final Field field = tsFieldLookup.get(name);
        if (field == null) {
            throw new SemanticError(SemanticErrorCode.CharacteristicNotValid, getPosition(), name, lhs.getType());
        }
        return field;
    }

    @Override
    public <R, P> R accept(final ASTNodeVisitor<R, P> v, final P p) throws Exception {
        return v.visitTimeFieldExpression(this, p);
    }

    private final Expression lhs;
    private final String characteristic;

    private final Field field;

    @Override
    public List<Expression> getChildExpressions() {
        return Collections.<Expression>singletonList(lhs);
    }

}
