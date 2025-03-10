/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.javagen.astimpl;

import org.xtuml.masl.javagen.ast.ASTNodeVisitor;
import org.xtuml.masl.javagen.ast.code.EmptyStatement;

public class EmptyStatementImpl extends StatementImpl implements EmptyStatement {

    EmptyStatementImpl(final ASTImpl ast) {
        super(ast);
    }

    @Override
    public void accept(final ASTNodeVisitor v) throws Exception {
        v.visitEmptyStatement(this);
    }

}
