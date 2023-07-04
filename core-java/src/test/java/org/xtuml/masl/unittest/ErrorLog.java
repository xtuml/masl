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
package org.xtuml.masl.unittest;
import java.util.HashMap;

import org.junit.Assert;
import org.xtuml.masl.error.ErrorCode;
import org.xtuml.masl.error.ErrorListener;
import org.xtuml.masl.error.MaslError;
import org.xtuml.masl.utils.TextUtils;

public class ErrorLog implements ErrorListener {

    private ErrorLog() {
        org.xtuml.masl.error.ErrorLog.getInstance().addErrorListener(this);
    }

    @Override
    public void errorReported(final MaslError error) {
        final ErrorCode code = error.getErrorCode();

        raised.put(code, getCount(code) + 1);
    }

    private int getCount(final ErrorCode code) {
        final Integer result = raised.get(code);
        return result == null ? 0 : result;
    }

    public void reset() {
        raised.clear();
    }

    public boolean removeError(final ErrorCode code) {
        final int currentCount = getCount(code);
        if (currentCount == 0) {
            return false;
        } else if (currentCount == 1) {
            raised.remove(code);
        } else {
            raised.put(code, currentCount - 1);
        }
        return true;
    }

    public void checkErrors(final ErrorCode... expected) {
        for (final ErrorCode error : expected) {
            Assert.assertTrue(error + " not found", removeError(error));
        }
        if (raised.size() > 0) {
            Assert.fail("Unexpected Error Codes : " + TextUtils.formatList(raised.keySet(), "", ", ", ""));
        }
        reset();
    }

    private final HashMap<ErrorCode, Integer> raised = new HashMap<>();

    public static ErrorLog getInstance() {
        return instance;
    }

    static ErrorLog instance = new ErrorLog();
}
