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

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodel.type.TypeDefinition.ActualType;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.type.BasicType;
import org.xtuml.masl.metamodelImpl.type.DictionaryType;

import java.util.ArrayList;
import java.util.List;

public class DictionaryAccessExpression extends Expression
        implements org.xtuml.masl.metamodel.expression.DictionaryAccessExpression {

    private final Expression prefix;
    private final Expression key;

    public DictionaryAccessExpression(final Position position, final Expression prefix, final Expression index) throws
                                                                                                                SemanticError {
        super(position);
        ((DictionaryType) prefix.getType().getBasicType()).getKeyType().checkAssignable(index);
        if (prefix.getType().getBasicType().getActualType() != ActualType.DICTIONARY) {
            throw new SemanticError(SemanticErrorCode.ExpectedDictionaryExpression, position, prefix.getType());
        }

        this.prefix = prefix;
        this.key = index;
    }

    @Override
    public Expression getPrefix() {
        return prefix;
    }

    @Override
    public Expression getKey() {
        return key;
    }

    @Override
    public String toString() {
        return prefix + "[" + key + "]";
    }

    @Override
    public BasicType getType() {
        return ((DictionaryType) prefix.getType().getBasicType()).getValueType();
    }

    @Override
    protected List<Expression> getFindArgumentsInner() {
        final List<Expression> params = new ArrayList<>();
        params.addAll(prefix.getFindArguments());
        params.addAll(key.getFindArguments());
        return params;
    }

    @Override
    protected List<FindParameterExpression> getFindParametersInner() {
        final List<FindParameterExpression> params = new ArrayList<>();
        params.addAll(prefix.getConcreteFindParameters());
        params.addAll(key.getConcreteFindParameters());
        return params;
    }

    @Override
    public int getFindAttributeCount() {
        return prefix.getFindAttributeCount() + key.getFindAttributeCount();
    }

    @Override
    public Expression getFindSkeletonInner() {
        try {
            return new DictionaryAccessExpression(getPosition(), prefix.getFindSkeleton(), key.getFindSkeleton());
        } catch (final SemanticError e) {
            assert false;
            return null;
        }
    }

    @Override
    public int hashCode() {
        return prefix.hashCode() ^ key.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof DictionaryAccessExpression obj2) {

            return prefix.equals(obj2.prefix) && key.equals(obj2.key);
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
    public void accept(final ASTNodeVisitor v) {
        v.visitDictionaryAccessExpression(this);
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren(prefix, key);
    }

}
