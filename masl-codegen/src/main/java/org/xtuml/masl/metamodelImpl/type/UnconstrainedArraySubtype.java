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
import org.xtuml.masl.metamodelImpl.expression.Expression;
import org.xtuml.masl.metamodelImpl.expression.RangeExpression;

import java.util.List;

public class UnconstrainedArraySubtype extends BasicType
        implements org.xtuml.masl.metamodel.type.UnconstrainedArraySubtype {

    private final UserDefinedType fullType;
    private final Expression range;

    public static UnconstrainedArraySubtype create(final UserDefinedType containedType, final Expression range) {
        if (containedType == null || range == null) {
            return null;
        }

        return new UnconstrainedArraySubtype(containedType, range, false);
    }

    private UnconstrainedArraySubtype(final UserDefinedType fullType, final Expression range, final boolean anonymous) {
        super(fullType.getPosition(), anonymous);
        this.fullType = fullType;
        this.range = range;
    }

    @Override
    public UserDefinedType getFullType() {
        return fullType;
    }

    @Override
    public RangeExpression getRange() {
        return (RangeExpression) range;
    }

    @Override
    public String toString() {
        return fullType + "(" + range + ")";
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof UnconstrainedArraySubtype rhs)) {
            return false;
        } else {

            return fullType.equals(rhs.fullType) && range.equals(rhs.range);
        }
    }

    @Override
    public int hashCode() {

        return fullType.hashCode() * 31 + range.hashCode();
    }

    @Override
    public SequenceType getPrimitiveType() {
        return (SequenceType) fullType.getPrimitiveType();
    }

    @Override
    public UnconstrainedArraySubtype getBasicType() {
        return new UnconstrainedArraySubtype((UserDefinedType) fullType.getBasicType(), range, true);
    }

    @Override
    public ActualType getActualType() {
        return ActualType.UNCONSTRAINED_ARRAY_SUBTYPE;
    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitUnconstrainedArraySubtype(this);
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren(fullType, range);
    }

}
