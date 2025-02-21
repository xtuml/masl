/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.error;

import java.util.ArrayList;
import java.util.List;

public class ErrorLog {

    private static final ErrorLog instance = new ErrorLog();

    private ErrorLog() {
    }

    public static ErrorLog getInstance() {
        return instance;
    }

    private final List<ErrorListener> errorListeners = new ArrayList<>();

    public void addErrorListener(final ErrorListener listener) {
        errorListeners.add(listener);
    }

    public void report(final MaslError error) {
        for (final ErrorListener listener : errorListeners) {
            listener.errorReported(error);
        }

    }

}
