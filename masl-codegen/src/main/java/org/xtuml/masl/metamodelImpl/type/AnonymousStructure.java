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

import java.util.Collections;
import java.util.List;

public class AnonymousStructure extends BasicType implements org.xtuml.masl.metamodel.type.AnonymousStructure {

    private final List<BasicType> elements;

    public AnonymousStructure(final List<BasicType> elements) {
        super(null, true);
        this.elements = elements;
    }

    @Override
    public List<BasicType> getElements() {
        return Collections.unmodifiableList(elements);
    }

    /**
     * @return
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof AnonymousStructure rhs)) {
            return false;
        } else {

            return elements.equals(rhs.elements);
        }
    }

    @Override
    protected boolean isAssignableFromRelaxation(final BasicType rhs) {
        if (rhs instanceof AnonymousStructure) {
            if (elements.size() != ((AnonymousStructure) rhs).elements.size()) {
                return false;
            }
            int i = 0;
            for (final BasicType elt : elements) {
                if (!elt.isAssignableFrom(((AnonymousStructure) rhs).elements.get(i++))) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected boolean isConvertibleFromRelaxation(final BasicType rhs) {
        if (rhs instanceof AnonymousStructure) {
            if (elements.size() != ((AnonymousStructure) rhs).elements.size()) {
                return false;
            }
            int i = 0;
            for (final BasicType elt : elements) {
                if (!elt.isConvertibleFrom(((AnonymousStructure) rhs).elements.get(i++), true)) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {

        return "structure\n" +
               org.xtuml.masl.utils.TextUtils.alignTabs(org.xtuml.masl.utils.TextUtils.formatList(elements,
                                                                                                  "",
                                                                                                  "? : ",
                                                                                                  ";\n",
                                                                                                  "",
                                                                                                  "")) +
               "end structure";
    }

    /**
     * @return
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return elements.hashCode();
    }

    @Override
    public AnonymousStructure getPrimitiveType() {
        return this;
    }

    @Override
    public AnonymousStructure getBasicType() {
        return this;
    }

    @Override
    public ActualType getActualType() {
        return null;
    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitAnonymousStructure(this);
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren(elements);
    }

}
