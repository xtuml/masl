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
import org.xtuml.masl.metamodelImpl.type.BasicType;
import org.xtuml.masl.metamodelImpl.type.InternalType;

import java.util.List;

public class TypeNameExpression extends Expression implements org.xtuml.masl.metamodel.TypeNameExpression {

    public TypeNameExpression(final Position position, final BasicType type) {
        super(position);
        this.type = type;
    }

    @Override
    public BasicType getReferencedType() {
        return type;
    }

    private final BasicType type;

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof TypeNameExpression obj2)) {
            return false;
        } else {

            return type.equals(obj2.type);
        }
    }

    @Override
    public String toString() {
        return type.toString();
    }

    @Override
    public int hashCode() {
        return type.hashCode();
    }

    @Override
    public BasicType getType() {
        return InternalType.TYPE;
    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitTypeNameExpression(this);
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren();
    }

}
