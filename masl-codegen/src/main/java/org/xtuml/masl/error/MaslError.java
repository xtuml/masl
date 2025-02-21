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

public abstract class MaslError extends Exception {

    @Override
    public abstract String getMessage();

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    protected MaslError(final ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public void report() {
        ErrorLog.getInstance().report(this);
    }

    private final ErrorCode errorCode;

}
