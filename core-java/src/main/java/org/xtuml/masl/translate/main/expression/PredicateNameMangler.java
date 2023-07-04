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
package org.xtuml.masl.translate.main.expression;

import org.xtuml.masl.metamodel.expression.*;

import java.util.Objects;

public abstract class PredicateNameMangler {

    private static final String openParenthesisMangle = "OP";
    private static final String closeParenthesisMangle = "CP";

    private static class SimpleMangler extends PredicateNameMangler {

        SimpleMangler(final String name) {
            setName(name);
        }
    }

    private static class BinaryExpressionMangler extends PredicateNameMangler {

        BinaryExpressionMangler(final BinaryExpression expression) {
            String operator;
            switch (expression.getOperator()) {
                case EQUAL:
                    operator = "EQ";
                    break;
                case NOT_EQUAL:
                    operator = "NE";
                    break;
                case LESS_THAN:
                    operator = "LT";
                    break;
                case GREATER_THAN:
                    operator = "GT";
                    break;
                case LESS_THAN_OR_EQUAL:
                    operator = "LE";
                    break;
                case GREATER_THAN_OR_EQUAL:
                    operator = "GE";
                    break;
                case AND:
                    operator = "AND";
                    break;
                case OR:
                    operator = "OR";
                    break;
                case XOR:
                    operator = "XOR";
                    break;
                default:
                    throw new IllegalArgumentException("Binary operator '" +
                                                       expression.getOperator() +
                                                       "' invalid in find predicate");
            }

            setName(openParenthesisMangle +
                    createMangler(expression.getLhs()).getName() +
                    operator +
                    createMangler(expression.getRhs()).getName() +
                    closeParenthesisMangle);
        }

    }

    private static class UnaryExpressionMangler extends PredicateNameMangler {

        UnaryExpressionMangler(final UnaryExpression expression) {
            String operator;
            if (Objects.requireNonNull(expression.getOperator()) == UnaryExpression.Operator.NOT) {
                operator = "NOT";
            } else {
                throw new IllegalArgumentException("Unary operator '" +
                                                   expression.getOperator() +
                                                   "' invalid in find predicate");
            }

            setName(operator +
                    openParenthesisMangle +
                    createMangler(expression.getRhs()).getName() +
                    closeParenthesisMangle);
        }

    }

    public static PredicateNameMangler createMangler(final Expression expression) {
        if (expression instanceof BinaryExpression) {
            return new BinaryExpressionMangler((BinaryExpression) expression);
        } else if (expression instanceof FindParameterExpression) {
            return new SimpleMangler(((FindParameterExpression) expression).getName());
        } else if (expression instanceof FindAttributeNameExpression) {
            return new SimpleMangler("masl_" +
                                     ((FindAttributeNameExpression) expression).getAttribute().getName() +
                                     "_masl");
        } else if (expression instanceof SelectedComponentExpression) {
            return new SimpleMangler(createMangler(((SelectedComponentExpression) expression).getPrefix()).getName() +
                                     "DOTmasl_" +
                                     ((SelectedComponentExpression) expression).getComponent().getName() +
                                     "_masl");
        } else if (expression instanceof UnaryExpression) {
            return new UnaryExpressionMangler((UnaryExpression) expression);
        }

        throw new IllegalArgumentException("Unrecognised Expression " +
                                           expression.getClass() +
                                           " : '" +
                                           expression +
                                           "'");
    }

    public String getName() {
        return name;
    }

    private String name;

    void setName(final String name) {
        this.name = name;
    }
}
