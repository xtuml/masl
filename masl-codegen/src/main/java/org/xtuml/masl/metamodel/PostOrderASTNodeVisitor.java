/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.metamodel;

public abstract class PostOrderASTNodeVisitor extends ASTNodeVisitor {

    @Override
    public void visit(final ASTNode node) {
        node.children().stream().forEach(this::visit);
        super.visit(node);
    }

}
