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

import org.xtuml.masl.javagen.ast.expr.Expression;
import org.xtuml.masl.javagen.ast.expr.StatementExpression;

import java.util.List;

public interface For extends Statement {

    void addStartExpression(StatementExpression expression);

    void addUpdateExpression(StatementExpression expression);

    Expression getCondition();

    List<? extends StatementExpression> getStartExpressions();

    LocalVariable getVariable();

    Expression getCollection();

    Statement getStatement();

    List<? extends StatementExpression> getUpdateExpressions();

    void setCollection(Expression collection);

    void setCondition(Expression condition);

    void setVariable(LocalVariable variable);

    void setStatement(Statement statement);
}
