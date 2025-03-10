/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.metamodelImpl.expression;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.type.BasicType;
import org.xtuml.masl.metamodelImpl.type.InternalType;

public class FlushLiteral extends LiteralExpression implements org.xtuml.masl.metamodel.expression.FlushLiteral {

    public FlushLiteral(final Position position) {
        super(position);
    }

    @Override
    public String toString() {
        return "flush";
    }

    @Override
    public BasicType getType() {
        return InternalType.STREAM_MODIFIER;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        return obj instanceof FlushLiteral;
    }

    @Override
    public int hashCode() {

        return 0;
    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitFlushLiteral(this);
    }

}
