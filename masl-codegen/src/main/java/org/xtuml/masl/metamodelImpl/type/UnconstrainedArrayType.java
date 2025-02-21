/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.metamodelImpl.type;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;

import java.util.List;

public class UnconstrainedArrayType extends FullTypeDefinition
        implements org.xtuml.masl.metamodel.type.UnconstrainedArrayType {

    private final BasicType containedType;
    private final BasicType indexType;

    public UnconstrainedArrayType(final Position position, final BasicType containedType, final BasicType indexType) {
        super(position);
        this.containedType = containedType;
        this.indexType = indexType;
        primitive = SequenceType.createAnonymous(containedType);
    }

    @Override
    public BasicType getContainedType() {
        return containedType;
    }

    @Override
    public BasicType getIndexType() {
        return indexType;
    }

    @Override
    public String toString() {
        return "array (" + indexType + " range<>) of " + containedType;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof UnconstrainedArrayType rhs)) {
            return false;
        } else {

            return containedType.equals(rhs.containedType) && indexType.equals(rhs.indexType);
        }
    }

    @Override
    public int hashCode() {

        return containedType.hashCode() * 31 + indexType.hashCode();
    }

    @Override
    public SequenceType getPrimitiveType() {
        return primitive;
    }

    @Override
    public ActualType getActualType() {
        return ActualType.UNCONSTRAINED_ARRAY;
    }

    private final SequenceType primitive;

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitUnconstrainedArrayType(this);
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren(indexType, containedType);
    }

}
