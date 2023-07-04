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

import java.util.List;

public class ExitStatement extends Statement implements org.xtuml.masl.metamodel.code.ExitStatement {

    public static ExitStatement create(final Position position, final Expression condition) {
        try {
            return new ExitStatement(position, condition);
        } catch (final SemanticError e) {
            e.report();
            return null;
        }
    }

    private final Expression condition;

    public ExitStatement(final Position position, final Expression condition) throws SemanticError {
        super(position);

        if (condition != null && !BooleanType.createAnonymous().isAssignableFrom(condition)) {
            throw new SemanticError(SemanticErrorCode.ExpectedBooleanCondition,
                                    condition.getPosition(),
                                    condition.getType());
        }

        this.condition = condition;
    }

    @Override
    public Expression getCondition() {
        return condition;
    }

    @Override
    public String toString() {
        return "exit" + (condition == null ? "" : " when " + condition) + ";";
    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitExitStatement(this);
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren(condition);
    }

}
