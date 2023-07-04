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
import org.xtuml.masl.javagen.ast.expr.ArrayLengthAccess;
import org.xtuml.masl.javagen.ast.expr.Expression;

public class ArrayLengthAccessImpl extends ExpressionImpl implements ArrayLengthAccess {

    ArrayLengthAccessImpl(final ASTImpl ast, final Expression instance) {
        super(ast);
        setInstance(instance);
    }

    @Override
    public <R, P> R accept(final ASTNodeVisitor<R, P> v, final P p) throws Exception {
        return v.visitArrayLengthAccess(this, p);
    }

    @Override
    public ExpressionImpl getInstance() {
        return instance.get();
    }

    @Override
    public ExpressionImpl setInstance(Expression instance) {
        if (((ExpressionImpl) instance).getPrecedence() < getPrecedence()) {
            instance = getAST().createParenthesizedExpression(instance);
        }
        this.instance.set((ExpressionImpl) instance);
        return (ExpressionImpl) instance;
    }

    @Override
    protected int getPrecedence() {
        return 15;
    }

    private final ChildNode<ExpressionImpl> instance = new ChildNode<ExpressionImpl>(this);

}
