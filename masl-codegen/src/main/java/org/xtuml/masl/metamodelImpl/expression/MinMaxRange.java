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
import org.xtuml.masl.metamodelImpl.type.RangeType;

import java.util.List;

public class MinMaxRange extends RangeExpression implements org.xtuml.masl.metamodel.expression.MinMaxRange {

    private final Expression min;
    private final Expression max;
    private final RangeType type;

    public MinMaxRange(final Expression min, final Expression max) {
        super(min.getPosition());
        this.min = min;
        this.max = max;
        type = RangeType.createAnonymous(max.getType());
    }

    @Override
    public Expression getMin() {
        return min;
    }

    @Override
    public Expression getMax() {
        return max;
    }

    @Override
    public RangeType getType() {
        return type;
    }

    @Override
    public String toString() {
        return min + ".." + max;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof MinMaxRange rhs)) {
            return false;
        } else {

            return min.equals(rhs.min) && max.equals(rhs.max);
        }
    }

    @Override
    public int hashCode() {

        return min.hashCode() * 31 + max.hashCode();
    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitMinMaxRange(this);
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren(min, max);
    }

}
