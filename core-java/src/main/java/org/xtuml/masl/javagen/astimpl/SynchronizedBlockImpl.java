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
