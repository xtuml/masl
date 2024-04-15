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
package org.xtuml.masl.metamodelImpl.type;

import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.domain.Domain;
import org.xtuml.masl.metamodelImpl.error.NotFound;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.expression.Expression;

public abstract class BasicType extends TypeDefinition implements org.xtuml.masl.metamodel.type.BasicType {

    public static BasicType createNamedType(final Domain.Reference domainRef,
                                            final boolean allowBuiltin,
                                            final String name,
                                            final boolean anonymous) {
        if (domainRef == null || name == null) {
            return null;
        }

        try {
            if (allowBuiltin) {
                final TypeDeclaration type = domainRef.getDomain().findType(name);
                if (type == null) {
                    return BuiltinType.create(Position.getPosition(name), name, anonymous);
                } else {
                    if (anonymous) {
                        new SemanticError(SemanticErrorCode.AnonymousUserDefinedType,
                                          Position.getPosition(name)).report();
                    }
                    return UserDefinedType.create(domainRef, name);
                }
            } else {
                if (anonymous) {
                    new SemanticError(SemanticErrorCode.AnonymousUserDefinedType, Position.getPosition(name)).report();
                }
                return UserDefinedType.create(domainRef, name);
            }
        } catch (final NotFound e) {
            e.report();
            return null;
        }

    }

    BasicType(final Position position, final boolean anonymous) {
        super(position);
        this.anonymous = anonymous;
    }

    public final boolean isAnonymousType() {
        return anonymous;
    }

    private final boolean anonymous;

    public BasicType getContainedType() {
        return null;
    }

    public void checkAssignable(final Expression rhs) throws SemanticError {
        if (!isAssignableFrom(rhs)) {
            throw new SemanticError(SemanticErrorCode.NotAssignable,
                                    rhs.getPosition(),
                                    rhs.getType().toString(),
                                    this.toString());
        }
    }

    public void checkAssignable(final BasicType rhs) throws SemanticError {
        if (!isAssignableFrom(rhs)) {
            throw new SemanticError(SemanticErrorCode.NotAssignable,
                                    rhs.getPosition(),
                                    rhs.toString(),
                                    this.toString());
        }
    }

    public final boolean isAssignableFrom(final Expression rhs) {
        return isAssignableFrom(rhs, false);
    }

    public final boolean isAssignableFrom(final Expression rhs, final boolean allowSeqPromote) {
        return isAssignableFrom(rhs,allowSeqPromote,true);
    }

    public final boolean isAssignableFrom(final Expression rhs, final boolean allowSeqPromote, boolean allowRelaxed) {
        return isAssignableFrom(rhs.resolve(this, allowSeqPromote).getType(),allowRelaxed);
    }

    public final boolean isAssignableFrom(final BasicType rhs, boolean allowRelaxed) {
        if (isAnonymousType() || rhs.isAnonymousType()) {
            return getPrimitiveType().isAssignableFromInner(rhs.getPrimitiveType(), allowRelaxed);
        } else {
            return isAssignableFromInner(rhs,allowRelaxed);
        }
    }

    public final boolean isAssignableFrom(final BasicType rhs) {
        return isAssignableFrom(rhs,true);
    }

    private final boolean isAssignableFromInner(final BasicType rhs, boolean allowRelaxed) {
        return this.equals(rhs) || (allowRelaxed && isAssignableFromRelaxation(rhs));
    }

    protected boolean isAssignableFromRelaxation(final BasicType rhs) {
        return false;
    }

    public final boolean isConvertibleFrom(final BasicType rhs) {
        return isConvertibleFrom(rhs, true);
    }

    protected final boolean isConvertibleFrom(final BasicType rhs, final boolean allowSeqPromote) {
        return getPrimitiveType().isConvertibleFromInner(rhs.getPrimitiveType(), allowSeqPromote);
    }

    private final boolean isConvertibleFromInner(final BasicType rhs, final boolean allowSeqPromote) {
        return this.equals(rhs) ||
               isConvertibleFromRelaxation(rhs)
               // Allow promotion to sequence
               ||
               (allowSeqPromote &&
                getContainedType() != null &&
                getContainedType().isConvertibleFrom(rhs, allowSeqPromote))
               // Allow conversion of single value to single component structures
               ||
               (getPrimitiveType() instanceof AnonymousStructure &&
                ((AnonymousStructure) getPrimitiveType()).getElements().size() == 1 &&
                ((AnonymousStructure) getPrimitiveType()).getElements().get(0).isConvertibleFrom(rhs, allowSeqPromote));
    }

    protected boolean isConvertibleFromRelaxation(final BasicType rhs) {
        return false;
    }

    @Override
    abstract public BasicType getBasicType();

    public BasicType getBaseType() {
        return this;
    }

}
