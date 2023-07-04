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
package org.xtuml.masl.javagen.astimpl;

import org.xtuml.masl.javagen.ast.ASTNodeVisitor;
import org.xtuml.masl.javagen.ast.expr.ArrayInitializer;
import org.xtuml.masl.javagen.ast.expr.Expression;

import java.util.Collections;
import java.util.List;

class ArrayInitializerImpl extends ExpressionImpl implements ArrayInitializer {

    ArrayInitializerImpl(final ASTImpl ast, final Expression... elements) {
        super(ast);
        for (final Expression element : elements) {
            addElement(element);
        }
    }

    @Override
    public void accept(final ASTNodeVisitor v) throws Exception {
        v.visitArrayInitializer(this);
    }

    @Override
    public ExpressionImpl addElement(final Expression element) {
        elements.add((ExpressionImpl) element);
        return (ExpressionImpl) element;
    }

    @Override
    public List<? extends Expression> getElements() {
        return Collections.unmodifiableList(elements);
    }

    @Override
    protected int getPrecedence() {
        // Should never be used anywhere other then where it is syntactically
        // unambiguous, so should never need parenthesizing
        return Integer.MAX_VALUE;
    }

    private final ChildNodeList<ExpressionImpl> elements = new ChildNodeList<>(this);

}
