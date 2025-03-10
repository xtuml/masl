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
    public void accept(final ASTNodeVisitor v) throws Exception {
        v.visitSwitch(this);
    }

    @Override
    public ExpressionImpl getDiscriminator() {
        return discriminator.get();
    }

    @Override
    public void setDiscriminator(final Expression discriminator) {
        this.discriminator.set((ExpressionImpl) discriminator);
    }

    private final ChildNode<ExpressionImpl> discriminator = new ChildNode<>(this);
    private final ChildNodeList<SwitchBlockImpl> switchBlocks = new ChildNodeList<>(this);
    private final Scope scope = new SScope();

    @Override
    public Scope getScope() {
        return scope;
    }
}
