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

import java.util.List;

public class VariableElements extends LoopSpec implements org.xtuml.masl.metamodel.code.LoopSpec.VariableElements {

    public VariableElements(final String loopVariable, final boolean reverse, final Expression variable) {
        super(loopVariable, reverse, variable.getType().getPrimitiveType().getContainedType());
        this.variable = variable;
    }

    @Override
    public Expression getVariable() {
        return this.variable;
    }

    @Override
    public String toString() {
        return super.toString() + " " + variable + "'elements";
    }

    private final Expression variable;

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitLoopVariableElements(this);
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren(variable);
    }

}
