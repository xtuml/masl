/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.metamodelImpl.expression;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.type.BasicType;
import org.xtuml.masl.metamodelImpl.type.TimestampType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimestampLiteral extends LiteralExpression
        implements org.xtuml.masl.metamodel.expression.TimestampLiteral {

    private enum RegexField {
        YEAR, MONTH, DAY_OF_MONTH, WEEK_OF_YEAR, DAY_OF_WEEK, DAY_OF_YEAR, HOUR, MINUTE, SECOND, TZ_SIGN, TZ_HOUR, TZ_MINUTE,
        ;

        int getId() {
            return ordinal() + 1;
        }
    }

    // Regular expressions to parse and ISO 8601:2004 date or date & time. No
    // expanded representations are allowed, and time of day must be specified as
    // either UTC (ie with Z suffix) or as difference from UTC. Dates with no time
    // component are interpreted as midnight UTC on the specified day. Any reduced
    // accuracy representations are interpreted as the earliest moment for the
    // specified date/time.

    private static final String year = "(\\d\\d\\d\\d)";
    private static final String month = "(\\d\\d)";
    private static final String week = "W(\\d\\d)";
    private static final String wday = "(\\d)";
    private static final String yday = "(\\d\\d\\d)";
    private static final String mday = "(\\d\\d)";
    private static final String hour = "(\\d\\d(?:\\.\\d+(?!:))?)";
    private static final String minute = "(\\d\\d(?:\\.\\d+(?!:))?)";
    private static final String second = "(\\d\\d(?:\\.\\d+)?)";
    private static final String tzsign = "(\\+|-)";
    private static final String tzhour = "(\\d\\d)";
    private static final String tzminute = "(\\d\\d)";

    private static final String extd_second = "(?::" + second + ")?";
    private static final String extd_minute = "(?::" + minute + extd_second + ")?";
    private static final String extd_hour = "(?:" + hour + extd_minute + ")";

    private static final String extd_tzminute = "(?::" + tzminute + ")?";
    private static final String extd_tzhour = tzhour + extd_tzminute;
    private static final String extd_tz = "(?:Z|" + tzsign + extd_tzhour + ")";

    private static final String extd_time = "T" + extd_hour + extd_tz;

    // (?|T) alternative makes other alternatives optional if not followed by a
    // time component. The standard specifies that for combined date & time
    // representations, the date shall not be represented with reduced accurracy.
    private static final String extd_mday = month + "(?:(?!T)|-" + mday + ")";
    private static final String extd_wday = week + "(?:(?!T)|-" + wday + ")";
    private static final String extd_yday = yday;
    private static final String extd_date = year + "(?:(?!T)|-" + extd_mday + "|-" + extd_wday + "|-" + extd_yday + ")";

    private static final String extd_dateTime = extd_date + "(?:" + extd_time + ")?";
    private static final Pattern extd_pattern = Pattern.compile(extd_dateTime);

    private static final String basic_second = "(?:" + second + ")?";
    private static final String basic_minute = "(?:" + minute + basic_second + ")?";
    private static final String basic_hour = "(?:" + hour + basic_minute + ")";

    private static final String basic_tzminute = "(?:" + tzminute + ")?";
    private static final String basic_tzhour = tzhour + basic_tzminute;
    private static final String basic_tz = "(?:Z|" + tzsign + basic_tzhour + ")";

    private static final String basic_time = "T" + basic_hour + basic_tz;

    // (?|T) alternative makes other alternatives optional if not followed by a
    // time component. The standard specifies that for combined date & time
    // representations, the date shall not be represented with reduced accurracy.
    // day not optional in basic format (actually, standard says that YYYY-MM is the basic format,
    // but it amounts to the same thing)
    private static final String basic_mday = month + mday;
    private static final String basic_wday = week + "(?:(?!T)|" + wday + ")";
    private static final String basic_yday = yday;
    private static final String
            basic_date =
            year + "(?:(?!T)|" + basic_mday + "|" + basic_wday + "|" + basic_yday + ")";

    private static final String basic_dateTime = basic_date + "(?:" + basic_time + ")?";
    private static final Pattern basic_pattern = Pattern.compile(basic_dateTime);

    private static final BigDecimal SIXTY = new BigDecimal(60);

    private static final BigDecimal NANOS = new BigDecimal(1000000000);

    public static TimestampLiteral create(final Position position, final String literal) {
        try {
            return new TimestampLiteral(position, literal);
        } catch (final SemanticError e) {
            e.report();
            return null;
        }
    }

    private TimestampLiteral(final Position position, final String literal) throws SemanticError {
        super(position);
        original = literal;

        final String theDate = literal.substring(1, literal.length() - 1);
        Matcher matcher = basic_pattern.matcher(theDate);

        if (!matcher.matches()) {
            matcher = extd_pattern.matcher(theDate);
        }

        if (matcher.matches()) {
            rawResult = matcher.toMatchResult();

            calculate();
        } else {
            throw new SemanticError(SemanticErrorCode.TimestampFormatNotRecognised, position);
        }
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof TimestampLiteral obj2)) {
            return false;
        } else {

            return datetime == obj2.datetime && nanos == obj2.nanos;
        }
    }

    @Override
    public int getNanos() {
        return nanos;
    }

    @Override
    public BasicType getType() {
        return TimestampType.createAnonymous();
    }

    @Override
    public Date getValue() {
        return datetime;
    }

    @Override
    public int hashCode() {
        return nanos ^ datetime.hashCode();
    }

    @Override
    public String toString() {
        return original;
    }

    private BigDecimal asDecimal(final RegexField field) {
        return new BigDecimal(asString(field));
    }

    private int asInteger(final RegexField field) {
        return Integer.parseInt(asString(field));
    }

    private String asString(final RegexField field) {
        return rawResult.group(field.getId());
    }

    private void calculate() throws SemanticError {
        final Calendar calendar = new GregorianCalendar();
        calendar.setLenient(false);
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setMinimalDaysInFirstWeek(4);
        calendar.clear();
        nanos = 0;

        // TimeZone
        if (!isPresent(RegexField.TZ_SIGN)) {
            calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        } else {
            final String
                    tz =
                    "GMT" +
                    asString(RegexField.TZ_SIGN) +
                    asString(RegexField.TZ_HOUR) +
                    (isPresent(RegexField.TZ_MINUTE) ? asString(RegexField.TZ_MINUTE) : "");
            calendar.setTimeZone(TimeZone.getTimeZone(tz));
        }

        // Date
        calendar.set(Calendar.YEAR, asInteger(RegexField.YEAR));

        // yyyy-mm-dd format
        if (isPresent(RegexField.MONTH)) {
            calendar.set(Calendar.MONTH, asInteger(RegexField.MONTH) - 1);
        }

        if (isPresent(RegexField.DAY_OF_MONTH)) {
            calendar.set(Calendar.DAY_OF_MONTH, asInteger(RegexField.DAY_OF_MONTH));
        }

        // yyyy-Www-d format
        if (isPresent(RegexField.WEEK_OF_YEAR)) {
            final int week = asInteger(RegexField.WEEK_OF_YEAR);
            final int day = isPresent(RegexField.DAY_OF_WEEK) ? asInteger(RegexField.DAY_OF_WEEK) : 1;

            // Convert from Monday=1 to Calendar days - don't use an array as it might
            // be out of range!
            int calday = day;
            switch (day) {
                case 1:
                    calday = Calendar.MONDAY;
                    break;
                case 2:
                    calday = Calendar.TUESDAY;
                    break;
                case 3:
                    calday = Calendar.WEDNESDAY;
                    break;
                case 4:
                    calday = Calendar.THURSDAY;
                    break;
                case 5:
                    calday = Calendar.FRIDAY;
                    break;
                case 6:
                    calday = Calendar.SATURDAY;
                    break;
                case 7:
                    calday = Calendar.SUNDAY;
                    break;
            }

            calendar.set(Calendar.WEEK_OF_YEAR, week);
            calendar.set(Calendar.DAY_OF_WEEK, calday);

            // I think this is a bug in java Calendar - if the year changes as a
            // result of the day of week X being in the previous or next year (eg for
            // week 1 or 53), then the non-lenient checks throw it out, even though it
            // is valid. As an aside, trying to print the date in the yyyy-Www-d
            // format
            // for any date which exhibits this phenomenon results in the incorrect
            // year being shown. eg 2009-W01-1 (28 Dec 2008 as per ISO 8601), would
            // print
            // as 2008-W01-1. I belive that Calendar should have a
            // YEAR_FOR_WEEK_IN_YEAR field or similar to get/set the year that
            // corresponds to the week number. Similarly for SimpleDateFormat.

            // Do the same checks that lenient = false would do, missing out the Year
            // check. This causes a recalculation of all fields, so future checks
            // shoudl behave as expected.
            calendar.setLenient(true);
            if (calendar.get(Calendar.DAY_OF_WEEK) != calday) {
                throw new SemanticError(SemanticErrorCode.TimestampFieldOutOfRange, getPosition(), "DAY_OF_WEEK");
            }
            if (calendar.get(Calendar.WEEK_OF_YEAR) != week) {
                throw new SemanticError(SemanticErrorCode.TimestampFieldOutOfRange, getPosition(), "WEEK_OF_YEAR");
            }
            calendar.setLenient(false);
        }

        // yyyy-ddd format
        if (isPresent(RegexField.DAY_OF_YEAR)) {
            calendar.set(Calendar.DAY_OF_YEAR, asInteger(RegexField.DAY_OF_YEAR));
        }

        // Time

        // The format specifies that a decimal can only occur in the least
        // significant figure present, but we do not rely on that assumption here
        final BigDecimal hour = isPresent(RegexField.HOUR) ? asDecimal(RegexField.HOUR) : BigDecimal.ZERO;
        final BigDecimal
                minute =
                isPresent(RegexField.MINUTE) ?
                asDecimal(RegexField.MINUTE) :
                hour.remainder(BigDecimal.ONE).multiply(SIXTY);
        final BigDecimal
                second =
                isPresent(RegexField.SECOND) ?
                asDecimal(RegexField.SECOND) :
                minute.remainder(BigDecimal.ONE).multiply(SIXTY);
        final BigDecimal calcNanos = second.remainder(BigDecimal.ONE).multiply(NANOS);

        if (hour.intValue() == 24 &&
            minute.compareTo(BigDecimal.ZERO) == 0 &&
            second.compareTo(BigDecimal.ZERO) == 0 &&
            calcNanos.compareTo(BigDecimal.ZERO) == 0) {
            // Special case ISO 8601 allows T24:00:00 to mean T00:00:00 of the
            // following day
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        } else {
            calendar.set(Calendar.HOUR_OF_DAY, hour.intValue());
            calendar.set(Calendar.MINUTE, minute.intValue());
            calendar.set(Calendar.SECOND, second.intValue());
            nanos = calcNanos.setScale(0, RoundingMode.HALF_UP).intValue();
        }

        try {
            datetime = calendar.getTime();
        } catch (final IllegalArgumentException e) {
            throw new SemanticError(SemanticErrorCode.TimestampFieldOutOfRange, getPosition(), e.getMessage());
        }
    }

    private boolean isPresent(final RegexField field) {
        return rawResult.group(field.getId()) != null;
    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitTimestampLiteral(this);
    }

    private final MatchResult rawResult;

    private Date datetime;

    private int nanos = 0;

    private final String original;

}
