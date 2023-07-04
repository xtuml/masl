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
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.type.BasicType;
import org.xtuml.masl.metamodelImpl.type.IntegerType;
import org.xtuml.masl.utils.HashCode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class AnyExpression extends Expression implements org.xtuml.masl.metamodel.expression.AnyExpression {

    AnyExpression(final Position position, final Expression collection, final Expression count) throws SemanticError {
        super(position);

        if (collection.getType().getContainedType() == null) {
            throw new SemanticError(SemanticErrorCode.ExpectedCollectionOrString, position, collection.getType());
        }

        this.collection = collection;
        if (count == null) {
            this.type = collection.getType().getContainedType();
        } else {
            IntegerType.createAnonymous().checkAssignable(count);
            this.type = collection.getType();
        }
        this.count = count;
    }

    AnyExpression(final Position position, final Expression collection) throws SemanticError {
        this(position, collection, null);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj != null) {
            if (this == obj) {
                return true;
            }
            if (obj.getClass() == getClass()) {
                final AnyExpression obj2 = ((AnyExpression) obj);
                return collection.equals(obj2.collection) &&
                       (Objects.equals(count, obj2.count));
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
        return HashCode.makeHash(collection, count);
    }

    @Override
    public int getFindAttributeCount() {
        return collection.getFindAttributeCount() + (count == null ? 0 : count.getFindAttributeCount());
    }

    @Override
    public Expression getFindSkeletonInner() {
        try {
            return new AnyExpression(getPosition(), collection.getFindSkeleton(), count.getFindSkeleton());
        } catch (final SemanticError e) {
            e.printStackTrace();
            assert false;
            return null;
        }

    }

    @Override
    protected List<Expression> getFindArgumentsInner() {
        final List<Expression> params = new ArrayList<Expression>();
        params.addAll(collection.getFindArguments());
        if (count != null) {
            params.addAll(count.getFindArguments());
        }
        return params;

    }

    @Override
    protected List<FindParameterExpression> getFindParametersInner() {
        final List<FindParameterExpression> params = new ArrayList<FindParameterExpression>();
        params.addAll(collection.getConcreteFindParameters());
        if (count != null) {
            params.addAll(count.getConcreteFindParameters());
        }
        return params;
    }

    private final Expression count;
    private final Expression collection;
    private final BasicType type;

    @Override
    public Expression getCollection() {
        return collection;
    }

    @Override
    public Expression getCount() {
        return count;
    }

    @Override
    public String toString() {
        return collection + "'any" + (count == null ? "" : "(" + count + ")");
    }

    @Override
    public <R, P> R accept(final ASTNodeVisitor<R, P> v, final P p) throws Exception {
        return v.visitAnyExpression(this, p);
    }

    @Override
    public List<Expression> getChildExpressions() {
        return Arrays.asList(collection, count);
    }

}
