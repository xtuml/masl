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
import org.xtuml.masl.metamodelImpl.type.BasicType;

import java.util.List;

public class TypeRange extends LoopSpec implements org.xtuml.masl.metamodel.code.LoopSpec.TypeRange {

    public TypeRange(final String loopVariable, final boolean reverse, final BasicType type) {
        super(loopVariable, reverse, type);
        this.type = type;
    }

    @Override
    public BasicType getType() {
        return this.type;
    }

    @Override
    public String toString() {
        return super.toString() + " " + type + "'elements";
    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitLoopTypeRange(this);
    }

    private final BasicType type;

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren(type);
    }

}
