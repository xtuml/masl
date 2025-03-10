/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.metamodelImpl.expression;

import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.name.Name;
import org.xtuml.masl.metamodelImpl.object.AttributeDeclaration;
import org.xtuml.masl.metamodelImpl.object.ObjectDeclaration;
import org.xtuml.masl.metamodelImpl.statemodel.EventDeclaration;
import org.xtuml.masl.metamodelImpl.type.*;

public final class DotExpression {

    public static Expression create(final Position position, final Expression lhs, final String name) {
        if (lhs == null) {
            return null;
        }

        try {

            final TypeDefinition type = lhs.getType().getBasicType().getDefinedType();
            if (type instanceof StructureType struct) {
                final StructureElement element = struct.getElement(name);
                return new SelectedComponentExpression(position, lhs, element);
            } else if (type instanceof InstanceType instance) {
                final Name found = instance.getObjectDeclaration().getNameLookup().get(name);
                if (found instanceof AttributeDeclaration) {
                    return ((AttributeDeclaration) found).getReference(position, lhs);
                } else if (found instanceof ObjectDeclaration.ServiceOverload &&
                           ((ObjectDeclaration.ServiceOverload) found).isInstance()) {
                    return ((ObjectDeclaration.ServiceOverload) found).getReference(position, lhs);
                } else {
                    new SemanticError(SemanticErrorCode.OnlyForObject,
                                      Position.getPosition(name),
                                      name,
                                      instance.getObjectDeclaration().getName()).report();
                    return create(position,
                                  new ObjectNameExpression(lhs.getPosition(), instance.getObjectDeclaration()),
                                  name);
                }
            } else if (lhs instanceof TypeNameExpression) {
                final BasicType refType = ((TypeNameExpression) lhs).getReferencedType();
                final TypeDefinition definedType = refType.getBasicType().getDefinedType();
                if (definedType instanceof EnumerateType) {
                    return ((EnumerateType) definedType).getItem(name).getReference(position);
                } else {
                    throw new SemanticError(SemanticErrorCode.NotEnumerate, Position.getPosition(name), refType);
                }
            } else if (lhs instanceof ObjectNameExpression objName) {
                final Name found = objName.getObject().getNameLookup().get(name);
                if (found instanceof ObjectDeclaration.ServiceOverload &&
                    ((ObjectDeclaration.ServiceOverload) found).isObject()) {
                    return ((ObjectDeclaration.ServiceOverload) found).getReference(position);
                } else if (found instanceof EventDeclaration) {
                    return ((EventDeclaration) found).getReference(position);
                } else {
                    throw new SemanticError(SemanticErrorCode.OnlyForInstance,
                                            Position.getPosition(name),
                                            name,
                                            objName.getObject().getName());
                }
            } else {
                throw new SemanticError(SemanticErrorCode.DotNotValid, position, type);
            }

        } catch (final SemanticError e) {
            e.report();
            return null;
        }
    }

}
