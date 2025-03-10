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
import org.xtuml.masl.javagen.ast.def.InitializerBlock;

public class InitializerBlockImpl extends TypeMemberImpl implements InitializerBlock {

    InitializerBlockImpl(final ASTImpl ast, final boolean isStatic) {
        super(ast);
        this.isStatic = isStatic;
        setCodeBlock();
    }

    @Override
    public CodeBlockImpl getCodeBlock() {
        return codeBlock.get();
    }

    @Override
    public boolean isStatic() {
        return isStatic;
    }

    @Override
    public void accept(final ASTNodeVisitor v) throws Exception {
        v.visitInitializerBlock(this);
    }

    private final boolean isStatic;
    private final ChildNode<CodeBlockImpl> codeBlock = new ChildNode<>(this);

    @Override
    public CodeBlock setCodeBlock() {
        return setCodeBlock(getAST().createCodeBlock());
    }

    @Override
    public CodeBlock setCodeBlock(final CodeBlock codeBlock) {
        this.codeBlock.set((CodeBlockImpl) codeBlock);
        return codeBlock;
    }

}
