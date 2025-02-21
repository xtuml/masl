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

public class DigitsConstraint extends TypeConstraint implements org.xtuml.masl.metamodel.type.DigitsConstraint {

    private final Expression digits;

    public DigitsConstraint(final Expression digits, final RangeConstraint range) {
        super(range.getRange());
        this.digits = digits;
    }

    @Override
    public Expression getDigits() {
        return digits;
    }

    @Override
    public String toString() {
        return "digits " + digits + " range " + range;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof DigitsConstraint rhs)) {
            return false;
        } else {

            return super.equals(rhs) && digits.equals(rhs.digits);
        }
    }

    @Override
    public int hashCode() {

        return super.hashCode() * 31 + digits.hashCode();
    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitDigitsConstraint(this);
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren(super.children(), digits);
    }

}
