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
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.type.*;

import java.util.List;

public abstract class OrderingExpression extends Expression {

    public static class OrderComponent {

        String getName() {
            return name;
        }

        boolean isReverse() {
            return reverse;
        }

        public OrderComponent(final String name, final boolean reverse) {
            this.name = name;
            this.reverse = reverse;
        }

        private final String name;
        private final boolean reverse;
    }

    public static OrderingExpression create(final Position position,
                                            final Expression expression,
                                            final boolean reverse,
                                            final List<OrderComponent> components) {
        try {
            OrderingExpression result = null;
            final BasicType basicType = expression.getType().getBasicType();
            if (basicType instanceof CollectionType) {
                final TypeDefinition containedType = basicType.getContainedType().getBasicType().getDefinedType();

                if (containedType instanceof InstanceType) {
                    result = new InstanceOrderingExpression(position, expression, reverse, components);
                } else {
                    result = new StructureOrderingExpression(position, expression, reverse, components);
                }
            } else {
                throw new SemanticError(SemanticErrorCode.OrderNotCollection, position);
            }
            return result;
        } catch (final SemanticError e) {
            e.report();
            return null;
        }
    }

    public abstract static class Component {

        Component(final boolean reverse) {
            this.reverse = reverse;
        }

        private final boolean reverse;

        public boolean isReverse() {
            return reverse;
        }
    }

    public OrderingExpression(final Position position, final Expression collection, final boolean reverse) {
        super(position);
        this.collection = collection;
        this.reverse = reverse;

    }

    public Expression getCollection() {
        return collection;
    }

    @Override
    public BasicType getType() {
        return SequenceType.createAnonymous(collection.getType().getBasicType().getContainedType());
    }

    @Override
    public int hashCode() {

        return collection.hashCode() ^ (reverse ? 0 : 1);
    }

    public boolean isReverse() {
        return reverse;
    }

    @Override
    public String toString() {
        return collection + " " + (reverse ? "reverse_" : "") + "ordered_by ";

    }

    private final Expression collection;

    private final boolean reverse;

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren(collection);
    }
}
