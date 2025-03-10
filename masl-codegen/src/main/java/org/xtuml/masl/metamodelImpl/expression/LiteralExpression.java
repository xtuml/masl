/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.metamodelImpl.expression;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodelImpl.common.Position;

import java.util.List;

public abstract class LiteralExpression extends Expression
        implements org.xtuml.masl.metamodel.expression.LiteralExpression {

    LiteralExpression(final Position position) {
        super(position);
    }

    @Override
    public LiteralExpression evaluate() {
        return this;
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren();
    }

}
