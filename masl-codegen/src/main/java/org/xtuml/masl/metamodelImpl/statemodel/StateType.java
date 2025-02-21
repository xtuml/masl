/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.metamodelImpl.statemodel;

public enum StateType {
    NORMAL("", State.Type.NORMAL), CREATION("creation", State.Type.CREATION), TERMINAL("terminal",
                                                                                       State.Type.TERMINAL), ASSIGNER(
            "assigner",
            State.Type.ASSIGNER), ASSIGNER_START("assigner start", State.Type.ASSIGNER_START);

    StateType(final String text, final State.Type type) {
        this.text = text;
        this.type = type;
    }

    public State.Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return text;
    }

    private final String text;

    private final State.Type type;
}
