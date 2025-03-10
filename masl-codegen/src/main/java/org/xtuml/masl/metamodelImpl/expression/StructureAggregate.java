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
import org.xtuml.masl.metamodelImpl.type.AnonymousStructure;
import org.xtuml.masl.metamodelImpl.type.BasicType;
import org.xtuml.masl.utils.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StructureAggregate extends Expression implements org.xtuml.masl.metamodel.expression.StructureAggregate {

    private final List<Expression> elements;
    private final BasicType type;

    public StructureAggregate(final Position position, final List<Expression> elements) {
        this(position, elements, null);
    }

    public StructureAggregate(final Position position, final List<Expression> elements, final BasicType type) {
        super(position);
        this.elements = new ArrayList<>();
        for (final Expression element : elements) {
            if (element != null) {
                this.elements.add(element);
            }
        }
        if (type == null) {
            final List<BasicType> eltypes = new ArrayList<>(elements.size());
            for (final Expression element : elements) {
                eltypes.add(element.getType());
            }
            this.type = new AnonymousStructure(eltypes);
        } else {
            this.type = type;
        }
    }

    @Override
    protected Expression resolveInner(final BasicType requiredType) {
        if (requiredType.getPrimitiveType().getDefinedType() instanceof AnonymousStructure rhs) {
            if (rhs.getElements().size() == elements.size()) {
                final List<Expression> newElts = new ArrayList<>();

                for (int i = 0; i < elements.size(); ++i) {
                    if (rhs.getElements().get(i).isAssignableFrom(elements.get(i), true)) {
                        newElts.add(elements.get(i).resolve(rhs.getElements().get(i)));
                    } else {
                        return this;
                    }
                }

                return new StructureAggregate(getPosition(), newElts, requiredType);
            }
        }
        return this;
    }

    /**
     * @return
     * @see org.xtuml.masl.metamodelImpl.expression.Expression#getType()
     */
    @Override
    public BasicType getType() {
        return type;
    }

    @Override
    public List<Expression> getElements() {
        return Collections.unmodifiableList(elements);
    }

    @Override
    public String toString() {
        return "(" + TextUtils.formatList(elements, "", ", ", "") + ")";
    }

    @Override
    protected List<Expression> getFindArgumentsInner() {
        final List<Expression> params = new ArrayList<>();
        for (final Expression element : elements) {
            params.addAll(element.getFindArguments());
        }
        return params;

    }

    @Override
    protected List<FindParameterExpression> getFindParametersInner() {
        final List<FindParameterExpression> params = new ArrayList<>();
        for (final Expression element : elements) {
            params.addAll(element.getConcreteFindParameters());
        }
        return params;

    }

    @Override
    public int getFindAttributeCount() {
        int count = 0;
        for (final Expression element : elements) {
            count += element.getFindAttributeCount();
        }
        return count;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof StructureAggregate obj2)) {
            return false;
        } else {

            return elements.equals(obj2.elements);
        }
    }

    @Override
    public int hashCode() {
        return elements.hashCode();
    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitStructureAggregate(this);
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren(elements);
    }

}
