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
import org.xtuml.masl.javagen.ast.code.LocalVariable;
import org.xtuml.masl.javagen.ast.expr.Expression;
import org.xtuml.masl.javagen.ast.types.Type;

public class LocalVariableImpl extends VariableImpl implements LocalVariable {

    LocalVariableImpl(final ASTImpl ast, final Type type, final String name) {
        super(ast, type, name);
    }

    LocalVariableImpl(final ASTImpl ast, final Type type, final String name, final Expression initialValue) {
        super(ast, type, name);
        setInitialValue(initialValue);
    }

    @Override
    public void accept(final ASTNodeVisitor v) throws Exception {
        v.visitLocalVariable(this);
    }

    private final ChildNode<ExpressionImpl> initialValue = new ChildNode<>(this);

    @Override
    public Expression getInitialValue() {
        return initialValue.get();
    }

    @Override
    public void setInitialValue(final Expression initialValue) {
        this.initialValue.set((ExpressionImpl) initialValue);
    }

    @Override
    public VariableDeclarationStatementImpl asStatement() {
        return getAST().createVariableDeclaration(this);
    }

}
