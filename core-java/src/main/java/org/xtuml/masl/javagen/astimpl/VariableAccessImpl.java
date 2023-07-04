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
    public <R, P> R accept(final ASTNodeVisitor<R, P> v, final P p) throws Exception {
        return v.visitvariableAccess(this, p);
    }

    private Variable variable;

}
