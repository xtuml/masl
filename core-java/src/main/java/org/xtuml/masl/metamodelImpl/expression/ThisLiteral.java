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

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.common.Service;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.object.ObjectDeclaration;
import org.xtuml.masl.metamodelImpl.object.ObjectService;
import org.xtuml.masl.metamodelImpl.statemodel.State;
import org.xtuml.masl.metamodelImpl.type.BasicType;
import org.xtuml.masl.metamodelImpl.type.InstanceType;

public class ThisLiteral extends LiteralExpression implements org.xtuml.masl.metamodel.expression.ThisLiteral {

    public static ThisLiteral create(final Position position, final Service service, final State state) {
        try {
            return new ThisLiteral(position, service, state);
        } catch (final SemanticError e) {
            e.report();
            return null;
        }
    }

    private ThisLiteral(final Position position, final Service service, final State state) throws SemanticError {
        super(position);

        if (service != null && service instanceof ObjectService && ((ObjectService) service).isInstance()) {
            this.object = ((ObjectService) service).getParentObject();
        } else if (state != null && state.isInstance()) {
            this.object = state.getParentObject();
        } else {
            throw new SemanticError(SemanticErrorCode.ThisNotValid, position);
        }
    }

    public ThisLiteral(final Position position, final ObjectDeclaration object) {
        super(position);
        this.object = object;
    }

    private final ObjectDeclaration object;

    @Override
    public String toString() {
        return "this";
    }

    @Override
    public ObjectDeclaration getObject() {
        return object;
    }

    @Override
    public BasicType getType() {
        return InstanceType.createAnonymous(object);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        return obj instanceof ThisLiteral;
    }

    @Override
    public int hashCode() {

        return object.hashCode();
    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitThisLiteral(this);
    }

}
