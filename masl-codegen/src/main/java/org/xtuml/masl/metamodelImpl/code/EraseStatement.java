/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.metamodelImpl.code;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.expression.DictionaryAccessExpression;
import org.xtuml.masl.metamodelImpl.expression.Expression;

import java.util.List;

public class EraseStatement extends Statement implements org.xtuml.masl.metamodel.code.EraseStatement {

    private final Expression dictionary;
    private final Expression key;

    public static EraseStatement create(final Position position, final Expression expression) {
        if (expression == null) {
            return null;
        }

        try {
            if (expression instanceof DictionaryAccessExpression) {
                return new EraseStatement(position,
                                          ((DictionaryAccessExpression) expression).getPrefix(),
                                          ((DictionaryAccessExpression) expression).getKey());
            } else {
                throw new SemanticError(SemanticErrorCode.EraseOnlyValidforDictionary, position);
            }

        } catch (final SemanticError e) {
            e.report();
            return null;
        }
    }

    private EraseStatement(final Position position, final Expression dictionary, final Expression key) {
        super(position);

        this.dictionary = dictionary;
        this.key = key;
    }

    @Override
    public Expression getDictionary() {
        return dictionary;
    }

    @Override
    public Expression getKey() {
        return key;
    }

    @Override
    public String toString() {
        return "erase " + dictionary + "[" + key + "];";
    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitEraseStatement(this);
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren(dictionary, key);
    }

}
