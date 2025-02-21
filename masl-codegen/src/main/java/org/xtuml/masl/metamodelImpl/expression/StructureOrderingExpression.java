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

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.type.*;
import org.xtuml.masl.utils.HashCode;
import org.xtuml.masl.utils.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StructureOrderingExpression extends OrderingExpression
        implements org.xtuml.masl.metamodel.expression.StructureOrderingExpression {

    public static class Component extends OrderingExpression.Component
            implements org.xtuml.masl.metamodel.expression.StructureOrderingExpression.Component {

        Component(final boolean reverse, final StructureElement element) {
            super(reverse);
            this.element = element;
        }

        @Override
        public StructureElement getElement() {
            return element;
        }

        private final StructureElement element;

        @Override
        public int hashCode() {
            return HashCode.makeHash(isReverse(), element);
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof Component obj2)) {
                return false;
            } else {

                return isReverse() == obj2.isReverse() && element == obj2.element;
            }
        }

        @Override
        public String toString() {
            return (isReverse() ? "reverse " : " ") + element.toString();
        }

        @Override
        public void accept(final ASTNodeVisitor v) {
            v.visitStructureOrderingComponent(this);
        }

        @Override
        public List<ASTNode> children() {
            return ASTNode.makeChildren();
        }

    }

    public StructureOrderingExpression(final Position position,
                                       final Expression collection,
                                       final boolean reverse,
                                       final List<OrderComponent> components) {
        super(position, collection, reverse);
        this.order = new ArrayList<>();
        for (final OrderComponent component : components) {
            try {
                addComponent(component);
            } catch (final SemanticError e) {
                e.report();
            }
        }

    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof StructureOrderingExpression obj2)) {
            return false;
        } else {

            return getCollection().equals(obj2.getCollection()) &&
                   isReverse() == obj2.isReverse() &&
                   order.equals(obj2.order);
        }
    }

    @Override
    public List<StructureOrderingExpression.Component> getOrder() {
        return Collections.unmodifiableList(order);
    }

    @Override
    public int hashCode() {

        return super.hashCode() ^ order.hashCode();
    }

    @Override
    public String toString() {
        return super.toString() + " (" + TextUtils.formatList(order, "", ",", "") + ")";

    }

    public void addComponent(final OrderComponent component) throws SemanticError {
        final BasicType basicType = getCollection().getType().getBasicType();

        assert basicType instanceof CollectionType;

        final TypeDefinition containedType = basicType.getContainedType().getBasicType().getDefinedType();

        if (containedType instanceof StructureType contained) {
            final StructureElement elt = contained.getElement(component.getName());
            order.add(new Component(component.isReverse(), elt));
        } else {
            throw new SemanticError(SemanticErrorCode.ElementNotFoundOnType,
                                    Position.getPosition(component.getName()),
                                    component.getName(),
                                    getCollection().getType());
        }
    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitStructureOrderingExpression(this);
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren(super.children(), order);
    }

    private final List<Component> order;
}
