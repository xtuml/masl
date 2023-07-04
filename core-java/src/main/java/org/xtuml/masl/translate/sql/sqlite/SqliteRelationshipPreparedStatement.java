/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 ----------------------------------------------------------------------------
 Classification: UK OFFICIAL
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.translate.sql.sqlite;

import org.xtuml.masl.cppgen.Class;
import org.xtuml.masl.cppgen.Expression;
import org.xtuml.masl.cppgen.Function;
import org.xtuml.masl.translate.sql.main.PreparedStatement;

import java.util.List;

public class SqliteRelationshipPreparedStatement implements PreparedStatement {

    protected PreparedStatement.PreparedStatementType statementType;
    protected String preparedStatement;

    public SqliteRelationshipPreparedStatement(final PreparedStatement.PreparedStatementType statementType) {
        this.statementType = statementType;
    }

    @Override
    public Class getClassType() {
        return SqliteDatabase.preparedStatement;
    }

    @Override
    public String getStatement() {
        return preparedStatement;
    }

    @Override
    public Expression prepare(final Expression statementExpr) {
        return new Function("prepare").asFunctionCall(statementExpr, false);
    }

    @Override
    public Expression execute(final Expression statementExpr, final List<Expression> arguments) {
        return new Function("execute").asFunctionCall(statementExpr, false, arguments);
    }
}
