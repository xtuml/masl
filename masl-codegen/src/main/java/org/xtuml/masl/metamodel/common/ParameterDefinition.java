/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.metamodel.common;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.type.BasicType;

public interface ParameterDefinition extends ASTNode {

    enum Mode {
        IN, OUT
    }

    Mode getMode();

    String getName();

    BasicType getType();

}
