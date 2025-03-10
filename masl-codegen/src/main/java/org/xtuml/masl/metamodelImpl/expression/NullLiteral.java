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
import org.xtuml.masl.metamodelImpl.type.AnyInstanceType;
import org.xtuml.masl.metamodelImpl.type.BasicType;
import org.xtuml.masl.metamodelImpl.type.InstanceType;

public class NullLiteral extends LiteralExpression implements org.xtuml.masl.metamodel.expression.NullLiteral {

    public NullLiteral(final Position position) {
        this(position, AnyInstanceType.createAnonymous());
    }

    public NullLiteral(final Position position, final BasicType type) {
        super(position);
        this.type = type;
    }

    @Override
    public String toString() {
        return "null";
    }

    @Override
    public BasicType getType() {
        return type;
    }

    @Override
    public Expression resolveInner(final BasicType type) {
        if (type.getPrimitiveType() instanceof InstanceType) {
            return new NullLiteral(getPosition(), type);
        } else {
            return this;
        }
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        return obj instanceof NullLiteral;
    }

    @Override
    public int hashCode() {

        return 0;
    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitNullLiteral(this);
    }

    private final BasicType type;
}
