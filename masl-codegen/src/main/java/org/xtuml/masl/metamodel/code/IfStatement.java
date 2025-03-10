/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.metamodel.code;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.expression.Expression;

import java.util.List;

public interface IfStatement extends Statement {

    interface Branch extends ASTNode {

        Expression getCondition();

        List<? extends Statement> getStatements();
    }

    List<? extends Branch> getBranches();
}
