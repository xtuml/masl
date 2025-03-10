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

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.common.Service;
import org.xtuml.masl.metamodelImpl.common.ServiceOverload;
import org.xtuml.masl.metamodelImpl.type.BasicType;
import org.xtuml.masl.metamodelImpl.type.InternalType;

import java.util.List;

public class ServiceExpression<T extends Service> extends Expression {

    public ServiceExpression(final Position position, final ServiceOverload<T> overload) {
        super(position);
        this.overload = overload;
    }

    public ServiceOverload<T> getOverload() {
        return this.overload;
    }

    @Override
    public String toString() {
        final T first = overload.asList().get(0);
        return first.getQualifiedName();
    }

    /**
     * @return
     * @see org.xtuml.masl.metamodelImpl.expression.Expression#getType()
     */
    @Override
    public BasicType getType() {
        return InternalType.SERVICE;
    }

    private final ServiceOverload<T> overload;

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ServiceExpression<?> obj2)) {
            return false;
        } else {

            return overload.equals(obj2.overload);
        }
    }

    @Override
    public int hashCode() {
        return overload.hashCode();
    }

    @Override
    public void accept(final ASTNodeVisitor v) {
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren();
    }

}
