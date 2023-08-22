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
package org.xtuml.masl.metamodelImpl.expression;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.code.VariableDefinition;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.type.BasicType;

import java.util.List;

public class VariableNameExpression extends Expression
        implements org.xtuml.masl.metamodel.expression.VariableNameExpression {

    public VariableNameExpression(final Position position, final VariableDefinition definition) {
        super(position);
        this.definition = definition;
    }

    @Override
    public BasicType getType() {
        return definition.getType();
    }

    @Override
    public String toString() {
        return definition.getName();
    }

    private final VariableDefinition definition;

    @Override
    public VariableDefinition getVariable() {
        return definition;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof VariableNameExpression obj2)) {
            return false;
        } else {

            return definition.equals(obj2.definition);
        }
    }

    @Override
    public int hashCode() {

        return definition.hashCode();
    }

    @Override
    public void checkWriteableInner(final Position position) throws SemanticError {
        if (definition.isReadonly()) {
            throw new SemanticError(SemanticErrorCode.AssignToReadOnly, position, definition.getName());
        }
    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitVariableNameExpression(this);
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren();
    }

}
