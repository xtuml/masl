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
