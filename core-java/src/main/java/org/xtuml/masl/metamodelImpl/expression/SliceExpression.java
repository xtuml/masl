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
import org.xtuml.masl.metamodel.type.DictionaryType;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.type.*;

import java.util.ArrayList;
import java.util.List;

public class SliceExpression extends Expression implements org.xtuml.masl.metamodel.expression.SliceExpression {

    public static Expression create(final Position position, final Expression prefix, final Expression range) {
        if (prefix == null || range == null) {
            return null;
        }
        try {
            if (prefix.getType().getBasicType() instanceof SequenceType ||
                prefix.getType().getBasicType() instanceof ArrayType ||
                prefix.getType().getBasicType() instanceof StringType) {
                if (range instanceof RangeExpression) {
                    RangeType.createAnonymous(IntegerType.createAnonymous()).checkAssignable(range);
                    return new SliceExpression(position, prefix, (RangeExpression) range);
                } else {
                    IntegerType.createAnonymous().checkAssignable(range);
                    return new IndexedNameExpression(position, prefix, range);
                }
            } else if (prefix.getType().getBasicType() instanceof DictionaryType) {
                return new DictionaryAccessExpression(position, prefix, range);
            } else {
                throw new SemanticError(SemanticErrorCode.IndexNotValid, position, prefix.getType());
            }
        } catch (final SemanticError e) {
            e.report();
            return null;
        }
    }

    public SliceExpression(final Position position, final Expression prefix, final RangeExpression range) {
        super(position);
        this.prefix = prefix;
        this.range = range;
    }

    @Override
    public Expression getPrefix() {
        return prefix;
    }

    @Override
    public RangeExpression getRange() {
        return range;
    }

    @Override
    public BasicType getType() {
        if (prefix.getType().getBasicType() instanceof ArrayType) {
            return SequenceType.createAnonymous(prefix.getType().getContainedType());
        } else {
            return prefix.getType();
        }
    }

    @Override
    public String toString() {
        return prefix + "[" + range + "]";
    }

    private final Expression prefix;
    private final RangeExpression range;

    @Override
    protected List<Expression> getFindArgumentsInner() {
        final List<Expression> params = new ArrayList<>();
        params.addAll(prefix.getFindArguments());
        if (range instanceof MinMaxRange) {
            params.addAll(getRange().getMin().getFindArguments());
            params.addAll(getRange().getMax().getFindArguments());
        }
        return params;
    }

    @Override
    protected List<FindParameterExpression> getFindParametersInner() {
        final List<FindParameterExpression> params = new ArrayList<>();
        params.addAll(prefix.getConcreteFindParameters());
        if (range instanceof MinMaxRange) {
            params.addAll(getRange().getMin().getConcreteFindParameters());
            params.addAll(getRange().getMax().getConcreteFindParameters());
        }
        return params;
    }

    @Override
    public int getFindAttributeCount() {
        return prefix.getFindAttributeCount() +
               getRange().getMin().getFindAttributeCount() +
               getRange().getMax().getFindAttributeCount();
    }

    @Override
    public Expression getFindSkeletonInner() {
        if (range instanceof MinMaxRange) {
            final Expression minSkel = getRange().getMin().getFindSkeleton();
            final Expression maxSkel = getRange().getMax().getFindSkeleton();

            if (minSkel instanceof FindParameterExpression) {
                if (prefix.getType() instanceof ArrayType) {
                    ((FindParameterExpression) minSkel).overrideType(((ArrayType) prefix.getType()).getRange().getType());
                } else {
                    ((FindParameterExpression) minSkel).overrideType(IntegerType.createAnonymous());
                }
            }

            if (maxSkel instanceof FindParameterExpression) {
                if (prefix.getType() instanceof ArrayType) {
                    ((FindParameterExpression) maxSkel).overrideType(((ArrayType) prefix.getType()).getRange().getType());
                } else {
                    ((FindParameterExpression) maxSkel).overrideType(IntegerType.createAnonymous());
                }
            }

            return new SliceExpression(null, prefix.getFindSkeleton(), new MinMaxRange(minSkel, maxSkel));
        } else {
            return new SliceExpression(null, prefix.getFindSkeleton(), range);
        }
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SliceExpression obj2)) {
            return false;
        } else {

            return prefix.equals(obj2.prefix) && range.equals(obj2.range);
        }
    }

    @Override
    public int hashCode() {

        return prefix.hashCode() ^ range.hashCode();
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
        v.visitSliceExpression(this);
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren(prefix, range);
    }

}
