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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.xtuml.masl.error.ErrorCode;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.unittest.ErrorLog;

import junit.framework.TestCase;

public class TestTimestampLiteral extends TestCase {

    public void checkLiteral(final String literal, final int year, final int month, final int day, final int hour,
            final int min, final int sec, final int nano) {
        final Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        cal.clear();
        cal.set(year, month - 1, day, hour, min, sec);
        final Date expected = cal.getTime();
        ErrorLog.getInstance().reset();
        final TimestampLiteral result = TimestampLiteral.create(null, "@" + literal + "@");
        assertNotNull("Null result", result);
        assertEquals(expected, result.getValue());
        assertEquals(nano, result.getNanos());
        ErrorLog.getInstance().checkErrors();
    }

    public void checkLiteral(final String literal, final ErrorCode... errors) {
        ErrorLog.getInstance().reset();
        assertNull(TimestampLiteral.create(null, "@" + literal + "@"));
        ErrorLog.getInstance().checkErrors(errors);
    }

    // Year

    public void testY() {
        checkLiteral("2009", 2009, 1, 1, 0, 0, 0, 0);
    }

    // Year, Month

    public void testBasicYM() {
        checkLiteral("200902", SemanticErrorCode.TimestampFormatNotRecognised);
    }

    public void testExtYM() {
        checkLiteral("2009-02", 2009, 2, 1, 0, 0, 0, 0);
    }

    public void testBoundaryYM0() {
        checkLiteral("2009-00", SemanticErrorCode.TimestampFieldOutOfRange);
    }

    public void testBoundaryYM1() {
        checkLiteral("2009-01", 2009, 1, 1, 0, 0, 0, 0);
    }

    public void testBoundaryYM12() {
        checkLiteral("2009-12", 2009, 12, 1, 0, 0, 0, 0);
    }

    public void testBoundaryYM13() {
        checkLiteral("2009-13", SemanticErrorCode.TimestampFieldOutOfRange);
    }

    // Year, Month, Day

    public void testBasicYMD() {
        checkLiteral("20090214", 2009, 2, 14, 0, 0, 0, 0);
    }

    public void testExtYMD() {
        checkLiteral("2009-02-14", 2009, 2, 14, 0, 0, 0, 0);
    }

    public void testBoundaryYMD0() {
        checkLiteral("2009-01-00", SemanticErrorCode.TimestampFieldOutOfRange);
    }

    public void testBoundaryYMD1() {
        checkLiteral("2009-01-01", 2009, 1, 1, 0, 0, 0, 0);
    }

    public void testBoundaryYMD28FebNL() {
        checkLiteral("2009-02-28", 2009, 2, 28, 0, 0, 0, 0);
    }

    public void testBoundaryYMD29FebL() {
        checkLiteral("2008-02-29", 2008, 2, 29, 0, 0, 0, 0);
    }

    public void testBoundaryYMD29FebNL() {
        checkLiteral("2009-02-29", SemanticErrorCode.TimestampFieldOutOfRange);
    }

    public void testBoundaryYMD30FebL() {
        checkLiteral("2009-02-30", SemanticErrorCode.TimestampFieldOutOfRange);
    }

    public void testBoundaryYMD30Apr() {
        checkLiteral("2009-04-30", 2009, 4, 30, 0, 0, 0, 0);
    }

    public void testBoundaryYMD31Apr() {
        checkLiteral("2009-04-31", SemanticErrorCode.TimestampFieldOutOfRange);
    }

    public void testBoundaryYMD31Jan() {
        checkLiteral("2009-01-31", 2009, 1, 31, 0, 0, 0, 0);
    }

    public void testBoundaryYMD32Jan() {
        checkLiteral("2009-01-32", SemanticErrorCode.TimestampFieldOutOfRange);
    }

    // Year, DayOfYear

    public void testBasicYD() {
        checkLiteral("2009059", 2009, 2, 28, 0, 0, 0, 0);
    }

    public void testExtYD() {
        checkLiteral("2009-059", 2009, 2, 28, 0, 0, 0, 0);
    }

    public void testBoundaryYD0() {
        checkLiteral("2009-000", SemanticErrorCode.TimestampFieldOutOfRange);
    }

    public void testBoundaryYD1() {
        checkLiteral("2009-001", 2009, 1, 1, 0, 0, 0, 0);
    }

    public void testBoundaryYD365NonLeap() {
        checkLiteral("2009-365", 2009, 12, 31, 0, 0, 0, 0);
    }

    public void testBoundaryYD365Leap() {
        checkLiteral("2008-365", 2008, 12, 30, 0, 0, 0, 0);
    }

    public void testBoundaryYD366Leap() {
        checkLiteral("2008-366", 2008, 12, 31, 0, 0, 0, 0);
    }

    public void testBoundaryYD366NonLeap() {
        checkLiteral("2009-366", SemanticErrorCode.TimestampFieldOutOfRange);
    }

    public void testBoundaryYD367Leap() {
        checkLiteral("2008-367", SemanticErrorCode.TimestampFieldOutOfRange);
    }

    // Year, Week

    public void testBasicYW() {
        checkLiteral("2009W02", 2009, 1, 5, 0, 0, 0, 0);
    }

    public void testExtYW() {
        checkLiteral("2009-W02", 2009, 1, 5, 0, 0, 0, 0);
    }

    public void testBoundaryW0() {
        checkLiteral("2009-W00", SemanticErrorCode.TimestampFieldOutOfRange);
    }

    public void testBoundaryW1() {
        checkLiteral("2009-W01", 2008, 12, 29, 0, 0, 0, 0);
    }

    public void testBoundaryW52() {
        checkLiteral("2008-W52", 2008, 12, 22, 0, 0, 0, 0);
    }

    public void testBoundaryW53Valid() {
        checkLiteral("2009-W53", 2009, 12, 28, 0, 0, 0, 0);
    }

    public void testBoundaryW53Invalid() {
        checkLiteral("2008-W53", SemanticErrorCode.TimestampFieldOutOfRange);
    }

    // Year, Week, Weekday

    public void testBasicYWD() {
        checkLiteral("2009W021", 2009, 1, 5, 0, 0, 0, 0);
    }

    public void testExtYWD() {
        checkLiteral("2009-W02-1", 2009, 1, 5, 0, 0, 0, 0);
    }

    public void testBoundaryYWD0() {
        checkLiteral("2009-W02-0", SemanticErrorCode.TimestampFieldOutOfRange);
    }

    public void testBoundaryYWD1() {
        checkLiteral("2009-W02-1", 2009, 1, 5, 0, 0, 0, 0);
    }

    public void testBoundaryYWD7() {
        checkLiteral("2009-W02-7", 2009, 1, 11, 0, 0, 0, 0);
    }

    public void testBoundaryYWD8() {
        checkLiteral("2009-W02-8", SemanticErrorCode.TimestampFieldOutOfRange);
    }

    // Boundary Cases from wikipedia 'ISO week date' page
    public void testWeek_2004_W53_6() {
        checkLiteral("2004-W53-6", 2005, 01, 01, 0, 0, 0, 0);
    }

    public void testWeek_2004_W53_7() {
        checkLiteral("2004-W53-7", 2005, 01, 02, 0, 0, 0, 0);
    }

    public void testWeek_2005_W52_6() {
        checkLiteral("2005-W52-6", 2005, 12, 31, 0, 0, 0, 0);
    }

    public void testWeek_2007_W01_1() {
        checkLiteral("2007-W01-1", 2007, 01, 01, 0, 0, 0, 0);
    }

    public void testWeek_2007_W52_7() {
        checkLiteral("2007-W52-7", 2007, 12, 30, 0, 0, 0, 0);
    }

    public void testWeek_2008_W01_1() {
        checkLiteral("2008-W01-1", 2007, 12, 31, 0, 0, 0, 0);
    }

    public void testWeek_2008_W01_2() {
        checkLiteral("2008-W01-2", 2008, 01, 01, 0, 0, 0, 0);
    }

    public void testWeek_2008_W52_7() {
        checkLiteral("2008-W52-7", 2008, 12, 28, 0, 0, 0, 0);
    }

    public void testWeek_2009_W01_1() {
        checkLiteral("2009-W01-1", 2008, 12, 29, 0, 0, 0, 0);
    }

    public void testWeek_2009_W01_2() {
        checkLiteral("2009-W01-2", 2008, 12, 30, 0, 0, 0, 0);
    }

    public void testWeek_2009_W01_3() {
        checkLiteral("2009-W01-3", 2008, 12, 31, 0, 0, 0, 0);
    }

    public void testWeek_2009_W01_4() {
        checkLiteral("2009-W01-4", 2009, 01, 01, 0, 0, 0, 0);
    }

    public void testWeek_2009_W53_4() {
        checkLiteral("2009-W53-4", 2009, 12, 31, 0, 0, 0, 0);
    }

    public void testWeek_2009_W53_5() {
        checkLiteral("2009-W53-5", 2010, 01, 01, 0, 0, 0, 0);
    }

    public void testWeek_2009_W53_6() {
        checkLiteral("2009-W53-6", 2010, 01, 02, 0, 0, 0, 0);
    }

    public void testWeek_2009_W53_7() {
        checkLiteral("2009-W53-7", 2010, 01, 03, 0, 0, 0, 0);
    }

    public void testWeek_2010_W01_1() {
        checkLiteral("2010-W01-1", 2010, 01, 04, 0, 0, 0, 0);
    }

    // Hour

    public void testBasicH() {
        checkLiteral("20090101T14Z", 2009, 1, 1, 14, 0, 0, 0);
    }

    public void testBasicHd() {
        checkLiteral("20090101T14.5Z", 2009, 1, 1, 14, 30, 0, 0);
    }

    public void testExtH() {
        checkLiteral("2009-01-01T14Z", 2009, 1, 1, 14, 0, 0, 0);
    }

    public void testExtHd() {
        checkLiteral("2009-01-01T14.5Z", 2009, 1, 1, 14, 30, 0, 0);
    }

    public void testBoundaryH0() {
        checkLiteral("2009-01-01T00Z", 2009, 1, 1, 0, 0, 0, 0);
    }

    public void testBoundaryH23() {
        checkLiteral("2009-01-01T23Z", 2009, 1, 1, 23, 0, 0, 0);
    }

    public void testBoundaryH24() {
        checkLiteral("2009-01-01T24Z", 2009, 1, 2, 0, 0, 0, 0);
    }

    public void testBoundaryH25() {
        checkLiteral("2009-01-01T25Z", SemanticErrorCode.TimestampFieldOutOfRange);
    }

    // Hour, Min

    public void testBasicHM() {
        checkLiteral("20090101T1425Z", 2009, 1, 1, 14, 25, 0, 0);
    }

    public void testBasicHMd() {
        checkLiteral("20090101T1425.251Z", 2009, 1, 1, 14, 25, 15, 60000000);
    }

    public void testExtHM() {
        checkLiteral("2009-01-01T14:25Z", 2009, 1, 1, 14, 25, 0, 0);
    }

    public void testExtHMd() {
        checkLiteral("2009-01-01T14:25.251Z", 2009, 1, 1, 14, 25, 15, 60000000);
    }

    public void testBoundaryHM0() {
        checkLiteral("2009-01-01T12:00Z", 2009, 1, 1, 12, 0, 0, 0);
    }

    public void testBoundaryHM59() {
        checkLiteral("2009-01-01T12:59Z", 2009, 1, 1, 12, 59, 0, 0);
    }

    public void testBoundaryHM60() {
        checkLiteral("2009-01-01T12:60Z", SemanticErrorCode.TimestampFieldOutOfRange);
    }

    public void testBoundaryH24M0() {
        checkLiteral("2009-01-01T24:00Z", 2009, 1, 2, 0, 0, 0, 0);
    }

    public void testBoundaryH24M1() {
        checkLiteral("2009-01-01T24:01Z", SemanticErrorCode.TimestampFieldOutOfRange);
    }

    // Hour, Min, Second

    public void testBasicHMS() {
        checkLiteral("20090101T142543Z", 2009, 1, 1, 14, 25, 43, 0);
    }

    public void testBasicHMSd() {
        checkLiteral("20090101T142543.0025Z", 2009, 1, 1, 14, 25, 43, 2500000);
    }

    public void testExtHMS() {
        checkLiteral("2009-01-01T14:25:43Z", 2009, 1, 1, 14, 25, 43, 0);
    }

    public void testExtHMSd() {
        checkLiteral("2009-01-01T14:25:43.0025Z", 2009, 1, 1, 14, 25, 43, 2500000);
    }

    public void testBoundaryHMS0() {
        checkLiteral("2009-01-01T12:00:00Z", 2009, 1, 1, 12, 0, 0, 0);
    }

    public void testBoundaryHMS59() {
        checkLiteral("2009-01-01T12:00:59Z", 2009, 1, 1, 12, 0, 59, 0);
    }

    public void testBoundaryHMS59_999999999() {
        checkLiteral("2009-01-01T12:00:59.999999999Z", 2009, 1, 1, 12, 0, 59, 999999999);
    }

    public void testBoundaryHMS60() {
        checkLiteral("2009-01-01T12:00:60Z", SemanticErrorCode.TimestampFieldOutOfRange);
    }

    // Date/Time combos

    public void testExtYHMS() {
        checkLiteral("2009T00:00:00Z", SemanticErrorCode.TimestampFormatNotRecognised);
    }

    public void testExtYMHMS() {
        checkLiteral("2009-01T00:00:00Z", SemanticErrorCode.TimestampFormatNotRecognised);
    }

    public void testExtYMDHMS() {
        checkLiteral("2009-01-01T00:00:00Z", 2009, 1, 1, 0, 0, 0, 0);
    }

    public void testExtYWHMS() {
        checkLiteral("2009-W02T00:00:00Z", SemanticErrorCode.TimestampFormatNotRecognised);
    }

    public void testExtYWDHMS() {
        checkLiteral("2009-W02-1T00:00:00Z", 2009, 1, 5, 0, 0, 0, 0);
    }

    public void testExtYDHMS() {
        checkLiteral("2009-001T00:00:00Z", 2009, 1, 1, 0, 0, 0, 0);
    }

    public void testExtYHM() {
        checkLiteral("2009T00:00Z", SemanticErrorCode.TimestampFormatNotRecognised);
    }

    public void testExtYMHM() {
        checkLiteral("2009-01T00:00Z", SemanticErrorCode.TimestampFormatNotRecognised);
    }

    public void testExtYMDHM() {
        checkLiteral("2009-01-01T00:00Z", 2009, 1, 1, 0, 0, 0, 0);
    }

    public void testExtYWHM() {
        checkLiteral("2009-W02T00:00Z", SemanticErrorCode.TimestampFormatNotRecognised);
    }

    public void testExtYWDHM() {
        checkLiteral("2009-W02-1T00:00Z", 2009, 1, 5, 0, 0, 0, 0);
    }

    public void testExtYDHM() {
        checkLiteral("2009-001T00:00Z", 2009, 1, 1, 0, 0, 0, 0);
    }

    public void testExtYH() {
        checkLiteral("2009T00Z", SemanticErrorCode.TimestampFormatNotRecognised);
    }

    public void testExtYMH() {
        checkLiteral("2009-01T00Z", SemanticErrorCode.TimestampFormatNotRecognised);
    }

    public void testExtYMDH() {
        checkLiteral("2009-01-01T00Z", 2009, 1, 1, 0, 0, 0, 0);
    }

    public void testExtYWH() {
        checkLiteral("2009-W02T00Z", SemanticErrorCode.TimestampFormatNotRecognised);
    }

    public void testExtYWDH() {
        checkLiteral("2009-W02-1T00Z", 2009, 1, 5, 0, 0, 0, 0);
    }

    public void testExtYDH() {
        checkLiteral("2009-001T00Z", 2009, 1, 1, 0, 0, 0, 0);
    }

    public void testBasicYHMS() {
        checkLiteral("2009T000000Z", SemanticErrorCode.TimestampFormatNotRecognised);
    }

    public void testBasicYMHMS() {
        checkLiteral("200901T000000Z", SemanticErrorCode.TimestampFormatNotRecognised);
    }

    public void testBasicYMDHMS() {
        checkLiteral("20090101T000000Z", 2009, 1, 1, 0, 0, 0, 0);
    }

    public void testBasicYWHMS() {
        checkLiteral("2009W02T000000Z", SemanticErrorCode.TimestampFormatNotRecognised);
    }

    public void testBasicYWDHMS() {
        checkLiteral("2009W021T000000Z", 2009, 1, 5, 0, 0, 0, 0);
    }

    public void testBasicYDHMS() {
        checkLiteral("2009001T000000Z", 2009, 1, 1, 0, 0, 0, 0);
    }

    public void testBasicYHM() {
        checkLiteral("2009T0000Z", SemanticErrorCode.TimestampFormatNotRecognised);
    }

    public void testBasicYMHM() {
        checkLiteral("200901T0000Z", SemanticErrorCode.TimestampFormatNotRecognised);
    }

    public void testBasicYMDHM() {
        checkLiteral("20090101T0000Z", 2009, 1, 1, 0, 0, 0, 0);
    }

    public void testBasicYWHM() {
        checkLiteral("2009W02T0000Z", SemanticErrorCode.TimestampFormatNotRecognised);
    }

    public void testBasicYWDHM() {
        checkLiteral("2009W021T0000Z", 2009, 1, 5, 0, 0, 0, 0);
    }

    public void testBasicYDHM() {
        checkLiteral("2009001T0000Z", 2009, 1, 1, 0, 0, 0, 0);
    }

    public void testBasicYH() {
        checkLiteral("2009T00Z", SemanticErrorCode.TimestampFormatNotRecognised);
    }

    public void testBasicYMH() {
        checkLiteral("200901T00Z", SemanticErrorCode.TimestampFormatNotRecognised);
    }

    public void testBasicYMDH() {
        checkLiteral("20090101T00Z", 2009, 1, 1, 0, 0, 0, 0);
    }

    public void testBasicYWH() {
        checkLiteral("2009W02T00Z", SemanticErrorCode.TimestampFormatNotRecognised);
    }

    public void testBasicYWDH() {
        checkLiteral("2009W021T00Z", 2009, 1, 5, 0, 0, 0, 0);
    }

    public void testBasicYDH() {
        checkLiteral("2009001T00Z", 2009, 1, 1, 0, 0, 0, 0);
    }

    // Timezone

    public void testExtPTZH() {
        checkLiteral("2009-01-01T12:00:00+01", 2009, 1, 1, 11, 0, 0, 0);
    }

    public void testExtPTZHM() {
        checkLiteral("2009-01-01T12:00:00+01:30", 2009, 1, 1, 10, 30, 0, 0);
    }

    public void testExtNTZH() {
        checkLiteral("2009-01-01T12:00:00-01", 2009, 1, 1, 13, 0, 0, 0);
    }

    public void testExtNTZHM() {
        checkLiteral("2009-01-01T12:00:00-01:30", 2009, 1, 1, 13, 30, 0, 0);
    }

    public void testExtDecPTZH() {
        checkLiteral("2009-01-01T12:00:00.1+01", 2009, 1, 1, 11, 0, 0, 100000000);
    }

    public void testExtDecPTZHM() {
        checkLiteral("2009-01-01T12:00:00.1+01:30", 2009, 1, 1, 10, 30, 0, 100000000);
    }

    public void testExtDecNTZH() {
        checkLiteral("2009-01-01T12:00:00.1-01", 2009, 1, 1, 13, 0, 0, 100000000);
    }

    public void testExtDecTZHM() {
        checkLiteral("2009-01-01T12:00:00.1-01:30", 2009, 1, 1, 13, 30, 0, 100000000);
    }

    public void testBasicPTZH() {
        checkLiteral("20090101T120000+01", 2009, 1, 1, 11, 0, 0, 0);
    }

    public void testBasicPTZHM() {
        checkLiteral("20090101T120000+0130", 2009, 1, 1, 10, 30, 0, 0);
    }

    public void testBasicNTZH() {
        checkLiteral("20090101T120000-01", 2009, 1, 1, 13, 0, 0, 0);
    }

    public void testBasicNTZHM() {
        checkLiteral("20090101T120000-0130", 2009, 1, 1, 13, 30, 0, 0);
    }

    public void testBasicDecPTZH() {
        checkLiteral("20090101T120000.1+01", 2009, 1, 1, 11, 0, 0, 100000000);
    }

    public void testBasicDecPTZHM() {
        checkLiteral("20090101T120000.1+0130", 2009, 1, 1, 10, 30, 0, 100000000);
    }

    public void testBasicDecNTZH() {
        checkLiteral("20090101T120000.1-01", 2009, 1, 1, 13, 0, 0, 100000000);
    }

    public void testBasicDecTZHM() {
        checkLiteral("20090101T120000.1-0130", 2009, 1, 1, 13, 30, 0, 100000000);
    }

    public void test2DigitYExt() {
        checkLiteral("09-01-01Z", SemanticErrorCode.TimestampFormatNotRecognised);
    }

    public void testMixBasicExtZ() {
        checkLiteral("20090101T00:00:00Z", SemanticErrorCode.TimestampFormatNotRecognised);
    }

    public void testMixEBZ() {
        checkLiteral("2009-01-01T000000Z", SemanticErrorCode.TimestampFormatNotRecognised);
    }

    public void testMixEEB() {
        checkLiteral("2009-01-01T00:00:00+0000", SemanticErrorCode.TimestampFormatNotRecognised);
    }

    public void testMixBBE() {
        checkLiteral("20090101T000000+00:00", SemanticErrorCode.TimestampFormatNotRecognised);
    }

    public void testDecimalMS() {
        checkLiteral("2009-01-01T00:00.5:00Z", SemanticErrorCode.TimestampFormatNotRecognised);
    }

    public void testDecimalHM() {
        checkLiteral("2009-01-01T00.5:00:00Z", SemanticErrorCode.TimestampFormatNotRecognised);
    }

}
