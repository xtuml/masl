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

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.common.PragmaList;
import org.xtuml.masl.metamodelImpl.common.Visibility;
import org.xtuml.masl.metamodelImpl.domain.Domain;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.expression.TypeNameExpression;
import org.xtuml.masl.metamodelImpl.name.Name;
import org.xtuml.masl.utils.TextUtils;

public final class TypeDeclaration extends Name implements org.xtuml.masl.metamodel.type.TypeDeclaration {

    private final Visibility visibility;
    private TypeDefinition typeDefinition;
    private final PragmaList pragmas;

    public static TypeDeclaration createForwardDeclaration(final Position position,
                                                           final Domain domain,
                                                           final String name,
                                                           final Visibility visibility,
                                                           final PragmaList pragmas) {
        if (domain == null || name == null || visibility == null) {
            return null;
        }

        try {
            final TypeDeclaration type = new TypeDeclaration(position, domain, name, visibility, null, pragmas);
            domain.addTypeForwardDeclaration(type);
            return type;
        } catch (final SemanticError e) {
            e.report();
            return null;
        }
    }

    public static TypeDeclaration create(final Position position,
                                         final Domain domain,
                                         final String name,
                                         final Visibility visibility,
                                         final TypeDefinition typeDefinition,
                                         final PragmaList pragmas) {
        if (domain == null || name == null || visibility == null) {
            return null;
        }

        try {
            final TypeDeclaration
                    type =
                    new TypeDeclaration(position, domain, name, visibility, typeDefinition, pragmas);
            domain.addType(type);
            return type;
        } catch (final SemanticError e) {
            e.report();
            return null;
        }
    }

    public static TypeDeclaration getOrCreate(final Position position,
                                              final Domain domain,
                                              final String name,
                                              final Visibility visibility,
                                              final TypeDefinition typeDefinition,
                                              final PragmaList pragmas) {
        if (domain == null || name == null || visibility == null) {
            return null;
        }

        try {
            TypeDeclaration type = domain.findType(name);
            if (type == null) {
                type = new TypeDeclaration(position, domain, name, visibility, typeDefinition, pragmas);
                domain.addType(type);
            } else {
                type.getPragmas().addPragmas(pragmas.getPragmas());
                if (type.getVisibility() != visibility.getVisibility()) {
                    throw new SemanticError(SemanticErrorCode.TypeVisibility, position, name);
                }
            }
            return type;
        } catch (final SemanticError e) {
            e.report();
            return null;
        }
    }

    public TypeDeclaration(final Position position,
                           final Domain domain,
                           final String name,
                           final Visibility visibility,
                           final TypeDefinition typeDefinition,
                           final PragmaList pragmas) {
        super(position, name);

        this.domain = domain;
        this.pragmas = pragmas;
        this.visibility = visibility;
        this.userDefinedType = UserDefinedType.createNamed(this);
        setTypeDefinition(typeDefinition);
    }

    private final UserDefinedType userDefinedType;

    @Override
    public PragmaList getPragmas() {
        return pragmas;
    }

    @Override
    public UserDefinedType getDeclaredType() {
        return userDefinedType;
    }

    public UserDefinedType getAnonymousType() {
        return UserDefinedType.createAnonymous(this);
    }

    @Override
    public org.xtuml.masl.metamodel.common.Visibility getVisibility() {
        return visibility.getVisibility();
    }

    @Override
    public TypeDefinition getTypeDefinition() {
        return typeDefinition;
    }

    public void setTypeDefinition(final TypeDefinition definition) {
        typeDefinition = definition;
        if (typeDefinition == null) {
            return;
        }

        typeDefinition.setTypeDeclaration(this);
        if (visibility == Visibility.PUBLIC) {
            typeDefinition.checkCanBePublic();
        }
    }

    private final Domain domain;

    @Override
    public Domain getDomain() {
        return domain;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof TypeDeclaration rhs)) {
            return false;
        } else {

            return getName().equals(rhs.getName()) && domain.equals(rhs.domain);
        }
    }

    @Override
    public int hashCode() {
        return getName().hashCode() * 31 + domain.hashCode();
    }

    @Override
    public String toString() {
        return (comment == null ? "" : TextUtils.textBlock("", null, "// ", comment, "", true)) +
               visibility +
               (visibility.toString().equals("") ? "" : " ") +
               "type\t" +
               getName() +
               "\tis " +
               typeDefinition +
               ";\n" +
               pragmas;
    }

    @Override
    public TypeNameExpression getReference(final Position position) {
        return new TypeNameExpression(position, this.getDeclaredType());
    }

    public void setComment(final String comment) {
        this.comment = comment;
    }

    @Override
    public String getComment() {
        return comment;
    }

    private String comment;

    @Override
    public <R, P> R accept(final ASTNodeVisitor<R, P> v, final P p) throws Exception {
        return v.visitTypeDeclaration(this, p);
    }
}
