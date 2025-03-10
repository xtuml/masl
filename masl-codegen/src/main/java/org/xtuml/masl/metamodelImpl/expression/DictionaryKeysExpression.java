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
import org.xtuml.masl.metamodelImpl.type.BasicType;
import org.xtuml.masl.metamodelImpl.type.DictionaryType;
import org.xtuml.masl.metamodelImpl.type.SetType;

import java.util.ArrayList;
import java.util.List;

public class DictionaryKeysExpression extends Expression
        implements org.xtuml.masl.metamodel.expression.DictionaryKeysExpression {

    DictionaryKeysExpression(final Position position, final Expression dictionary) throws SemanticError {
        super(position);

        if (dictionary.getType().getBasicType().getActualType() != ActualType.DICTIONARY) {
            throw new SemanticError(SemanticErrorCode.ExpectedDictionaryExpression, position, dictionary.getType());
        }

        this.dictionary = dictionary;
        this.type = SetType.createAnonymous(((DictionaryType) dictionary.getType().getBaseType()).getKeyType());
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj != null) {
            if (this == obj) {
                return true;
            }
            if (obj.getClass() == getClass()) {
                final DictionaryKeysExpression obj2 = ((DictionaryKeysExpression) obj);
                return dictionary.equals(obj2.dictionary);
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
        return dictionary.hashCode();
    }

    @Override
    public int getFindAttributeCount() {
        return dictionary.getFindAttributeCount();
    }

    @Override
    public Expression getFindSkeletonInner() {
        try {
            return new DictionaryKeysExpression(getPosition(), dictionary.getFindSkeleton());
        } catch (final SemanticError e) {
            e.printStackTrace();
            assert false;
            return null;
        }

    }

    @Override
    protected List<Expression> getFindArgumentsInner() {
        final List<Expression> params = new ArrayList<>();
        params.addAll(dictionary.getFindArguments());
        return params;

    }

    @Override
    protected List<FindParameterExpression> getFindParametersInner() {
        final List<FindParameterExpression> params = new ArrayList<>();
        params.addAll(dictionary.getConcreteFindParameters());
        return params;
    }

    private final Expression dictionary;
    private final SetType type;

    @Override
    public Expression getDictionary() {
        return dictionary;
    }

    @Override
    public String toString() {
        return dictionary + "'keys";
    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitDictionaryKeysExpression(this);
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren(dictionary);
    }

}
