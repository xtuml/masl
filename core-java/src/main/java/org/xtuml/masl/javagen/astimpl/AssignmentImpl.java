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
import org.xtuml.masl.javagen.ast.expr.Assignment;
import org.xtuml.masl.javagen.ast.expr.Expression;

class AssignmentImpl extends ExpressionImpl implements Assignment {

    AssignmentImpl(final ASTImpl ast, final Expression target, final Operator operator, final Expression source) {
        super(ast);
        setTarget(target);
        setOperator(operator);
        setSource(source);
    }

    @Override
    public <R, P> R accept(final ASTNodeVisitor<R, P> v, final P p) throws Exception {
        return v.visitAssignmen(this, p);
    }

    @Override
    public Operator getOperator() {
        return operator;
    }

    @Override
    public ExpressionImpl getSource() {
        return source.get();
    }

    @Override
    public ExpressionImpl getTarget() {
        return target.get();
    }

    @Override
    public void setOperator(final Operator operator) {
        this.operator = operator;
    }

    @Override
    public ExpressionImpl setSource(Expression expression) {
        if (((ExpressionImpl) expression).getPrecedence() < getPrecedence()) {
            expression = getAST().createParenthesizedExpression(expression);
        }
        this.source.set((ExpressionImpl) expression);
        return (ExpressionImpl) expression;
    }

    @Override
    public ExpressionImpl setTarget(Expression expression) {
        // Right Associative, so need to parenthesize lhs if equal precedence
        if (((ExpressionImpl) expression).getPrecedence() <= getPrecedence()) {
            expression = getAST().createParenthesizedExpression(expression);
        }
        this.target.set((ExpressionImpl) expression);
        return (ExpressionImpl) expression;
    }

    @Override
    protected int getPrecedence() {
        // Value from Java in a Nutshell Operator Summary Table
        return 1;
    }

    private final ChildNode<ExpressionImpl> target = new ChildNode<ExpressionImpl>(this);
    private final ChildNode<ExpressionImpl> source = new ChildNode<ExpressionImpl>(this);

    Operator operator;
}
