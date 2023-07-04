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
    public <R, P> R accept(final ASTNodeVisitor<R, P> v, final P p) throws Exception {
        return v.visitInitializerBlock(this, p);
    }

    private final boolean isStatic;
    private final ChildNode<CodeBlockImpl> codeBlock = new ChildNode<CodeBlockImpl>(this);

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
