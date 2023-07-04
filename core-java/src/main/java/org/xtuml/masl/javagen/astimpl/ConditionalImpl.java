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
import org.xtuml.masl.javagen.ast.expr.Conditional;
import org.xtuml.masl.javagen.ast.expr.Expression;

class ConditionalImpl extends ExpressionImpl implements Conditional {

    ConditionalImpl(final ASTImpl ast,
                    final Expression condition,
                    final Expression trueValue,
                    final Expression falseValue) {
        super(ast);
        setCondition(condition);
        setTrueValue(trueValue);
        setFalseValue(falseValue);
    }

    @Override
    public void accept(final ASTNodeVisitor v) throws Exception {
        v.visitConditional(this);
    }

    @Override
    public ExpressionImpl getCondition() {
        return condition.get();
    }

    @Override
    public ExpressionImpl getFalseValue() {
        return falseValue.get();
    }

    @Override
    public ExpressionImpl getTrueValue() {
        return trueValue.get();
    }

    @Override
    public ExpressionImpl setCondition(Expression expression) {
        // Parenthesize if conditional sub expression, or impossible to read
        if (((ExpressionImpl) expression).getPrecedence() <= getPrecedence()) {
            expression = getAST().createParenthesizedExpression(expression);
        }
        this.condition.set((ExpressionImpl) expression);
        return (ExpressionImpl) expression;
    }

    @Override
    public ExpressionImpl setFalseValue(Expression expression) {
        // Parenthesize if conditional sub expression, or impossible to read
        if (((ExpressionImpl) expression).getPrecedence() <= getPrecedence()) {
            expression = getAST().createParenthesizedExpression(expression);
        }
        this.falseValue.set((ExpressionImpl) expression);
        return (ExpressionImpl) expression;
    }

    @Override
    public ExpressionImpl setTrueValue(Expression expression) {
        // Parenthesize if conditional sub expression, or impossible to read
        if (((ExpressionImpl) expression).getPrecedence() <= getPrecedence()) {
            expression = getAST().createParenthesizedExpression(expression);
        }
        this.trueValue.set((ExpressionImpl) expression);
        return (ExpressionImpl) expression;
    }

    @Override
    protected int getPrecedence() {
        // Values from Java in a Nutshell Operator Summary Table
        return 2;
    }

    private final ChildNode<ExpressionImpl> condition = new ChildNode<>(this);
    private final ChildNode<ExpressionImpl> trueValue = new ChildNode<>(this);

    private final ChildNode<ExpressionImpl> falseValue = new ChildNode<>(this);
}
