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
import org.xtuml.masl.error.ErrorCode;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.unittest.ErrorLog;

import junit.framework.TestCase;

public class TestDurationLiteral extends TestCase {

    public void checkLiteral(final String literal, final long nanos) {
        ErrorLog.getInstance().reset();
        final DurationLiteral result = DurationLiteral.create(null, "@" + literal + "@");
        assertEquals(nanos, result.getNanos());
        ErrorLog.getInstance().checkErrors();
    }

    public void checkLiteral(final String literal, final ErrorCode... errors) {
        ErrorLog.getInstance().reset();
        assertNull(DurationLiteral.create(null, "@" + literal + "@"));
        ErrorLog.getInstance().checkErrors(errors);
    }

    private static final long S = 1000000000;
    private static final long M = 60 * S;
    private static final long H = 60 * M;
    private static final long D = 24 * H;
    private static final long W = 7 * D;

    // Single element

    public void testY0() {
        checkLiteral("P0Y", 0);
    }

    public void testY0d() {
        checkLiteral("P0.0Y", 0);
    }

    public void testY() {
        checkLiteral("P2Y", SemanticErrorCode.IndeterminateDuration);
    }

    public void testYd() {
        checkLiteral("P2.5Y", SemanticErrorCode.IndeterminateDuration);
    }

    public void testM0() {
        checkLiteral("P0M", 0);
    }

    public void testM0d() {
        checkLiteral("P0.0M", 0);
    }

    public void testM() {
        checkLiteral("P2M", SemanticErrorCode.IndeterminateDuration);
    }

    public void testMd() {
        checkLiteral("P2.5M", SemanticErrorCode.IndeterminateDuration);
    }

    public void testW() {
        checkLiteral("P2W", 2 * W);
    }

    public void testWd() {
        checkLiteral("P2.5W", 2 * W + 3 * D + 12 * H);
    }

    public void testD() {
        checkLiteral("P2D", 2 * D);
    }

    public void testDd() {
        checkLiteral("P2.5D", 2 * D + 12 * H);
    }

    public void testTH() {
        checkLiteral("PT2H", 2 * H);
    }

    public void testTHd() {
        checkLiteral("PT2.5H", 2 * H + 30 * M);
    }

    public void testTM() {
        checkLiteral("PT2H", 2 * H);
    }

    public void testTMd() {
        checkLiteral("PT2.5M", 2 * M + 30 * S);
    }

    public void testTS() {
        checkLiteral("PT2H", 2 * H);
    }

    public void testTSd() {
        checkLiteral("PT2.5S", 2 * S + 500000000);
    }

    // Two consecutive elements

    public void testDTH() {
        checkLiteral("P3DT12H", 3 * D + 12 * H);
    }

    public void testDdTH() {
        checkLiteral("P0.0DT0H", SemanticErrorCode.DurationFormatNotRecognised);
    }

    public void testDTHd() {
        checkLiteral("P3DT12.5000H", 3 * D + 12 * H + 30 * M);
    }

    public void testDH() {
        checkLiteral("P0D0H", SemanticErrorCode.DurationFormatNotRecognised);
    }

    // Two non-consecutive elements

    public void testDTM() {
        checkLiteral("P3DT12M", 3 * D + 12 * M);
    }

    public void testTMD() {
        checkLiteral("PT3M12D", SemanticErrorCode.DurationFormatNotRecognised);
    }

    public void testTDM() {
        checkLiteral("PT3D12M", SemanticErrorCode.DurationFormatNotRecognised);
    }

    public void testDdTM() {
        checkLiteral("P3.0DT12M", SemanticErrorCode.DurationFormatNotRecognised);
    }

    public void testDTMd() {
        checkLiteral("P3DT12.5000M", 3 * D + 12 * M + 30 * S);
    }

    public void testDMd() {
        checkLiteral("P0D0M", SemanticErrorCode.DurationFormatNotRecognised);
    }

    // Two time elements

    public void testTHM() {
        checkLiteral("PT3H12.5000M", 3 * H + 12 * M + 30 * S);
    }

    public void testTMH() {
        checkLiteral("PT0M0H", SemanticErrorCode.DurationFormatNotRecognised);
    }

    public void testTHTM() {
        checkLiteral("PT0MT0H", SemanticErrorCode.DurationFormatNotRecognised);
    }

    // The lot
    public void testFull() {
        checkLiteral("P0Y0M1DT1H1M1.000000001S", D + H + M + S + 1);
    }

    // For some reason ISO 8601 says week can only appear on its own...
    public void testFullWithWeek() {
        checkLiteral("P0Y0M1W1DT1H1M1.000000001S", SemanticErrorCode.DurationFormatNotRecognised);
    }

}
