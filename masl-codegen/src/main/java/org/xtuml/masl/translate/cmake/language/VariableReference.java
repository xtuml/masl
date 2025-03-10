/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.translate.cmake.language;

import org.xtuml.masl.translate.cmake.Variable;
import org.xtuml.masl.translate.cmake.language.arguments.SimpleArgument;

public class VariableReference implements SimpleArgument {

    private final Variable variable;

    public VariableReference(final Variable variable) {
        this.variable = variable;
    }

    @Override
    public String getText() {
        return "${" + variable.getName() + "}";
    }

}