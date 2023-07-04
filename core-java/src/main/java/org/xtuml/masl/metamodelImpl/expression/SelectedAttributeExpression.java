/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 ----------------------------------------------------------------------------
 Classification: UK OFFICIAL
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.metamodelImpl.expression;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodel.type.TypeDefinition.ActualType;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.object.AttributeDeclaration;
import org.xtuml.masl.metamodelImpl.type.BasicType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
        final List<Expression> params = new ArrayList<Expression>();
        params.addAll(prefix.getFindArguments());
        return params;
    }

    @Override
    protected List<FindParameterExpression> getFindParametersInner() {
        final List<FindParameterExpression> params = new ArrayList<FindParameterExpression>();
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
    public <R, P> R accept(final ASTNodeVisitor<R, P> v, final P p) throws Exception {
        return v.visitSelectedAttributeExpression(this, p);
    }

    @Override
    public List<Expression> getChildExpressions() {
        return Collections.<Expression>singletonList(prefix);
    }

}
