/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.metamodelImpl.code;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.expression.Expression;
import org.xtuml.masl.metamodelImpl.expression.MinMaxRange;

import java.util.List;

public class FromToRange extends LoopSpec implements org.xtuml.masl.metamodel.code.LoopSpec.FromToRange {

    public FromToRange(final String loopVariable, final boolean reverse, final MinMaxRange range) {
        super(loopVariable, reverse, range.getType().getContainedType());
        this.range = range;
    }

    @Override
    public Expression getFrom() {
        return range.getMin();
    }

    @Override
    public Expression getTo() {
        return range.getMax();
    }

    @Override
    public String toString() {
        return super.toString() + " " + range;
    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitLoopFromToRange(this);
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren(range);
    }

    private final MinMaxRange range;

}
