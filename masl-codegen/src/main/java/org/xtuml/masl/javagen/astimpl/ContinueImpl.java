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
import org.xtuml.masl.javagen.ast.code.LabeledStatement;

public class ContinueImpl extends StatementImpl implements org.xtuml.masl.javagen.ast.code.Continue {

    public ContinueImpl(final ASTImpl ast) {
        super(ast);
    }

    @Override
    public LabeledStatement getReferencedLabel() {
        return referencedLabel;
    }

    @Override
    public void setReferencedLabel(final LabeledStatement label) {
        this.referencedLabel = label;
    }

    @Override
    public void accept(final ASTNodeVisitor v) throws Exception {
        v.visitContinue(this);
    }

    // Not a child node, just a reference to a node in another tree.
    private LabeledStatement referencedLabel = null;

}
