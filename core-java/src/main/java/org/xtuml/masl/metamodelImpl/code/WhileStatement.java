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
package org.xtuml.masl.metamodelImpl.code;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.expression.Expression;
import org.xtuml.masl.metamodelImpl.type.BooleanType;
import org.xtuml.masl.utils.TextUtils;

import java.util.Collections;
import java.util.List;

public class WhileStatement extends Statement implements org.xtuml.masl.metamodel.code.WhileStatement {

    private final Expression condition;
    private final List<Statement> statements;

    public static WhileStatement create(final Position position,
                                        final Expression condition,
                                        final List<Statement> statements) {
        if (condition == null || statements == null) {
            return null;
        }

        try {
            return new WhileStatement(position, condition, statements);
        } catch (final SemanticError e) {
            e.report();
            return null;
        }
    }

    private WhileStatement(final Position position, final Expression condition, final List<Statement> statements) throws
                                                                                                                  SemanticError {
        super(position);
        this.condition = condition;
        this.statements = statements;
        statements.forEach(s -> s.setParentStatement(this));

        if (!BooleanType.createAnonymous().isAssignableFrom(condition)) {
            throw new SemanticError(SemanticErrorCode.ExpectedBooleanCondition,
                                    condition.getPosition(),
                                    condition.getType());
        }

    }

    @Override
    public Expression getCondition() {
        return this.condition;
    }

    @Override
    public List<Statement> getStatements() {
        return Collections.unmodifiableList(statements);
    }

    @Override
    public List<Statement> getChildStatements() {
        return Collections.unmodifiableList(statements);
    }

    @Override
    public String toString() {
        return "while " + condition + " loop\n" + TextUtils.formatList(statements, "", "", "\n", "", "") + "end loop;";
    }

    @Override
    public String toAbbreviatedString() {
        return "while " + condition + " loop ...";
    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitWhileStatement(this);
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren(condition, statements);
    }

}
