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
import org.xtuml.masl.metamodelImpl.exception.ExceptionReference;
import org.xtuml.masl.metamodelImpl.expression.Expression;
import org.xtuml.masl.metamodelImpl.type.StringType;

public class RaiseStatement extends Statement implements org.xtuml.masl.metamodel.code.RaiseStatement {

    private final ExceptionReference exception;
    private final Expression message;

    public static RaiseStatement create(final Position position,
                                        final ExceptionReference ref,
                                        final Expression message) {
        try {

            if (message != null) {
                StringType.createAnonymous().checkAssignable(message);
            }

            return new RaiseStatement(position, ref, message);
        } catch (final SemanticError e) {
            e.report();
            return null;
        }
    }

    public RaiseStatement(final Position position, final ExceptionReference exception, final Expression message) {
        super(position);
        this.exception = exception;
        this.message = message;
    }

    @Override
    public ExceptionReference getException() {
        return exception;
    }

    /**
     * @return Returns the exeption text.
     */
    @Override
    public Expression getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "raise" +
               (exception == null ? "" : " " + exception) +
               (message == null ? "" : " ( " + message + " )") +
               ";";
    }

    @Override
    public <R, P> R accept(final ASTNodeVisitor<R, P> v, final P p) throws Exception {
        return v.visitRaiseStatement(this, p);
    }

}
