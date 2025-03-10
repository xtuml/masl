/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.translate.cmake.language.arguments;

public class SingleArgument implements SimpleArgument {

    public SingleArgument(final String value) {
        this.value = value;
    }

    private final String value;

    public String getValue() {
        return value;
    }

    @Override
    public String getText() {
        return getValue();
    }

}