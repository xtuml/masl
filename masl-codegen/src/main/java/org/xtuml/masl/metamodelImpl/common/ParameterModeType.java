/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.metamodelImpl.common;

public enum ParameterModeType {
    IN("in", ParameterDefinition.Mode.IN), OUT("out", ParameterDefinition.Mode.OUT);

    private final String text;
    private final ParameterDefinition.Mode mode;

    ParameterModeType(final String text, final ParameterDefinition.Mode mode) {
        this.text = text;
        this.mode = mode;
    }

    public ParameterDefinition.Mode getMode() {
        return mode;
    }

    @Override
    public String toString() {
        return text;
    }
}
