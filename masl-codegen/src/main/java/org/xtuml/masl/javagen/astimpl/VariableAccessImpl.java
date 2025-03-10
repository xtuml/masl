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
import org.xtuml.masl.javagen.ast.code.Variable;
import org.xtuml.masl.javagen.ast.expr.VariableAccess;

public class VariableAccessImpl extends ExpressionImpl implements VariableAccess {

    public VariableAccessImpl(final ASTImpl ast, final Variable variable) {
        super(ast);
        setVariable(variable);
    }

    @Override
    protected int getPrecedence() {
        return Integer.MAX_VALUE;
    }

    @Override
    public Variable getVariable() {
        return variable;
    }

    @Override
    public Variable setVariable(final Variable variable) {
        this.variable = variable;
        return variable;
    }

    @Override
    public void accept(final ASTNodeVisitor v) throws Exception {
        v.visitvariableAccess(this);
    }

    private Variable variable;

}
