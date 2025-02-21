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

public class NotFoundOnParent extends NotFound {

    public NotFoundOnParent(final SemanticErrorCode code,
                            final Position position,
                            final String name,
                            final String parentName) {
        super(code, position, name, parentName);
    }
}
