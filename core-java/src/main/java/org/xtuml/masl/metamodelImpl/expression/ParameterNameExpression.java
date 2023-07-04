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
import org.xtuml.masl.metamodelImpl.common.ParameterDefinition;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.type.BasicType;

import java.util.List;

public class ParameterNameExpression extends Expression
        implements org.xtuml.masl.metamodel.expression.ParameterNameExpression {

    private final ParameterDefinition param;

    public ParameterNameExpression(final Position position, final ParameterDefinition param) {
        super(position);
        this.param = param;
    }

    @Override
    public String toString() {
        return param.getName();
    }

    @Override
    public ParameterDefinition getParameter() {
        return param;
    }

    @Override
    public BasicType getType() {
        return param.getType();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ParameterNameExpression obj2)) {
            return false;
        } else {

            return param.equals(obj2.param);
        }
    }

    @Override
    public int hashCode() {

        return param.hashCode();
    }

    @Override
    public void checkWriteableInner(final Position position) throws SemanticError {
        if (param.getMode() == ParameterDefinition.Mode.IN) {
            throw new SemanticError(SemanticErrorCode.AssignToInParameter, position);
        }
    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitParameterNameExpression(this);
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren();
    }

}
