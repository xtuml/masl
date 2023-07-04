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

import org.xtuml.masl.javagen.ast.def.Field;
import org.xtuml.masl.javagen.ast.def.Method;
import org.xtuml.masl.javagen.ast.expr.*;
import org.xtuml.masl.javagen.ast.types.Type;

public abstract class ExpressionImpl extends ASTNodeImpl implements Expression, StatementExpression {

    @Override
    public ArrayLengthAccess length() {
        return getAST().createArrayLengthAccess(this);
    }

    ExpressionImpl(final ASTImpl ast) {
        super(ast);
    }

    abstract protected int getPrecedence();

    @Override
    public ExpressionStatementImpl asStatement() {
        return getAST().createExpressionStatement(this);
    }

    @Override
    public ArrayAccessImpl arrayIndex(final Expression index) {
        return getAST().createArrayAccess(this, index);
    }

    @Override
    public AssignmentImpl assign(final Expression value) {
        return getAST().createAssignment(this, value);
    }

    @Override
    public AssignmentImpl assign(final Assignment.Operator operator, final Expression value) {
        return getAST().createAssignment(this, operator, value);
    }

    @Override
    public BinaryExpressionImpl multiply(final Expression rhs) {
        return getAST().createBinaryExpression(this, BinaryExpression.Operator.MULTIPLY, rhs);
    }

    @Override
    public BinaryExpressionImpl divide(final Expression rhs) {
        return getAST().createBinaryExpression(this, BinaryExpression.Operator.DIVIDE, rhs);
    }

    @Override
    public BinaryExpressionImpl remainder(final Expression rhs) {
        return getAST().createBinaryExpression(this, BinaryExpression.Operator.REMAINDER, rhs);
    }

    @Override
    public BinaryExpressionImpl add(final Expression rhs) {
        return getAST().createBinaryExpression(this, BinaryExpression.Operator.ADD, rhs);
    }

    @Override
    public BinaryExpressionImpl subtract(final Expression rhs) {
        return getAST().createBinaryExpression(this, BinaryExpression.Operator.SUBTRACT, rhs);
    }

    @Override
    public BinaryExpressionImpl leftShift(final Expression rhs) {
        return getAST().createBinaryExpression(this, BinaryExpression.Operator.LEFT_SHIFT, rhs);
    }

    @Override
    public BinaryExpressionImpl rightShift(final Expression rhs) {
        return getAST().createBinaryExpression(this, BinaryExpression.Operator.RIGHT_SHIFT, rhs);
    }

    @Override
    public BinaryExpressionImpl rightShiftUnsigned(final Expression rhs) {
        return getAST().createBinaryExpression(this, BinaryExpression.Operator.RIGHT_SHIFT_UNSIGNED, rhs);
    }

    @Override
    public BinaryExpressionImpl lessThan(final Expression rhs) {
        return getAST().createBinaryExpression(this, BinaryExpression.Operator.LESS_THAN, rhs);
    }

    @Override
    public BinaryExpressionImpl greaterThan(final Expression rhs) {
        return getAST().createBinaryExpression(this, BinaryExpression.Operator.GREATER_THAN, rhs);
    }

    @Override
    public BinaryExpressionImpl lessThanOrEqualTo(final Expression rhs) {
        return getAST().createBinaryExpression(this, BinaryExpression.Operator.LESS_THAN_OR_EQUAL_TO, rhs);
    }

    @Override
    public BinaryExpressionImpl greaterThanOrEqualTo(final Expression rhs) {
        return getAST().createBinaryExpression(this, BinaryExpression.Operator.GREATER_THAN_OR_EQUAL_TO, rhs);
    }

    @Override
    public BinaryExpressionImpl isInstanceof(final Expression rhs) {
        return getAST().createBinaryExpression(this, BinaryExpression.Operator.INSTANCEOF, rhs);
    }

    @Override
    public BinaryExpressionImpl equalTo(final Expression rhs) {
        return getAST().createBinaryExpression(this, BinaryExpression.Operator.EQUAL_TO, rhs);
    }

    @Override
    public BinaryExpressionImpl notEqualTo(final Expression rhs) {
        return getAST().createBinaryExpression(this, BinaryExpression.Operator.NOT_EQUAL_TO, rhs);
    }

    @Override
    public BinaryExpressionImpl bitwiseAnd(final Expression rhs) {
        return getAST().createBinaryExpression(this, BinaryExpression.Operator.BITWISE_AND, rhs);
    }

    @Override
    public BinaryExpressionImpl bitwiseOr(final Expression rhs) {
        return getAST().createBinaryExpression(this, BinaryExpression.Operator.BITWISE_OR, rhs);
    }

    @Override
    public BinaryExpressionImpl bitwiseXor(final Expression rhs) {
        return getAST().createBinaryExpression(this, BinaryExpression.Operator.BITWISE_XOR, rhs);
    }

    @Override
    public BinaryExpressionImpl and(final Expression rhs) {
        return getAST().createBinaryExpression(this, BinaryExpression.Operator.AND, rhs);
    }

    @Override
    public BinaryExpressionImpl or(final Expression rhs) {
        return getAST().createBinaryExpression(this, BinaryExpression.Operator.OR, rhs);
    }

    @Override
    public CastImpl castTo(final Type type) {
        return getAST().createCast(type, this);
    }

    @Override
    public ConditionalImpl conditional(final Expression trueValue, final Expression falseValue) {
        return getAST().createConditional(this, trueValue, falseValue);
    }

    @Override
    public FieldAccessImpl dot(final Field field) {
        return getAST().createFieldAccess(this, field);
    }

    @Override
    public MethodInvocationImpl dot(final Method method, final Expression... args) {
        return getAST().createMethodInvocation(this, method, args);
    }

    @Override
    public ParenthesizedExpressionImpl parenthesize() {
        return getAST().createParenthesizedExpression(this);
    }

    @Override
    public PostfixExpressionImpl postIncrement() {
        return getAST().createPostIncrement(this);
    }

    @Override
    public PostfixExpressionImpl postDecrement() {
        return getAST().createPostDecrement(this);
    }

    @Override
    public PrefixExpressionImpl increment() {
        return getAST().createPreIncrement(this);
    }

    @Override
    public PrefixExpressionImpl decrement() {
        return getAST().createPreDecrement(this);
    }

    @Override
    public UnaryExpressionImpl plus() {
        return getAST().createUnaryExpression(UnaryExpression.Operator.PLUS, this);
    }

    @Override
    public UnaryExpressionImpl minus() {
        return getAST().createUnaryExpression(UnaryExpression.Operator.MINUS, this);
    }

    @Override
    public UnaryExpressionImpl not() {
        return getAST().createUnaryExpression(UnaryExpression.Operator.NOT, this);
    }

    @Override
    public UnaryExpressionImpl complement() {
        return getAST().createUnaryExpression(UnaryExpression.Operator.BITWISE_COMPLEMENT, this);
    }

}
