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

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.utils.HashCode;

public final class RangeType extends CollectionType {

    public static RangeType createAnonymous(final BasicType containedType) {
        return new RangeType(null, containedType);
    }

    private RangeType(final Position position, final BasicType containedType) {
        super(position, containedType, true);
    }

    @Override
    public String toString() {
        return (isAnonymousType() ? "anonymous " : "") + "range of " + getContainedType();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof RangeType rhs)) {
            return false;
        } else {

            return collEquals(rhs);
        }
    }

    @Override
    public int hashCode() {
        return HashCode.combineHashes(collHashCode());
    }

    @Override
    public RangeType getBasicType() {
        return new RangeType(null, getContainedType().getBasicType());
    }

    @Override
    public ActualType getActualType() {
        return null;
    }

    @Override
    public void accept(final ASTNodeVisitor v) {
    }

}
