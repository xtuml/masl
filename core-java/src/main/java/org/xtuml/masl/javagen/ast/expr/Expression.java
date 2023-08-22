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
package org.xtuml.masl.javagen.ast.expr;

import org.xtuml.masl.javagen.ast.ASTNode;
import org.xtuml.masl.javagen.ast.def.Field;
import org.xtuml.masl.javagen.ast.def.Method;
import org.xtuml.masl.javagen.ast.types.Type;

public interface Expression extends ASTNode {

    ArrayAccess arrayIndex(Expression index);

    Assignment assign(Expression value);

    Assignment assign(Assignment.Operator operator, Expression value);

    BinaryExpression multiply(Expression rhs);

    BinaryExpression divide(Expression rhs);

    BinaryExpression remainder(Expression rhs);

    BinaryExpression add(Expression rhs);

    BinaryExpression subtract(Expression rhs);

    BinaryExpression leftShift(Expression rhs);

    BinaryExpression rightShift(Expression rhs);

    BinaryExpression rightShiftUnsigned(Expression rhs);

    BinaryExpression lessThan(Expression rhs);

    BinaryExpression greaterThan(Expression rhs);

    BinaryExpression lessThanOrEqualTo(Expression rhs);

    BinaryExpression greaterThanOrEqualTo(Expression rhs);

    BinaryExpression isInstanceof(Expression rhs);

    BinaryExpression equalTo(Expression rhs);

    BinaryExpression notEqualTo(Expression rhs);

    BinaryExpression bitwiseAnd(Expression rhs);

    BinaryExpression bitwiseOr(Expression rhs);

    BinaryExpression bitwiseXor(Expression rhs);

    BinaryExpression and(Expression rhs);

    BinaryExpression or(Expression rhs);

    Cast castTo(Type type);

    Conditional conditional(Expression trueValue, Expression falseValue);

    FieldAccess dot(Field field);

    MethodInvocation dot(Method method, Expression... args);

    ParenthesizedExpression parenthesize();

    PostfixExpression postIncrement();

    PostfixExpression postDecrement();

    PrefixExpression increment();

    PrefixExpression decrement();

    UnaryExpression plus();

    UnaryExpression minus();

    UnaryExpression not();

    UnaryExpression complement();

    ArrayLengthAccess length();

}
