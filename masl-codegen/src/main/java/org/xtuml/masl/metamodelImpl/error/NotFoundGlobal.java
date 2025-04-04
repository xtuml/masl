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

public class NotFoundGlobal extends NotFound {

    public NotFoundGlobal(final SemanticErrorCode code, final Position position, final String name) {
        super(code, position, name);
    }
}
