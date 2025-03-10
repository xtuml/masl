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

public interface ASTNode {

    void accept(final ASTNodeVisitor v) throws Exception;

    AST getAST();

    Collection<? extends ASTNode> getChildNodes();

    ASTNode getParentNode();

}
