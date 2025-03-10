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
import org.xtuml.masl.javagen.ast.code.Catch;
import org.xtuml.masl.javagen.ast.code.CodeBlock;
import org.xtuml.masl.javagen.ast.def.Parameter;

public class CatchImpl extends ASTNodeImpl implements Catch {

    public CatchImpl(final ASTImpl ast, final Parameter exceptionParameter) {
        super(ast);
        setCodeBlock(ast.createCodeBlock());
        setException(exceptionParameter);
    }

    @Override
    public void setCodeBlock(final CodeBlock codeBlock) {
        this.codeBlock.set((CodeBlockImpl) codeBlock);
    }

    @Override
    public CodeBlockImpl getCodeBlock() {
        return codeBlock.get();
    }

    @Override
    public ParameterImpl getException() {
        return exceptionParameter.get();
    }

    @Override
    public void setException(final Parameter exceptionParameter) {
        this.exceptionParameter.set((ParameterImpl) exceptionParameter);
    }

    @Override
    public void accept(final ASTNodeVisitor v) throws Exception {
        v.visitCatch(this);
    }

    private final ChildNode<ParameterImpl> exceptionParameter = new ChildNode<>(this);
    private final ChildNode<CodeBlockImpl> codeBlock = new ChildNode<>(this);

}
