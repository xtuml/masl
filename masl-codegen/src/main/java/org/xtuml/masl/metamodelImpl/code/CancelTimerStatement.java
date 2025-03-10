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
import org.xtuml.masl.metamodelImpl.expression.Expression;

import java.util.List;

public class CancelTimerStatement extends Statement implements org.xtuml.masl.metamodel.code.CancelTimerStatement {

    public static CancelTimerStatement create(final Position position, final Expression timerId) {
        if (timerId == null) {
            return null;
        }

        return new CancelTimerStatement(position, timerId);
    }

    private CancelTimerStatement(final Position position, final Expression timerId) {
        super(position);
        this.timerId = timerId;
    }

    private final Expression timerId;

    @Override
    public String toString() {
        return "cancel " + timerId + ";";

    }

    @Override
    public Expression getTimerId() {
        return timerId;
    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitCancelTimerStatement(this);

    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren(timerId);
    }

}
