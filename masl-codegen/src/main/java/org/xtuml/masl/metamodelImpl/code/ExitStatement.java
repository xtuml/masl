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
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.expression.Expression;
import org.xtuml.masl.metamodelImpl.type.BooleanType;

import java.util.List;

public class ExitStatement extends Statement implements org.xtuml.masl.metamodel.code.ExitStatement {

    public static ExitStatement create(final Position position, final Expression condition) {
        try {
            return new ExitStatement(position, condition);
        } catch (final SemanticError e) {
            e.report();
            return null;
        }
    }

    private final Expression condition;

    public ExitStatement(final Position position, final Expression condition) throws SemanticError {
        super(position);

        if (condition != null && !BooleanType.createAnonymous().isAssignableFrom(condition)) {
            throw new SemanticError(SemanticErrorCode.ExpectedBooleanCondition,
                                    condition.getPosition(),
                                    condition.getType());
        }

        this.condition = condition;
    }

    @Override
    public Expression getCondition() {
        return condition;
    }

    @Override
    public String toString() {
        return "exit" + (condition == null ? "" : " when " + condition) + ";";
    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitExitStatement(this);
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren(condition);
    }

}
