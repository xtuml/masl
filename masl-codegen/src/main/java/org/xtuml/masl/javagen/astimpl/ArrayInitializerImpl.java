/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
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
