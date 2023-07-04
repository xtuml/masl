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
import org.xtuml.masl.javagen.ast.expr.Cast;
import org.xtuml.masl.javagen.ast.expr.Expression;
import org.xtuml.masl.javagen.ast.types.Type;

class CastImpl extends ExpressionImpl implements Cast {

    CastImpl(final ASTImpl ast, final Type type, final Expression expression) {
        super(ast);
        setType(type);
        setExpression(expression);
    }

    @Override
    public void accept(final ASTNodeVisitor v) throws Exception {
        v.visitCast(this);
    }

    @Override
    public ExpressionImpl getExpression() {
        return expression.get();
    }

    @Override
    public TypeImpl getType() {
        return type.get();
    }

    @Override
    public ExpressionImpl setExpression(Expression expression) {
        if (((ExpressionImpl) expression).getPrecedence() < getPrecedence()) {
            expression = getAST().createParenthesizedExpression(expression);
        }
        this.expression.set((ExpressionImpl) expression);
        return (ExpressionImpl) expression;
    }

    @Override
    public TypeImpl setType(final Type type) {
        this.type.set((TypeImpl) type);
        return (TypeImpl) type;
    }

    @Override
    protected int getPrecedence() {
        // From operator precedence table in Java in a Nutshell
        return 13;
    }

    private final ChildNode<TypeImpl> type = new ChildNode<>(this);
    private final ChildNode<ExpressionImpl> expression = new ChildNode<>(this);
}
