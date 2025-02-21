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

public enum EventType {
    NORMAL("", EventDeclaration.Type.NORMAL), CREATION("creation", EventDeclaration.Type.CREATION), ASSIGNER("assigner",
                                                                                                             EventDeclaration.Type.ASSIGNER);

    private final String text;
    private final EventDeclaration.Type type;

    EventType(final String text, final EventDeclaration.Type type) {
        this.text = text;
        this.type = type;
    }

    public EventDeclaration.Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return text;
    }
}
