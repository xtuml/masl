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
package org.xtuml.masl.metamodelImpl.exception;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.common.Positioned;
import org.xtuml.masl.metamodelImpl.domain.Domain;
import org.xtuml.masl.metamodelImpl.error.SemanticError;

public abstract class ExceptionReference extends Positioned
        implements org.xtuml.masl.metamodel.exception.ExceptionReference {

    public static ExceptionReference create(final Domain.Reference domainRef,
                                            final boolean allowBuiltin,
                                            final String name) {
        if (domainRef == null || name == null) {
            return null;
        }

        try {
            final Position
                    position =
                    domainRef.getPosition() == null ? Position.getPosition(name) : domainRef.getPosition();

            if (allowBuiltin) {
                final ExceptionDeclaration exception = domainRef.getDomain().findException(name);
                if (exception == null) {
                    return BuiltinException.create(position, name);
                } else {
                    return exception.getReference(position);
                }
            } else {
                return domainRef.getDomain().getException(name).getReference(position);
            }
        } catch (final SemanticError e) {
            e.report();
            return null;
        }

    }

    @Override
    public <R, P> R accept(final ASTNodeVisitor<R, P> v, final P p) throws Exception {
        return v.visitExceptionReference(this, p);
    }

    public ExceptionReference(final Position position) {
        super(position);
    }

}
