/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.metamodelImpl.error;

import org.xtuml.masl.metamodelImpl.common.Position;

public class AlreadyDefined extends SemanticError {

    public AlreadyDefined(final SemanticErrorCode code,
                          final Position position,
                          final String name,
                          final Position previousDef) {
        super(code, position, name, previousDef.getText(), name, previousDef);
    }
}
