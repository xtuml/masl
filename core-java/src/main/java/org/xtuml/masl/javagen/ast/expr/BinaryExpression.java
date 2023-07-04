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

public interface BinaryExpression extends Expression {

    enum Operator {
        MULTIPLY, DIVIDE, REMAINDER, ADD, SUBTRACT, LEFT_SHIFT, RIGHT_SHIFT, RIGHT_SHIFT_UNSIGNED, LESS_THAN, GREATER_THAN, LESS_THAN_OR_EQUAL_TO, GREATER_THAN_OR_EQUAL_TO, INSTANCEOF, EQUAL_TO, NOT_EQUAL_TO, BITWISE_AND, BITWISE_OR, BITWISE_XOR, AND, OR
    }

    Expression getLhs();

    Operator getOperator();

    Expression getRhs();

    Expression setLhs(Expression lhs);

    Expression setRhs(Expression rhs);
}
