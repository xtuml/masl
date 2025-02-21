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
import org.xtuml.masl.metamodelImpl.expression.Expression;

public class RangeConstraint extends TypeConstraint implements org.xtuml.masl.metamodel.type.RangeConstraint {

    public RangeConstraint(final Expression range) {
        super(range);
    }

    @Override
    public String toString() {
        return "range " + range;
    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitRangeConstraint(this);
    }

}
