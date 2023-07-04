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
import org.xtuml.masl.metamodelImpl.type.ArrayType;
import org.xtuml.masl.metamodelImpl.type.BasicType;
import org.xtuml.masl.metamodelImpl.type.IntegerType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IndexedNameExpression extends Expression
        implements org.xtuml.masl.metamodel.expression.IndexedNameExpression {

    private final Expression prefix;
    private final Expression index;

    public IndexedNameExpression(final Position position, final Expression prefix, final Expression index) {
        super(position);
        this.prefix = prefix;
        this.index = index;
    }

    @Override
    public Expression getPrefix() {
        return prefix;
    }

    @Override
    public Expression getIndex() {
        return index;
    }

    @Override
    public String toString() {
        return prefix + "[" + index + "]";
    }

    @Override
    public BasicType getType() {
        return prefix.getType().getContainedType();
    }

    @Override
    protected List<Expression> getFindArgumentsInner() {
        final List<Expression> params = new ArrayList<Expression>();
        params.addAll(prefix.getFindArguments());
        params.addAll(index.getFindArguments());
        return params;
    }

    @Override
    protected List<FindParameterExpression> getFindParametersInner() {
        final List<FindParameterExpression> params = new ArrayList<FindParameterExpression>();
        params.addAll(prefix.getConcreteFindParameters());
        params.addAll(index.getConcreteFindParameters());
        return params;
    }

    @Override
    public int getFindAttributeCount() {
        return prefix.getFindAttributeCount() + index.getFindAttributeCount();
    }

    @Override
    public Expression getFindSkeletonInner() {
        final Expression nameSkel = prefix.getFindSkeleton();
        final Expression indexSkel = index.getFindSkeleton();

        if (indexSkel instanceof FindParameterExpression) {
            if (prefix.getType() instanceof ArrayType) {
                ((FindParameterExpression) indexSkel).overrideType(((ArrayType) prefix.getType()).getRange().getType());
            } else {
                ((FindParameterExpression) indexSkel).overrideType(IntegerType.createAnonymous());
            }
        }

        return new IndexedNameExpression(getPosition(), nameSkel, indexSkel);
    }

    @Override
    public int hashCode() {
        return prefix.hashCode() ^ index.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof IndexedNameExpression obj2) {

            return prefix.equals(obj2.prefix) && index.equals(obj2.index);
        } else {
            return false;
        }
    }

    @Override
    public void checkWriteableInner(final Position position) throws SemanticError {
        if (prefix instanceof SelectedAttributeExpression) {
            throw new SemanticError(SemanticErrorCode.AttributesAreOpaque, position);
        }
        prefix.checkWriteable(position);
    }

    @Override
    public <R, P> R accept(final ASTNodeVisitor<R, P> v, final P p) throws Exception {
        return v.visitIndexedNameExpression(this, p);
    }

    @Override
    public List<Expression> getChildExpressions() {
        return Arrays.asList(prefix, index);
    }

}
