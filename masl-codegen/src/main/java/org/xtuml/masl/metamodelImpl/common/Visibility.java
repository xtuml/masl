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

public enum Visibility {

    PUBLIC("public", org.xtuml.masl.metamodel.common.Visibility.PUBLIC), PRIVATE("private",
                                                                                 org.xtuml.masl.metamodel.common.Visibility.PRIVATE);

    private final String text;
    private final org.xtuml.masl.metamodel.common.Visibility visibility;

    Visibility(final String text, final org.xtuml.masl.metamodel.common.Visibility visibility) {
        this.text = text;
        this.visibility = visibility;
    }

    public org.xtuml.masl.metamodel.common.Visibility getVisibility() {
        return visibility;
    }

    @Override
    public String toString() {
        return text;
    }
}
