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

public abstract class NotFound extends SemanticError {

    static private Object[] composeArgs(final String name, final Object... args) {
        final Object[] result = new Object[args.length + 1];
        result[0] = name;
        System.arraycopy(args, 0, result, 1, args.length);
        return result;
    }

    public NotFound(final SemanticErrorCode code, final Position position, final String name, final Object... args) {
        super(code, position, composeArgs(name, args));
    }

}
