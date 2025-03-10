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
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.expression.EnumerateLiteral;
import org.xtuml.masl.metamodelImpl.expression.Expression;
import org.xtuml.masl.metamodelImpl.name.Name;

import java.util.List;

public class EnumerateItem extends Name implements org.xtuml.masl.metamodel.type.EnumerateItem {

    public static class AmbiguousEnumItem extends Name {

        public AmbiguousEnumItem(final String name, final List<EnumerateItem> items) {
            super(name);
            this.items = items;
        }

        private final List<EnumerateItem> items;

        @Override
        public EnumerateLiteral.AmbiguousEnumerateLiteral getReference(final Position position) {
            return new EnumerateLiteral.AmbiguousEnumerateLiteral(position, items);
        }
    }

    private final Expression value;
    private EnumerateType enumerate;
    private int index;
    private String comment;

    public EnumerateItem(final String name, final Expression value) {
        super(name);
        this.value = value;
    }

    public EnumerateItem(final String name) {
        this(name, null);
    }

    @Override
    public EnumerateType getEnumerate() {
        return enumerate;
    }

    @Override
    public Expression getValue() {
        return value;
    }

    @Override
    public EnumerateLiteral getReference(final Position position) {
        return new EnumerateLiteral(position, this);
    }

    public int getIndex() {
        return index;
    }

    @Override
    public String toString() {
        return getName() + (value == null ? "" : " = " + value);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof EnumerateItem rhs)) {
            return false;
        } else {

            return getName().equals(rhs.getName()) &&
                   ((value == null && rhs.value == null) || (value != null && value.equals(rhs.value)));
        }
    }

    @Override
    public int hashCode() {

        return getName().hashCode() * 31 + (value == null ? 0 : value.hashCode());
    }

    public void setEnumerate(final EnumerateType enumerate) {
        this.enumerate = enumerate;
    }

    public void setIndex(final int i) {
        index = i;
    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitEnumerateItem(this);
    }

    @Override
    public String getComment() {
        return comment;
    }

    public void setComment(final String comment) {
        this.comment = comment;
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren(value);
    }

}
