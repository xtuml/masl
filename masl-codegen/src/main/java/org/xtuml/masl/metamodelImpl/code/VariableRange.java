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
import org.xtuml.masl.metamodelImpl.type.ArrayType;
import org.xtuml.masl.metamodelImpl.type.BasicType;
import org.xtuml.masl.metamodelImpl.type.IntegerType;

import java.util.List;

public class VariableRange extends LoopSpec implements org.xtuml.masl.metamodel.code.LoopSpec.VariableRange {

    public VariableRange(final String loopVariable, final boolean reverse, final Expression variable) {
        super(loopVariable, reverse, getRangeType(variable));
        this.variable = variable;
    }

    public static BasicType getRangeType(final Expression variable) {
        if (variable.getType().getBasicType() instanceof ArrayType) {
            return ((ArrayType) variable.getType().getBasicType()).getRange().getType();
        } else {
            return IntegerType.createAnonymous();
        }
    }

    @Override
    public Expression getVariable() {
        return this.variable;
    }

    @Override
    public String toString() {
        return super.toString() + " " + variable + "'range";
    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitLoopVariableRange(this);
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren(variable);
    }

    private final Expression variable;

}
