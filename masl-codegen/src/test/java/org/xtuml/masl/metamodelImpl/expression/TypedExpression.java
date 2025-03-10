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
import java.util.List;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.type.BasicType;

public class TypedExpression extends Expression {

    public TypedExpression(final BasicType type) {
        super(null);
        this.type = type;
    }

    @Override
    public boolean equals(final Object obj) {
        return (this == obj);
    }

    @Override
    public BasicType getType() {
        return type;
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }

    private final BasicType type;

    @Override
    public void accept(final ASTNodeVisitor v) {
    }

    @Override
    public String toString() {
        return type.toString();
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren();
    }

}
