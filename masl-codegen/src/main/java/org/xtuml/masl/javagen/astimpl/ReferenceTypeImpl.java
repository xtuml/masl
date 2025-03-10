/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.javagen.astimpl;

import org.xtuml.masl.javagen.ast.types.ReferenceType;

public abstract class ReferenceTypeImpl extends TypeImpl implements ReferenceType {

    ReferenceTypeImpl(final ASTImpl ast) {
        super(ast);
    }

    @Override
    public abstract ReferenceTypeImpl deepCopy();
}
