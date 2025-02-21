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
import org.xtuml.masl.metamodelImpl.type.BasicType;
import org.xtuml.masl.metamodelImpl.type.SequenceType;

import java.util.ArrayList;
import java.util.List;

public class ElementsExpression extends Expression implements org.xtuml.masl.metamodel.expression.ElementsExpression {

    ElementsExpression(final Position position, final Expression collection) throws SemanticError {
        super(position);

        if (collection.getType().getContainedType() == null) {
            throw new SemanticError(SemanticErrorCode.ExpectedCollectionOrString, position, collection.getType());
        }

        this.collection = collection;
        this.type = SequenceType.createAnonymous(collection.getType().getContainedType());
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj != null) {
            if (this == obj) {
                return true;
            }
            if (obj.getClass() == getClass()) {
                final ElementsExpression obj2 = ((ElementsExpression) obj);
                return collection.equals(obj2.collection);
            } else {
                return false;
            }
        }
        return false;
    }

    @Override
    public BasicType getType() {
        return type;
    }

    @Override
    public int hashCode() {
        return collection.hashCode();
    }

    @Override
    public int getFindAttributeCount() {
        return collection.getFindAttributeCount();
    }

    @Override
    public Expression getFindSkeletonInner() {
        try {
            return new ElementsExpression(getPosition(), collection.getFindSkeleton());
        } catch (final SemanticError e) {
            e.printStackTrace();
            assert false;
            return null;
        }

    }

    @Override
    protected List<Expression> getFindArgumentsInner() {
        final List<Expression> params = new ArrayList<>();
        params.addAll(collection.getFindArguments());
        return params;

    }

    @Override
    protected List<FindParameterExpression> getFindParametersInner() {
        final List<FindParameterExpression> params = new ArrayList<>();
        params.addAll(collection.getConcreteFindParameters());
        return params;
    }

    private final Expression collection;
    private final SequenceType type;

    @Override
    public Expression getCollection() {
        return collection;
    }

    @Override
    public String toString() {
        return collection + "'elements";
    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitElementsExpression(this);
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren(collection);
    }

}
