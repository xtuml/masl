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
import org.xtuml.masl.javagen.ast.code.CodeBlock;
import org.xtuml.masl.javagen.ast.code.SynchronizedBlock;
import org.xtuml.masl.javagen.ast.expr.Expression;

public class SynchronizedBlockImpl extends StatementImpl implements SynchronizedBlock {

    public SynchronizedBlockImpl(final ASTImpl ast, final Expression lockExpression) {
        super(ast);
        setLockExpression(lockExpression);
        setCodeBlock(ast.createCodeBlock());
    }

    @Override
    public CodeBlock getCodeBlock() {
        return codeBlock.get();
    }

    @Override
    public void setCodeBlock(final CodeBlock block) {
        codeBlock.set((CodeBlockImpl) block);
    }

    @Override
    public Expression getLockExpression() {
        return lockExpression.get();
    }

    @Override
    public void setLockExpression(final Expression expression) {
        lockExpression.set((ExpressionImpl) expression);
    }

    @Override
    public void accept(final ASTNodeVisitor v) throws Exception {
        v.visitSynchronizedBlock(this);
    }

    private final ChildNode<ExpressionImpl> lockExpression = new ChildNode<>(this);
    private final ChildNode<CodeBlockImpl> codeBlock = new ChildNode<>(this);
}
