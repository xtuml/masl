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
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.expression.Expression;

import java.util.List;

public class AssignmentStatement extends Statement implements org.xtuml.masl.metamodel.code.AssignmentStatement {

    public static AssignmentStatement create(final Position position, final Expression target, final Expression value) {
        if (target == null || value == null) {
            return null;
        }

        try {
            return new AssignmentStatement(position, target, value);
        } catch (final SemanticError e) {
            e.report();
            return null;
        }
    }

    @Override
    public Expression getTarget() {
        return this.target;
    }

    private final Expression target;
    private final Expression value;

    public AssignmentStatement(final Position position, final Expression target, final Expression value) throws
                                                                                                         SemanticError {
        super(position);

        this.target = target;
        this.value = value.resolve(target.getType());

        target.checkWriteable(position);
        target.getType().checkAssignable(this.value);
    }

    @Override
    public Expression getValue() {
        return value;
    }

    @Override
    public String toString() {
        return target + " := " + value + ";";
    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitAssignmentStatement(this);
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren(target, value);
    }

}
