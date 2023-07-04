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
import org.xtuml.masl.metamodelImpl.common.Service;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.expression.Expression;

import java.util.List;

public class ReturnStatement extends Statement implements org.xtuml.masl.metamodel.code.ReturnStatement {

    private final Expression returnValue;

    public static ReturnStatement create(final Position position,
                                         final Service currentService,
                                         final Expression expression) {
        if (expression == null) {
            return null;
        }

        try {
            return new ReturnStatement(position, currentService, expression);
        } catch (final SemanticError e) {
            e.report();
            return null;
        }
    }

    public ReturnStatement(final Position position, final Service currentService, final Expression returnValue) throws
                                                                                                                SemanticError {
        super(position);

        if (currentService == null || !currentService.isFunction()) {
            throw new SemanticError(SemanticErrorCode.ReturnNotInFunction, position);
        }

        currentService.getReturnType().checkAssignable(returnValue);

        this.returnValue = returnValue;
    }

    @Override
    public Expression getReturnValue() {
        return returnValue;
    }

    @Override
    public String toString() {
        return "return " + returnValue + ";";
    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitReturnStatement(this);
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren(returnValue);
    }

}
