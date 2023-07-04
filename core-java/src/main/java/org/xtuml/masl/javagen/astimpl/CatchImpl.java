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
