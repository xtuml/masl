/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.metamodelImpl.object;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.common.PragmaList;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IdentifierDeclaration implements org.xtuml.masl.metamodel.object.IdentifierDeclaration {

    private final List<AttributeDeclaration> attributes;
    private final List<String> attributeNames = new ArrayList<>();
    private final PragmaList pragmas;
    private final boolean preferred;

    public static void create(final Position position,
                              final ObjectDeclaration object,
                              final List<String> attributes,
                              final PragmaList pragmas) {
        if (object == null) {
            return;
        }

        try {
            object.addIdentifier(new IdentifierDeclaration(position, object, attributes, pragmas));
        } catch (final SemanticError e) {
            e.report();
        }
    }

    static IdentifierDeclaration createPreferred() {
        return new IdentifierDeclaration(true, new PragmaList());
    }

    private IdentifierDeclaration(final boolean preferred, final PragmaList pragmas) {
        this.preferred = preferred;
        this.pragmas = pragmas;
        this.attributes = new ArrayList<>();
    }

    private IdentifierDeclaration(final Position position,
                                  final ObjectDeclaration object,
                                  final List<String> attributes,
                                  final PragmaList pragmas) throws SemanticError {
        this(false, pragmas);
        for (final String att : attributes) {
            try {
                addAttribute(object.getAttribute(att));
            } catch (final SemanticError e) {
                e.report();
            }
        }
        if (this.attributes.isEmpty()) {
            throw new SemanticError(SemanticErrorCode.EmptyIdentifier, position);
        }
    }

    public void addAttribute(final AttributeDeclaration attribute) {
        if (attribute != null) {
            attributes.add(attribute);
            attributeNames.add(attribute.getName());
            attribute.setIdentifier();
        }
    }

    public PragmaList getPragmas() {
        return pragmas;
    }

    @Override
    public List<AttributeDeclaration> getAttributes() {
        return Collections.unmodifiableList(attributes);
    }

    @Override
    public boolean isPreferred() {
        return preferred;
    }

    private ObjectDeclaration parentObject;

    public ObjectDeclaration getParentObject() {
        return parentObject;
    }

    public void setParentObject(final ObjectDeclaration parentObject) {
        this.parentObject = parentObject;
    }

    @Override
    public String toString() {
        return "identifier " + org.xtuml.masl.utils.TextUtils.formatList(attributeNames, "", ", ", "") + pragmas;
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((attributes == null) ? 0 : attributes.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final IdentifierDeclaration other = (IdentifierDeclaration) obj;
        if (attributes == null) {
            return other.attributes == null;
        } else {
            return attributes.equals(other.attributes);
        }
    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitIdentifierDeclaration(this);
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren();
    }

}
