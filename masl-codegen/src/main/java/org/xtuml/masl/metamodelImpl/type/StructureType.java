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
import org.xtuml.masl.metamodelImpl.common.CheckedLookup;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StructureType extends FullTypeDefinition implements org.xtuml.masl.metamodel.type.StructureType {

    public static StructureType create(final Position position, final List<StructureElement> elements) {
        if (elements == null) {
            return null;
        }

        try {
            return new StructureType(position, elements);
        } catch (final SemanticError e) {
            e.report();
            return null;
        }
    }

    private final CheckedLookup<StructureElement> elements;

    private StructureType(final Position position, final List<StructureElement> elements) throws SemanticError {
        super(position);
        this.elements =
                new CheckedLookup<>(SemanticErrorCode.ElementAlreadyDefinedOnStructure,
                                    SemanticErrorCode.ElementNotFoundOnStructure,
                                    () -> getUserDefinedType().getName());

        final List<BasicType> elTypes = new ArrayList<>();
        for (final StructureElement element : elements) {
            if (element != null) {
                this.elements.put(element.getName(), element);
                elTypes.add(element.getType());
            }
        }
        anonymousStruct = new AnonymousStructure(elTypes);
    }

    public StructureElement getElement(final String name) throws SemanticError {
        return elements.get(name);
    }

    @Override
    public List<StructureElement> getElements() {
        return Collections.unmodifiableList(elements.asList());
    }

    @Override
    public String toString() {
        return "structure\n" +
               org.xtuml.masl.utils.TextUtils.alignTabs(org.xtuml.masl.utils.TextUtils.formatList(elements.asList(),
                                                                                                  "",
                                                                                                  "  ",
                                                                                                  "\n",
                                                                                                  "",
                                                                                                  "")) +
               "end structure";
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof StructureType rhs)) {
            return false;
        } else {

            return elements.equals(rhs.elements);
        }
    }

    @Override
    public void setTypeDeclaration(final TypeDeclaration typeDeclaration) {
        super.setTypeDeclaration(typeDeclaration);
        for (final StructureElement element : elements) {
            if (element.getType().getTypeDeclaration() == typeDeclaration) {
                new SemanticError(SemanticErrorCode.RecursiveStructure,
                                  element.getPosition(),
                                  typeDeclaration.getName(),
                                  element.getName()).report();
            }
        }
    }

    @Override
    public UserDefinedType getUserDefinedType() {
        return getTypeDeclaration().getDeclaredType();
    }

    @Override
    public int hashCode() {
        return elements.hashCode();
    }

    @Override
    public AnonymousStructure getPrimitiveType() {
        return anonymousStruct;
    }

    @Override
    public ActualType getActualType() {
        return ActualType.STRUCTURE;
    }

    @Override
    public void checkCanBePublic() {
        for (final StructureElement element : elements) {
            element.getType().checkCanBePublic();
        }
    }

    private final AnonymousStructure anonymousStruct;

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitStructureType(this);
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren(elements);
    }

}
