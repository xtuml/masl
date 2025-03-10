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
import org.xtuml.masl.metamodelImpl.common.Position;

import java.util.List;

abstract public class CollectionType extends BasicType implements org.xtuml.masl.metamodel.type.CollectionType {

    private final BasicType containedType;

    public CollectionType(final Position position, final BasicType containedType, final boolean anonymous) {
        super(position, anonymous);
        this.containedType = containedType;
    }

    @Override
    public BasicType getContainedType() {
        return containedType;
    }

    @Override
    public String toString() {
        return (isAnonymousType() ? "anonymous " : "") + "collection of " + containedType;
    }

    protected final boolean collEquals(final CollectionType rhs) {
        return containedType.equals(rhs.containedType);
    }

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public abstract int hashCode();

    protected int collHashCode() {

        return containedType.hashCode();
    }

    @Override
    abstract public CollectionType getBasicType();

    @Override
    public SequenceType getPrimitiveType() {
        return SequenceType.createAnonymous(getContainedType());
    }

    @Override
    public boolean isCollection() {
        return true;
    }

    @Override
    public void checkCanBePublic() {
        containedType.checkCanBePublic();
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren(containedType);
    }

}
