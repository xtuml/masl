/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.metamodelImpl.type;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodel.common.Visibility;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.domain.Domain;
import org.xtuml.masl.metamodelImpl.error.NotFound;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.expression.Expression;

import java.util.List;

public class UserDefinedType extends BasicType implements org.xtuml.masl.metamodel.type.UserDefinedType {

    public static UserDefinedType create(final Domain.Reference domainRef, final String name) {
        if (domainRef == null || name == null) {
            return null;
        }

        try {
            final Position
                    position =
                    domainRef.getPosition() == null ? Position.getPosition(name) : domainRef.getPosition();

            return new UserDefinedType(position, domainRef.getDomain().getType(name));
        } catch (final NotFound e) {
            e.report();
            return null;
        }

    }

    public static UserDefinedType createNamed(final TypeDeclaration type) {
        return new UserDefinedType(null, type);
    }

    public static UserDefinedType createAnonymous(final TypeDeclaration type) {
        return new UserDefinedType(null, type, true);
    }

    private final TypeDeclaration type;

    private UserDefinedType(final Position position, final TypeDeclaration type, final boolean anonymous) {
        super(position, anonymous);
        this.type = type;
    }

    private UserDefinedType(final Position position, final TypeDeclaration type) {
        this(position, type, false);
    }

    @Override
    public Domain getDomain() {
        return type.getDomain();
    }

    @Override
    public String getName() {
        return type.getName();
    }

    @Override
    public String toString() {
        return (isAnonymousType() ? "anonymous " : "") + type.getDomain().getName() + "::" + type.getName();
    }

    @Override
    public TypeDefinition getDefinedType() {
        return getTypeDeclaration().getTypeDefinition();
    }

    @Override
    public TypeDeclaration getTypeDeclaration() {
        return type;
    }

    @Override
    public BasicType getPrimitiveType() {
        return getDefinedType().getPrimitiveType();
    }

    @Override
    public BasicType getBasicType() {
        if (getDefinedType() instanceof BasicType) {
            return ((BasicType) getDefinedType()).getBasicType();
        } else if (getDefinedType() instanceof ConstrainedType) {
            return ((ConstrainedType) getDefinedType()).getFullType().getBasicType();
        } else {
            return this;
        }
    }

    @Override
    public BasicType getBaseType() {
        if (getDefinedType() instanceof BasicType) {
            return ((BasicType) getDefinedType()).getBaseType();
        } else if (getDefinedType() instanceof ConstrainedType) {
            return ((ConstrainedType) getDefinedType()).getFullType().getBaseType();
        } else {
            return this;
        }
    }

    @Override
    public BasicType getContainedType() {
        if (getDefinedType() instanceof BasicType) {
            return ((BasicType) getDefinedType()).getContainedType();
        } else if (getDefinedType() instanceof ConstrainedType) {
            return ((ConstrainedType) getDefinedType()).getFullType().getContainedType();
        } else {
            return null;
        }
    }

    @Override
    public Expression getMinValue() {
        return getDefinedType().getMinValue();
    }

    @Override
    public Expression getMaxValue() {
        return getDefinedType().getMaxValue();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof UserDefinedType rhs)) {
            return false;
        } else {

            return type.equals(rhs.type);
        }
    }

    @Override
    public int hashCode() {
        return type.hashCode();
    }

    @Override
    public ActualType getActualType() {
        return ActualType.USER_DEFINED;
    }

    @Override
    protected boolean isConvertibleFromRelaxation(final BasicType rhs) {
        return getDefinedType() instanceof EnumerateType && rhs instanceof IntegerType;
    }

    @Override
    public void checkCanBePublic() {
        if (type.getVisibility() == Visibility.PRIVATE) {
            new SemanticError(SemanticErrorCode.PrivateTypeCannotBeUsedPublicly, getPosition(), toString()).report();
        }
    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitUserDefinedType(this);
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren();
    }

}
