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
import org.xtuml.masl.javagen.ast.code.Try;
import org.xtuml.masl.javagen.ast.types.Type;

import java.util.List;

public class TryImpl extends StatementImpl implements Try {

    TryImpl(final ASTImpl ast) {
        super(ast);
        setMainBlock(ast.createCodeBlock());
    }

    @Override
    public void accept(final ASTNodeVisitor v) throws Exception {
        v.visitTry(this);
    }

    @Override
    public CatchImpl addCatch(final Catch clause) {
        catchClauses.add((CatchImpl) clause);
        return (CatchImpl) clause;
    }

    @Override
    public List<? extends Catch> getCatches() {
        return catchClauses;
    }

    @Override
    public CodeBlock getFinallyBlock() {
        return finallyBlock.get();
    }

    @Override
    public CodeBlock getMainBlock() {
        return mainBlock.get();
    }

    @Override
    public CodeBlockImpl setFinallyBlock(final CodeBlock code) {
        finallyBlock.set((CodeBlockImpl) code);
        return (CodeBlockImpl) code;
    }

    @Override
    public CodeBlockImpl setMainBlock(final CodeBlock code) {
        mainBlock.set((CodeBlockImpl) code);
        return (CodeBlockImpl) code;
    }

    private final List<CatchImpl> catchClauses = new ChildNodeList<>(this);
    private final ChildNode<CodeBlockImpl> mainBlock = new ChildNode<>(this);
    private final ChildNode<CodeBlockImpl> finallyBlock = new ChildNode<>(this);

    @Override
    public Catch addCatch(final Type type, final String name) {
        return addCatch(getAST().createCatch(getAST().createParameter(type, name)));
    }
}
