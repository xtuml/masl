/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.cppgen;

public enum Visibility {

    PRIVATE("private"), PROTECTED("protected"), PUBLIC("public");

    private final String text;

    Visibility(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
