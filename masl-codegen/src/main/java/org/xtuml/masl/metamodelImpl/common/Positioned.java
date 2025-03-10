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

public abstract class Positioned {

    protected Positioned(final String position) {
        this.position = Position.getPosition(position);
    }

    protected Positioned(final Positioned position) {
        this.position = position.getPosition();
    }

    protected Positioned(final Position position) {
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }

    private final Position position;

}
