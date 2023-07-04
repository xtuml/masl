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
import org.xtuml.masl.metamodelImpl.type.IntegerType;
import org.xtuml.masl.metamodelImpl.type.RealType;

public class BinaryMultiplicativeExpression extends BinaryExpression {

    public BinaryMultiplicativeExpression(Expression lhs, final OperatorRef operator, Expression rhs) throws
                                                                                                      SemanticError {
        super(lhs.getPosition(), operator);

        rhs = rhs.resolve(lhs.getType());
        lhs = lhs.resolve(rhs.getType());

        setLhs(lhs);
        setRhs(rhs);

        final BasicType lhsType = getLhs().getType();
        final BasicType rhsType = getRhs().getType();

        final boolean lhsAnon = lhsType.isAnonymousType();
        final boolean rhsAnon = rhsType.isAnonymousType();

        final boolean lhsReal = lhsType.getPrimitiveType().equals(RealType.createAnonymous());
        final boolean rhsReal = rhsType.getPrimitiveType().equals(RealType.createAnonymous());

        if (RealType.createAnonymous().isAssignableFrom(getLhs()) &&
            RealType.createAnonymous().isAssignableFrom(getRhs())) {
            if (lhsAnon && !rhsAnon && rhsType.isAssignableFrom(getLhs())) {
                resultType = rhsType;
            } else if (rhsAnon && !lhsAnon && lhsType.isAssignableFrom(getRhs())) {
                resultType = lhsType;
            } else if (!rhsAnon && !lhsAnon && lhsType.isAssignableFrom(getRhs())) {
                // Neither anonymous, so both must be same type, so return lhs. Arguably
                // this should return an anonymous type, as multiplying something by
                // itself may not give the same result, eg length * length = area.
                resultType = lhsType;
            } else {
                resultType = lhsReal || rhsReal ? RealType.createAnonymous() : IntegerType.createAnonymous();
            }
        } else if (RealType.createAnonymous().isAssignableFrom(getLhs()) &&
                   DurationType.createAnonymous().isAssignableFrom(getRhs()) &&
                   operator.getOperator() == BinaryExpression.Operator.TIMES) {
            resultType = rhsType;
        } else if (DurationType.createAnonymous().isAssignableFrom(getLhs()) &&
                   RealType.createAnonymous().isAssignableFrom(getRhs()) &&
                   (operator.getOperator() == BinaryExpression.Operator.TIMES ||
                    operator.getOperator() == BinaryExpression.Operator.DIVIDE)) {
            resultType = lhsType;
        } else if (DurationType.createAnonymous().isAssignableFrom(getLhs()) &&
                   DurationType.createAnonymous().isAssignableFrom(getRhs()) &&
                   operator.getOperator() == BinaryExpression.Operator.DIVIDE) {
            resultType = RealType.createAnonymous();
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
                    case TIMES:
                        return new RealLiteral(lhsNum * rhsNum);
                    case DIVIDE:
                        return new RealLiteral(lhsNum / rhsNum);
                    case REM:
                        return new RealLiteral(Math.IEEEremainder(lhsNum, rhsNum));
                    case MOD: {
                        final double rem = Math.IEEEremainder(lhsNum, rhsNum);

                        return new RealLiteral(rem +
                                               (rem != 0 &&
                                                (((lhsNum < 0) && (rhsNum > 0)) || ((lhsNum > 0) && (rhsNum < 0))) ?
                                                rhsNum :
                                                0));
                    }
                    default:
                        assert false : "Invalid multiplicative operator " + getOperator();
                }
            } else {
                final long lhsNum = ((NumericLiteral) lhsVal).getValue().longValue();
                final long rhsNum = ((NumericLiteral) rhsVal).getValue().longValue();

                switch (getOperator()) {
                    case TIMES:
                        return new IntegerLiteral(lhsNum * rhsNum);
                    case DIVIDE:
                        return new IntegerLiteral(lhsNum / rhsNum);
                    case REM:
                        return new IntegerLiteral(lhsNum % rhsNum);
                    case MOD: {
                        final long rem = lhsNum % rhsNum;
                        return new IntegerLiteral(rem +
                                                  (rem != 0 &&
                                                   (((lhsNum < 0) && (rhsNum > 0)) || ((lhsNum > 0) && (rhsNum < 0))) ?
                                                   rhsNum :
                                                   0));
                    }
                    default:
                        assert false : "Invalid multiplicative operator " + getOperator();
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
