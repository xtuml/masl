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
import org.xtuml.masl.metamodelImpl.expression.Expression;
import org.xtuml.masl.metamodelImpl.expression.RangeExpression;

import java.util.List;

public abstract class TypeConstraint implements org.xtuml.masl.metamodel.type.TypeConstraint {

    protected final Expression range;

    public TypeConstraint(final Expression range) {
        this.range = range;
    }

    @Override
    public RangeExpression getRange() {
        return (RangeExpression) range;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof TypeConstraint rhs)) {
            return false;
        } else {

            return range.equals(rhs.range);
        }
    }

    @Override
    public int hashCode() {
        return range.hashCode();
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren(range);
    }

}
