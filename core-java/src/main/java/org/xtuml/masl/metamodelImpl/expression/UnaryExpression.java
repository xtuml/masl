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
package org.xtuml.masl.metamodelImpl.expression;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.common.Positioned;
import org.xtuml.masl.metamodelImpl.type.BasicType;
import org.xtuml.masl.metamodelImpl.type.BooleanType;

import java.util.List;

public class UnaryExpression extends Expression implements org.xtuml.masl.metamodel.expression.UnaryExpression {

    private final Expression rhs;
    private final OperatorRef operator;

    private static abstract class ResultType {

        abstract BasicType getResultType(Expression rhs);
    }

    private static final ResultType BINARY = new ResultType() {

        @Override
        BasicType getResultType(final Expression rhs) {
            return BooleanType.createAnonymous();
        }
    };

    private static final ResultType ARITHMETIC = new ResultType() {

        @Override
        BasicType getResultType(final Expression rhs) {
            return rhs.getType();
        }
    };

    public enum ImplOperator {
        MINUS("-", ARITHMETIC, Operator.MINUS) {
            @Override
            LiteralExpression evaluate(final Expression rhs) {
                if (rhs instanceof IntegerLiteral) {
                    return new IntegerLiteral(-((IntegerLiteral) rhs).getValue());
                } else if (rhs instanceof RealLiteral) {
                    return new RealLiteral(-((RealLiteral) rhs).getValue());
                } else {
                    return null;
                }
            }
        }, PLUS("+", ARITHMETIC, Operator.PLUS) {
            @Override
            LiteralExpression evaluate(final Expression rhs) {
                if (rhs instanceof NumericLiteral) {
                    return (NumericLiteral) rhs;
                } else {
                    return null;
                }
            }
        }, NOT("not", BINARY, Operator.NOT) {
            @Override
            LiteralExpression evaluate(final Expression rhs) {
                if (rhs instanceof BooleanLiteral) {
                    return new BooleanLiteral(!((BooleanLiteral) rhs).getValue());
                } else {
                    return null;
                }
            }
        },

        ABS("abs", ARITHMETIC, Operator.ABS) {
            @Override
            LiteralExpression evaluate(final Expression rhs) {
                if (rhs instanceof IntegerLiteral) {
                    return new IntegerLiteral(Math.abs(((IntegerLiteral) rhs).getValue()));
                } else if (rhs instanceof RealLiteral) {
                    return new RealLiteral(Math.abs(((RealLiteral) rhs).getValue()));
                } else {
                    return null;
                }
            }
        };

        private final String text;
        private final ResultType resultType;
        private final Operator operator;

        ImplOperator(final String text, final ResultType resultType, final Operator operator) {
            this.text = text;
            this.resultType = resultType;
            this.operator = operator;
        }

        @Override
        public String toString() {
            return text;
        }

        public Operator getOperator() {
            return operator;
        }

        BasicType getResultType(final Expression rhs) {
            return resultType.getResultType(rhs);
        }

        LiteralExpression evaluate(final Expression rhs) {
            return null;
        }

    }

    public static class OperatorRef extends Positioned {

        public OperatorRef(final Position position, final ImplOperator operator) {
            super(position);
            this.operatorImpl = operator;
        }

        private final ImplOperator operatorImpl;

        Operator getOperator() {
            return operatorImpl.getOperator();
        }

        BasicType getResultType(final Expression rhs) {
            return operatorImpl.getResultType(rhs);
        }

        @Override
        public String toString() {
            return operatorImpl.toString();
        }

        @Override
        public int hashCode() {
            return operatorImpl.hashCode();
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof OperatorRef op) {

                return operatorImpl.equals(op.operatorImpl);
            } else {
                return false;
            }

        }

        LiteralExpression evaluate(final Expression rhs) {
            return operatorImpl.evaluate(rhs);
        }

    }

    public static UnaryExpression create(final OperatorRef operator, final Expression rhs) {
        if (operator == null || rhs == null) {
            return null;
        }

        return new UnaryExpression(operator, rhs);

    }

    private UnaryExpression(final OperatorRef operator, final Expression rhs) {
        super(operator.getPosition());
        this.rhs = rhs;
        this.operator = operator;
    }

    @Override
    public Expression getRhs() {
        return rhs;
    }

    @Override
    public BasicType getType() {
        return operator.getResultType(rhs);
    }

    @Override
    public Operator getOperator() {
        return operator.getOperator();
    }

    @Override
    public String toString() {
        return "(" + operator + " " + rhs + ")";
    }

    @Override
    protected List<Expression> getFindArgumentsInner() {
        return rhs.getFindArguments();
    }

    @Override
    protected List<FindParameterExpression> getFindParametersInner() {
        return rhs.getConcreteFindParameters();
    }

    @Override
    public int getFindAttributeCount() {
        return rhs.getFindAttributeCount();
    }

    @Override
    public Expression getFindSkeletonInner() {
        return new UnaryExpression(operator, rhs.getFindSkeleton());
    }

    @Override
    public int hashCode() {
        return rhs.hashCode() ^ operator.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof UnaryExpression obj2) {

            return rhs.equals(obj2.rhs) && operator.equals(obj2.operator);
        } else {
            return false;
        }
    }

    @Override
    public LiteralExpression evaluate() {
        return operator.evaluate(rhs);
    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitUnaryExpression(this);
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren(rhs);
    }

}
