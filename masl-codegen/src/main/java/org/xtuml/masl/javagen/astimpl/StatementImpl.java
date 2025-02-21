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

import org.xtuml.masl.javagen.ast.code.BlockStatement;
import org.xtuml.masl.javagen.ast.code.Statement;

public abstract class StatementImpl extends ASTNodeImpl implements BlockStatement, Statement, StatementGroupMember {

    StatementImpl(final ASTImpl ast) {
        super(ast);
    }

}
