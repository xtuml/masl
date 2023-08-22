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

import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.type.BasicType;
import org.xtuml.masl.metamodelImpl.type.DurationType;
import org.xtuml.masl.metamodelImpl.type.RealType;
import org.xtuml.masl.metamodelImpl.type.TimestampType;

public class BinaryAdditiveExpression extends BinaryExpression {

    public BinaryAdditiveExpression(Expression lhs, final OperatorRef operator, Expression rhs) throws SemanticError {
        super(lhs.getPosition(), operator);

        rhs = rhs.resolve(lhs.getType());
        lhs = lhs.resolve(rhs.getType());

        setLhs(lhs);
        setRhs(rhs);

        final BasicType lhsType = getLhs().getType();
        final BasicType rhsType = getRhs().getType();

        final boolean lhsAnon = lhsType.isAnonymousType();
        final boolean rhsAnon = rhsType.isAnonymousType();

        if (TimestampType.createAnonymous().isAssignableFrom(getLhs()) &&
            TimestampType.createAnonymous().isAssignableFrom(getRhs())) {
            // timestamp + timestamp : invalid
            // timestamp - timestamp : duration
            if (getOperator() == BinaryExpression.Operator.PLUS) {
                resultType = null;
            } else {
                resultType = DurationType.createAnonymous();
            }
        } else if (TimestampType.createAnonymous().isAssignableFrom(getLhs()) &&
                   DurationType.createAnonymous().isAssignableFrom(getRhs())) {
            // timestamp + duration : timestamp
            // timestamp - duration : timestamp
            resultType = lhsType;
        } else if (DurationType.createAnonymous().isAssignableFrom(getLhs()) &&
                   TimestampType.createAnonymous().isAssignableFrom(getRhs())) {
            // duration + timestamp : timestamp
            // duration - timestamp : invalid
            if (getOperator() == BinaryExpression.Operator.PLUS) {
                resultType = rhsType;
            } else {
                resultType = null;
            }
        } else if (RealType.createAnonymous().isAssignableFrom(getLhs()) &&
                   RealType.createAnonymous().isAssignableFrom(getRhs()) ||
                   DurationType.createAnonymous().isAssignableFrom(getLhs()) &&
                   DurationType.createAnonymous().isAssignableFrom(getRhs())) {
            if (lhsAnon) {
                if (rhsAnon) {
                    if (lhsType.isAssignableFrom(getRhs())) {
                        resultType = lhsType;
                    } else if (rhsType.isAssignableFrom(getLhs())) {
                        resultType = rhsType;
                    } else {
                        resultType = null;
                    }
                } else {
                    resultType = rhsType.isAssignableFrom(getLhs()) ? rhsType : null;
                }
            } else {
                resultType = lhsType.isAssignableFrom(getRhs()) ? lhsType : null;
            }
        } else {
            resultType = null;
        }

        if (resultType == null) {
            throw new SemanticError(SemanticErrorCode.OperatorOperandsNotCompatible,
                                    getOperatorRef().getPosition(),
                                    lhsType,
                                    rhsType,
                                    getOperatorRef());
        }

    }

    @Override
    public NumericLiteral evaluate() {
        final LiteralExpression lhsVal = getLhs().evaluate();
        final LiteralExpression rhsVal = getRhs().evaluate();

        if (lhsVal instanceof NumericLiteral && rhsVal instanceof NumericLiteral) {
            if (lhsVal instanceof RealLiteral || rhsVal instanceof RealLiteral) {
                final double lhsNum = ((NumericLiteral) lhsVal).getValue().doubleValue();
                final double rhsNum = ((NumericLiteral) rhsVal).getValue().doubleValue();

                switch (getOperator()) {
                    case PLUS:
                        return new RealLiteral(lhsNum + rhsNum);
                    case MINUS:
                        return new RealLiteral(lhsNum - rhsNum);
                    default:
                        assert false : "Invalid additive operator " + getOperator();
                }
            } else {
                final long lhsNum = ((NumericLiteral) lhsVal).getValue().longValue();
                final long rhsNum = ((NumericLiteral) rhsVal).getValue().longValue();

                switch (getOperator()) {
                    case PLUS:
                        return new IntegerLiteral(lhsNum + rhsNum);
                    case MINUS:
                        return new IntegerLiteral(lhsNum - rhsNum);
                    default:
                        assert false : "Invalid additive operator " + getOperator();
                }
            }
        }
        return null;
    }

    @Override
    public BasicType getType() {
        return resultType;
    }

    private final BasicType resultType;

}
