/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.translate.sql.main;

import org.xtuml.masl.cppgen.Class;
import org.xtuml.masl.cppgen.Expression;

import java.util.List;

public interface PreparedStatement {

    enum PreparedStatementType {
        CREATE, INSERT, UPDATE, DELETE
    }

    Class getClassType();

    String getStatement();

    Expression prepare(Expression statementExpr);

    Expression execute(Expression statementExpr, List<Expression> arguments);
}
