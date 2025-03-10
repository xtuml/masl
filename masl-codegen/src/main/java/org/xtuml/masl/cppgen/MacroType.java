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

/**
 * Enable a Macro declaration to be used on the left hand side of a variable
 * definition.
 */
public class MacroType extends Type {

    public MacroType(final MacroCall type) {
        super(type.toString());
    }

    @Override
    boolean preferPassByReference() {
        return false;
    }

}
