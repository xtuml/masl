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
import org.xtuml.masl.metamodelImpl.expression.Expression;
import org.xtuml.masl.metamodelImpl.expression.SelectedAttributeExpression;
import org.xtuml.masl.metamodelImpl.expression.ThisLiteral;
import org.xtuml.masl.metamodelImpl.name.Name;
import org.xtuml.masl.metamodelImpl.type.BasicType;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class AttributeDeclaration extends Name implements org.xtuml.masl.metamodel.object.AttributeDeclaration {

    private final BasicType type;
    private final boolean isPreferredIdentifier;
    private boolean isIdentifier;
    private final boolean isUnique;
    private final List<ReferentialAttributeDefinition> refAttDefs;
    private final PragmaList pragmas;
    private final Expression defaultValue;

    public static AttributeDeclaration create(final ObjectDeclaration object,
                                              final String name,
                                              final BasicType type,
                                              final boolean isPreferredIdentifier,
                                              final boolean isUnique,
                                              final List<ReferentialAttributeDefinition> refAttDefs,
                                              final Expression defaultValue,
                                              final PragmaList pragmas) {
        if (object == null || name == null || type == null || refAttDefs == null || pragmas == null) {
            return null;
        }

        try {
            final AttributeDeclaration
                    att =
                    new AttributeDeclaration(object,
                                             name,
                                             type,
                                             isPreferredIdentifier,
                                             isUnique,
                                             refAttDefs,
                                             defaultValue,
                                             pragmas);
            object.addAttribute(att);
            return att;
        } catch (final SemanticError e) {
            e.report();
            return null;
        }
    }

    private AttributeDeclaration(final ObjectDeclaration object,
                                 final String name,
                                 final BasicType type,
                                 final boolean isPreferredIdentifier,
                                 final boolean isUnique,
                                 final List<ReferentialAttributeDefinition> refAttDefs,
                                 Expression defaultValue,
                                 final PragmaList pragmas) {
        super(name);
        this.parentObject = object;
        this.pragmas = pragmas;
        this.type = type;
        this.isPreferredIdentifier = isPreferredIdentifier;
        this.isIdentifier = isPreferredIdentifier;
        this.isUnique = isUnique;
        this.refAttDefs = refAttDefs;
        if (defaultValue != null) {
            try {
                type.checkAssignable(defaultValue);
            } catch (final SemanticError e) {
                e.report();
                defaultValue = null;
            }
        }

        this.defaultValue = defaultValue;

        for (final ReferentialAttributeDefinition refAtt : refAttDefs) {
            refAtt.setParentAttribute(this);
        }
    }

    @Override
    public Expression getDefault() {
        return defaultValue;
    }

    void linkReferentialAttributes() {
        // Can't do this on construction, as related objects may not have been fully
        // defined.
        for (final Iterator<ReferentialAttributeDefinition> it = refAttDefs.iterator(); it.hasNext(); ) {
            final ReferentialAttributeDefinition refAtt = it.next();
            try {
                refAtt.linkDestination();
            } catch (final SemanticError e) {
                // Error in formalising attribute, so remove from list of referentials
                it.remove();
                e.report();
            }
        }
    }

    @Override
    public PragmaList getPragmas() {
        return pragmas;
    }

    @Override
    public BasicType getType() {
        return type;
    }

    @Override
    public boolean isPreferredIdentifier() {
        return isPreferredIdentifier;
    }

    @Override
    public boolean isIdentifier() {
        return isIdentifier;
    }

    public void setIdentifier() {
        isIdentifier = true;
    }

    @Override
    public boolean isUnique() {
        return isUnique;
    }

    @Override
    public List<ReferentialAttributeDefinition> getRefAttDefs() {
        return Collections.unmodifiableList(refAttDefs);
    }

    @Override
    public boolean isReferential() {
        return refAttDefs.size() > 0;
    }

    private final ObjectDeclaration parentObject;

    @Override
    public ObjectDeclaration getParentObject() {
        return parentObject;
    }

    @Override
    public String toString() {
        return getName() +
               "\t: " +
               (isPreferredIdentifier ? "preferred " : "") +
               org.xtuml.masl.utils.TextUtils.formatList(refAttDefs, "referential (", ", ", ") ") +
               type +
               ";\n" +
               pragmas;
    }

    @Override
    public SelectedAttributeExpression getReference(final Position position) {
        return new SelectedAttributeExpression(position, new ThisLiteral(position, parentObject), this);
    }

    public SelectedAttributeExpression getReference(final Position position, final Expression instance) {
        return new SelectedAttributeExpression(position, instance, this);
    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitAttributeDeclaration(this);
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren(defaultValue, pragmas);
    }

}
