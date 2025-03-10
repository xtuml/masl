/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.javagen.ast;

import java.util.Collection;

public abstract class AbstractASTNodeVisitor implements ASTNodeVisitor {

    @Override
    public final void visit(final ASTNode node) throws Exception {
        if (node == null) {
            visitNull();
        } else {
            node.accept(this);
        }
    }

    public final void visit(final Collection<? extends ASTNode> nodes) throws Exception {
        for (final ASTNode node : nodes) {
            visit(node);
        }
    }

}
