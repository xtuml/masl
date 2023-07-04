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
import org.xtuml.masl.javagen.ast.expr.Expression;

import java.util.List;

public class SwitchImpl extends StatementImpl implements org.xtuml.masl.javagen.ast.code.Switch, Scoped {

    private final class SScope extends Scope {

        SScope() {
            super(SwitchImpl.this);
        }

        @Override
        protected boolean requiresQualifier(final Scope baseScope,
                                            final FieldAccessImpl fieldAccess,
                                            final boolean visible,
                                            boolean shadowed) {
            StatementImpl curStatement = fieldAccess.getEnclosingStatement();
            while (curStatement.getEnclosingStatement() != SwitchImpl.this) {
                // Assume we are in the correct hierarchy... null access if not!
                curStatement = curStatement.getEnclosingStatement();
            }

            for (final SwitchBlockImpl block : switchBlocks) {
                for (final StatementImpl statement : block.getStatements()) {
                    if (statement == curStatement) {
                        // Not affected by declarations after the current statement
                        break;
                    }

                    if (statement instanceof VariableDeclarationStatementImpl &&
                        ((VariableDeclarationStatementImpl) statement).getLocalVariable().getName().equals(fieldAccess.getField().getName())) {
                        shadowed = true;
                    }
                }
            }
            return super.requiresQualifier(baseScope, fieldAccess, visible, shadowed);
        }

        @Override
        protected boolean requiresQualifier(final Scope baseScope,
                                            final EnumConstantAccessImpl enumAccess,
                                            final boolean visible,
                                            boolean shadowed) {
            StatementImpl curStatement = enumAccess.getEnclosingStatement();
            while (curStatement.getEnclosingStatement() != SwitchImpl.this) {
                // Assume we are in the correct hierarchy... null access if not!
                curStatement = curStatement.getEnclosingStatement();
            }

            for (final SwitchBlockImpl block : switchBlocks) {
                for (final StatementImpl statement : block.getStatements()) {
                    if (statement == curStatement) {
                        // Not affected by declarations after the current statement
                        break;
                    }

                    if (statement instanceof VariableDeclarationStatementImpl &&
                        ((VariableDeclarationStatementImpl) statement).getLocalVariable().getName().equals(enumAccess.getConstant().getName())) {
                        shadowed = true;
                    }
                }
            }
            return super.requiresQualifier(baseScope, enumAccess, visible, shadowed);
        }

    }

    public SwitchImpl(final ASTImpl ast, final ExpressionImpl discriminator) {
        super(ast);
        setDiscriminator(discriminator);
    }

    @Override
    public void addSwitchBlock(final SwitchBlock switchBlock) {
        switchBlocks.add((SwitchBlockImpl) switchBlock);
    }

    @Override
    public List<? extends SwitchBlock> getSwitchBlocks() {
        return switchBlocks;
    }

    @Override
    public <R, P> R accept(final ASTNodeVisitor<R, P> v, final P p) throws Exception {
        return v.visitSwitch(this, p);
    }

    @Override
    public ExpressionImpl getDiscriminator() {
        return discriminator.get();
    }

    @Override
    public void setDiscriminator(final Expression discriminator) {
        this.discriminator.set((ExpressionImpl) discriminator);
    }

    private final ChildNode<ExpressionImpl> discriminator = new ChildNode<ExpressionImpl>(this);
    private final ChildNodeList<SwitchBlockImpl> switchBlocks = new ChildNodeList<SwitchBlockImpl>(this);
    private final Scope scope = new SScope();

    @Override
    public Scope getScope() {
        return scope;
    }
}
