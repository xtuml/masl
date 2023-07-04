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

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.expression.Expression;

public class AssignmentStatement extends Statement implements org.xtuml.masl.metamodel.code.AssignmentStatement {

    public static AssignmentStatement create(final Position position, final Expression target, final Expression value) {
        if (target == null || value == null) {
            return null;
        }

        try {
            return new AssignmentStatement(position, target, value);
        } catch (final SemanticError e) {
            e.report();
            return null;
        }
    }

    @Override
    public Expression getTarget() {
        return this.target;
    }

    private final Expression target;
    private final Expression value;

    public AssignmentStatement(final Position position, final Expression target, final Expression value) throws
                                                                                                         SemanticError {
        super(position);

        this.target = target;
        this.value = value.resolve(target.getType());

        target.checkWriteable(position);
        target.getType().checkAssignable(this.value);
    }

    @Override
    public Expression getValue() {
        return value;
    }

    @Override
    public String toString() {
        return target + " := " + value + ";";
    }

    @Override
    public <R, P> R accept(final ASTNodeVisitor<R, P> v, final P p) throws Exception {
        return v.visitAssignmentStatement(this, p);
    }

}
