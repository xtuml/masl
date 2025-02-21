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

import java.util.List;

public class DeltaConstraint extends TypeConstraint implements org.xtuml.masl.metamodel.type.DeltaConstraint {

    private final Expression delta;

    public DeltaConstraint(final Expression delta, final RangeConstraint range) {
        super(range.getRange());
        this.delta = delta;
    }

    @Override
    public Expression getDelta() {
        return delta;
    }

    @Override
    public String toString() {
        return "delta " + delta + " range " + range;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof DeltaConstraint rhs)) {
            return false;
        } else {

            return super.equals(obj) && delta.equals(rhs.delta);
        }
    }

    @Override
    public int hashCode() {

        return super.hashCode() * 31 + delta.hashCode();
    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitDeltaConstraint(this);
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren(super.children(), delta);
    }

}
