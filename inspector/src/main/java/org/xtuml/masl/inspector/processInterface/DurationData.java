// 
// Filename : TimeData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.processInterface;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public abstract class DurationData extends DataValue<DurationData> implements Comparable<DurationData> {

    private static class SplitTime {

        private static long hoursPerDay = 24;
        private static long minutesPerHour = 60;
        private static long secondsPerMinute = 60;
        private static long nanosPerSecond = 1000000000;

        SplitTime(final long nanoseconds) {
            nanos = nanoseconds;
            normalise();
        }

        void normalise() {
            final long nanoseconds = (negative ? -1 : 1)
                    * (((days * hoursPerDay + hours) * minutesPerHour + minutes) * secondsPerMinute + seconds)
                    * nanosPerSecond + nanos;

            negative = nanoseconds < 0;
            final long totalNanos = Math.abs(nanoseconds);
            final long totalSeconds = totalNanos / nanosPerSecond;
            final long totalMinutes = totalNanos / (secondsPerMinute * nanosPerSecond);
            final long totalHours = totalNanos / (minutesPerHour * secondsPerMinute * nanosPerSecond);
            final long totalDays = totalNanos / (hoursPerDay * minutesPerHour * secondsPerMinute * nanosPerSecond);

            days = totalDays;
            hours = totalHours - totalDays * hoursPerDay;
            minutes = totalMinutes - totalHours * minutesPerHour;
            seconds = totalSeconds - totalMinutes * secondsPerMinute;
            nanos = totalNanos - totalSeconds * nanosPerSecond;
            secondsFraction = (double) nanos / nanosPerSecond;
            hasTime = hours + minutes + seconds + nanos > 0;
        }

        boolean hasTime = false;
        boolean negative = false;
        long days = 0;
        long hours = 0;
        long minutes = 0;
        long seconds = 0;
        long nanos = 0;

        double secondsFraction = 0.0;

    }

    static java.text.NumberFormat fractionalFormat = NumberFormat.getInstance();

    static {
        fractionalFormat.setMaximumIntegerDigits(0);
        fractionalFormat.setMinimumIntegerDigits(0);
        fractionalFormat.setMinimumFractionDigits(0);
        fractionalFormat.setMaximumFractionDigits(9);
        fractionalFormat.setGroupingUsed(false);
    }

    public DurationData() {

    }

    public DurationData(final String formatted) throws ParseException {
        fromString(formatted);
    }

    @Override
    public int compareTo(final DurationData rhs) {
        return (getNanoseconds() < rhs.getNanoseconds()) ? -1 : (getNanoseconds() > rhs.getNanoseconds()) ? 1 : 0;
    }

    @Override
    public void fromString(final String text) throws ParseException {
        parseISO(text);
    }

    @Override
    public void fromXML(final Node parent) {
        final String formatted = parent.getFirstChild().getNodeValue();
        try {
            parseISO(formatted);
        } catch (final java.text.ParseException e) {
            System.err.println("Error parsing timestamp \"" + formatted + "\"");
        }
    }

    @Override
    public DurationData getValue() {
        return this;
    }

    @Override
    public void setValue(final DurationData value) {
        setNanoseconds(value.getNanoseconds());
    }

    @Override
    public String toString() {
        return toISO();
    }

    public String toISO() {
        if (getNanoseconds() == 0) {
            return "PT0S";
        }

        final SplitTime split = new SplitTime(getNanoseconds());

        String result = (split.negative ? "-" : "") + "P" + (split.days > 0 ? split.days + "D" : "");
        if (split.hasTime) {
            result = result + "T" + (split.hours > 0 ? split.hours + "H" : "")
                    + (split.minutes > 0 ? split.minutes + "M" : "")
                    + ((split.seconds > 0 || split.nanos > 0)
                            ? split.seconds + (split.nanos > 0 ? fractionalFormat.format(split.secondsFraction) : "")
                                    + "S"
                            : "");
        }
        return result;
    }

    @Override
    public Node toXML(final Document document) {
        return document.createTextNode(toISO());
    }

    private long nanoseconds = 0;

    static final String number = "(\\d+(?:\\.\\d+)?(?!..)|\\d+)";
    static final String w = "(?:" + number + "W)?";
    static final String y = "(?:" + number + "Y)?";
    static final String mo = "(?:" + number + "M)?";
    static final String d = "(?:" + number + "D)?";
    static final String h = "(?:" + number + "H)?";
    static final String m = "(?:" + number + "M)?";
    static final String s = "(?:" + number + "S)?";
    static final String t = "(?:T(?=.)" + h + m + s + ")?";
    static final Pattern pattern = Pattern.compile("P(?=.)" + "(?:" + w + "|" + y + mo + d + t + ")");

    static final long S = 1000000000;
    static final long M = 60 * S;
    static final long H = 60 * M;
    static final long D = 24 * H;
    static final long W = 7 * D;

    static final BigDecimal[] multipliers = { BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal(W), new BigDecimal(D),
            new BigDecimal(H), new BigDecimal(M), new BigDecimal(S) };
    static final int[] resultPos = { 2, 3, 1, 4, 5, 6, 7 };

    private void parseISO(final String theDuration) throws ParseException {
        final Matcher matcher = pattern.matcher(theDuration);

        if (matcher.matches()) {
            BigDecimal result = new BigDecimal(0);

            for (int i = 0; i < multipliers.length; ++i) {
                final String str = matcher.group(resultPos[i]);
                if (str != null) {
                    final BigDecimal val = new BigDecimal(str);
                    result = result.add(val.multiply(multipliers[i]));
                    if (multipliers[i].compareTo(BigDecimal.ZERO) == 0 && val.compareTo(BigDecimal.ZERO) != 0) {
                        throw new ParseException("Indeterminate duration", 0);
                    }
                }

            }
            setNanoseconds(result.setScale(0, RoundingMode.HALF_UP).longValue());
        } else {
            throw new ParseException("Duration format not recognised", 0);
        }
    }

    public void setNanoseconds(long nanoseconds) {
        this.nanoseconds = nanoseconds;
    }

    public long getNanoseconds() {
        return nanoseconds;
    }

}
