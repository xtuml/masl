/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.javagen.ast.code;

import org.xtuml.masl.javagen.ast.ASTNode;
import org.xtuml.masl.javagen.ast.expr.Expression;

import java.util.List;

public interface Switch extends Statement {

    interface SwitchBlock extends ASTNode {

        List<? extends Expression> getCaseLabels();

        void addCaseLabel(Expression caseLabel);

        boolean isDefault();

        void setDefault();

        void addStatement(BlockStatement statement);

        List<? extends BlockStatement> getStatements();

    }

    void setDiscriminator(Expression discriminator);

    Expression getDiscriminator();

    List<? extends SwitchBlock> getSwitchBlocks();

    void addSwitchBlock(SwitchBlock switchBlock);

}
