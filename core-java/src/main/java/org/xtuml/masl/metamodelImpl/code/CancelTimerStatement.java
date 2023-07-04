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
import org.xtuml.masl.metamodelImpl.expression.Expression;

public class CancelTimerStatement extends Statement implements org.xtuml.masl.metamodel.code.CancelTimerStatement {

    public static CancelTimerStatement create(final Position position, final Expression timerId) {
        if (timerId == null) {
            return null;
        }

        return new CancelTimerStatement(position, timerId);
    }

    private CancelTimerStatement(final Position position, final Expression timerId) {
        super(position);
        this.timerId = timerId;
    }

    private final Expression timerId;

    @Override
    public String toString() {
        return "cancel " + timerId + ";";

    }

    @Override
    public Expression getTimerId() {
        return timerId;
    }

    @Override
    public <R, P> R accept(final ASTNodeVisitor<R, P> v, final P p) throws Exception {
        return v.visitCancelTimerStatement(this, p);
    }

}
