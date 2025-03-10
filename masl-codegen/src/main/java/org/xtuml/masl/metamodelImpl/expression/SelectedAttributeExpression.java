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
import org.xtuml.masl.metamodel.type.TypeDefinition.ActualType;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.object.AttributeDeclaration;
import org.xtuml.masl.metamodelImpl.type.BasicType;

import java.util.ArrayList;
import java.util.List;

public class SelectedAttributeExpression extends Expression
        implements org.xtuml.masl.metamodel.expression.SelectedAttributeExpression {

    private final Expression prefix;
    private final AttributeDeclaration attribute;

    public SelectedAttributeExpression(final Position position,
                                       final Expression prefix,
                                       final AttributeDeclaration attribute) {
        super(position);
        this.prefix = prefix;
        this.attribute = attribute;
    }

    @Override
    public Expression getPrefix() {
        return prefix;
    }

    @Override
    public AttributeDeclaration getAttribute() {
        return attribute;
    }

    @Override
    public String toString() {
        return prefix + "." + attribute.getName();
    }

    @Override
    public BasicType getType() {
        return attribute.getType();
    }

    @Override
    protected List<Expression> getFindArgumentsInner() {
        final List<Expression> params = new ArrayList<>();
        params.addAll(prefix.getFindArguments());
        return params;
    }

    @Override
    protected List<FindParameterExpression> getFindParametersInner() {
        final List<FindParameterExpression> params = new ArrayList<>();
        params.addAll(prefix.getConcreteFindParameters());
        return params;
    }

    @Override
    public int getFindAttributeCount() {
        return prefix.getFindAttributeCount();
    }

    @Override
    public Expression getFindSkeletonInner() {
        final Expression nameSkel = prefix.getFindSkeleton();

        return new SelectedAttributeExpression(getPosition(), nameSkel, attribute);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SelectedAttributeExpression obj2)) {
            return false;
        } else {

            return prefix.equals(obj2.prefix) && attribute.equals(obj2.attribute);
        }
    }

    @Override
    public int hashCode() {

        return prefix.hashCode() ^ attribute.hashCode();
    }

    @Override
    public void checkWriteableInner(final Position position) throws SemanticError {
        if (attribute.isIdentifier()) {
            throw new SemanticError(SemanticErrorCode.AssignToIdentifier, position, attribute.getName());
        }
        if (attribute.isReferential()) {
            throw new SemanticError(SemanticErrorCode.AssignToReferential, position, attribute.getName());
        }
        if (attribute.isUnique()) {
            throw new SemanticError(SemanticErrorCode.AssignToUnique, position, attribute.getName());
        }
        if (attribute.getType().getBasicType().getActualType() == ActualType.TIMER) {
            throw new SemanticError(SemanticErrorCode.CannotWriteToAttributeType,
                                    position,
                                    attribute.getName(),
                                    getType().toString());
        }
    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitSelectedAttributeExpression(this);
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren(prefix);
    }

}
