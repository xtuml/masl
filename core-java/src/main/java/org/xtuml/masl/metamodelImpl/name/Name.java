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
package org.xtuml.masl.metamodelImpl.name;

import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.common.Positioned;
import org.xtuml.masl.metamodelImpl.common.Service;
import org.xtuml.masl.metamodelImpl.domain.Domain;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.expression.Expression;
import org.xtuml.masl.metamodelImpl.expression.ThisLiteral;
import org.xtuml.masl.metamodelImpl.expression.TypeNameExpression;
import org.xtuml.masl.metamodelImpl.object.AttributeDeclaration;
import org.xtuml.masl.metamodelImpl.object.ObjectDeclaration;
import org.xtuml.masl.metamodelImpl.object.ObjectService;
import org.xtuml.masl.metamodelImpl.statemodel.EventDeclaration;
import org.xtuml.masl.metamodelImpl.statemodel.State;
import org.xtuml.masl.metamodelImpl.type.BuiltinType;

public abstract class Name extends Positioned implements Named {

    public static Expression create(final Domain.Reference domainRef, final String name) {
        if (domainRef == null || name == null) {
            return null;
        }

        try {
            final Position
                    position =
                    domainRef.getPosition() == null ? Position.getPosition(name) : domainRef.getPosition();
            return domainRef.getDomain().getNameLookup().get(name).getReference(position);
        } catch (final SemanticError e) {
            e.report();
            return null;
        }

    }

    public static Expression create(final String name,
                                    final Domain currentDomain,
                                    final ObjectDeclaration currentObject,
                                    final Service currentService,
                                    final State currentState) {
        try {
            final Position position = Position.getPosition(name);

            if (currentObject != null) {
                final boolean
                        hasThis =
                        currentState != null && currentState.isInstance() ||
                        currentService != null &&
                        currentService instanceof ObjectService &&
                        ((ObjectService) currentService).isInstance();

                final Name found = currentObject.getNameLookup().find(name);
                if (found instanceof AttributeDeclaration) {
                    if (hasThis) {
                        return ((AttributeDeclaration) found).getReference(position);
                    } else {
                        throw new SemanticError(SemanticErrorCode.OnlyForInstance,
                                                position,
                                                name,
                                                currentObject.getName());
                    }
                } else if (found instanceof ObjectDeclaration.ServiceOverload) {
                    if (((ObjectDeclaration.ServiceOverload) found).isInstance()) {
                        if (hasThis) {
                            return ((ObjectDeclaration.ServiceOverload) found).getReference(position,
                                                                                            new ThisLiteral(position,
                                                                                                            currentObject));
                        } else {
                            throw new SemanticError(SemanticErrorCode.OnlyForInstance,
                                                    position,
                                                    name,
                                                    currentObject.getName());
                        }
                    } else {
                        return ((ObjectDeclaration.ServiceOverload) found).getReference(position);
                    }
                } else if (found instanceof EventDeclaration) {
                    return ((EventDeclaration) found).getReference(position);
                }
            }

            if (currentDomain != null) {
                final Name result = currentDomain.getNameLookup().find(name);
                if (result != null) {
                    return result.getReference(Position.getPosition(name));
                }
            }

            final BuiltinType type = BuiltinType.lookupName(Position.getPosition(name), name, false);
            if (type != null) {
                return new TypeNameExpression(Position.getPosition(name), type);
            }

            throw new SemanticError(SemanticErrorCode.NameNotFoundInScope, Position.getPosition(name), name);
        } catch (final SemanticError e) {
            e.report();
            return null;
        }
    }

    public Name(final String name) {
        super(name);
        this.name = name;
    }

    public Name(final Position position, final String name) {
        super(position);
        this.name = name;

    }

    @Override
    public String getName() {
        return name;
    }

    private final String name;

    public abstract Expression getReference(Position position);

}
