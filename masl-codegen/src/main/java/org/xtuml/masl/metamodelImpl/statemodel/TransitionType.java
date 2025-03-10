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

public enum TransitionType {
    TO_STATE("", org.xtuml.masl.metamodel.statemodel.TransitionType.TO_STATE), CANNOT_HAPPEN("Cannot_Happen",
                                                                                             org.xtuml.masl.metamodel.statemodel.TransitionType.CANNOT_HAPPEN), IGNORE(
            "Ignore",
            org.xtuml.masl.metamodel.statemodel.TransitionType.IGNORE);

    private final String text;
    private final org.xtuml.masl.metamodel.statemodel.TransitionType type;

    TransitionType(final String text, final org.xtuml.masl.metamodel.statemodel.TransitionType type) {
        this.text = text;
        this.type = type;
    }

    public org.xtuml.masl.metamodel.statemodel.TransitionType getType() {
        return type;
    }

    @Override
    public String toString() {
        return text;
    }
}
