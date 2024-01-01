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

import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.type.*;

import java.util.HashSet;
import java.util.Set;

public class BinaryComparisonExpression extends BinaryExpression {

    public BinaryComparisonExpression(Expression lhs, final OperatorRef operator, Expression rhs) throws SemanticError {
        super(lhs.getPosition(), operator);

        rhs = rhs.resolve(lhs.getType());
        lhs = lhs.resolve(rhs.getType());

        setLhs(lhs);
        setRhs(rhs);


        Set<BasicType> checked = new HashSet<>();
        checkOperand(getLhs().getType(), getLhs().getPosition(),checked);
        checkOperand(getRhs().getType(), getRhs().getPosition(),checked);

        if (!(getLhs().getType().isAssignableFrom(getRhs()) || getRhs().getType().isAssignableFrom(getLhs()))) {
            throw new SemanticError(SemanticErrorCode.OperatorOperandsNotCompatible,
                                    getOperatorRef().getPosition(),
                                    getLhs().getType(),
                                    getRhs().getType(),
                                    getOperatorRef());
        }

    }

    private void checkOperand(final BasicType opType, final Position position, Set<BasicType> checked) throws SemanticError {

        // Keep a track of what we've already checked in case of recursive types.
        if ( checked.contains(opType)) {
            return;
        } else {
            checked.add(opType);
        }

        if (!(RealType.createAnonymous().isConvertibleFrom(opType) ||
              DurationType.createAnonymous().isConvertibleFrom(opType) ||
              TimestampType.createAnonymous().isConvertibleFrom(opType) ||
              WCharacterType.createAnonymous().isConvertibleFrom(opType) ||
              WStringType.createAnonymous().isConvertibleFrom(opType) ||
              (opType.getPrimitiveType() instanceof UserDefinedType &&
               opType.getPrimitiveType().getDefinedType() instanceof EnumerateType))) {
            if (opType.getPrimitiveType() instanceof AnonymousStructure struct) {
                for (final BasicType elt : struct.getElements()) {
                    checkOperand(elt, position,checked);
                }
            } else if (opType.getPrimitiveType() instanceof SequenceType) {
                checkOperand(opType.getContainedType(), position,checked);
            } else if (opType.getPrimitiveType() instanceof DictionaryType) {
                checkOperand(((DictionaryType) opType.getPrimitiveType()).getKeyType(), position,checked);
                checkOperand(((DictionaryType) opType.getPrimitiveType()).getValueType(), position,checked);
            } else if (!((getOperator() == Operator.EQUAL || getOperator() == Operator.NOT_EQUAL) &&
                         (AnyInstanceType.createAnonymous().isConvertibleFrom(opType)) ||
                         BooleanType.createAnonymous().isConvertibleFrom(opType))) {
                throw new SemanticError(SemanticErrorCode.ComparisonNotValidForType,
                                        position,
                                        opType,
                                        getOperatorRef());
            }
        }

    }

    @Override
    public BooleanLiteral evaluate() {
        final LiteralExpression lhsVal = getLhs().evaluate();
        final LiteralExpression rhsVal = getRhs().evaluate();

        if (lhsVal instanceof NumericLiteral && rhsVal instanceof NumericLiteral) {
            if (lhsVal instanceof RealLiteral || rhsVal instanceof RealLiteral) {
                final double lhsNum = ((NumericLiteral) lhsVal).getValue().doubleValue();
                final double rhsNum = ((NumericLiteral) rhsVal).getValue().doubleValue();

                switch (getOperator()) {
                    case LESS_THAN:
                        return new BooleanLiteral(lhsNum < rhsNum);
                    case LESS_THAN_OR_EQUAL:
                        return new BooleanLiteral(lhsNum <= rhsNum);
                    case GREATER_THAN:
                        return new BooleanLiteral(lhsNum > rhsNum);
                    case GREATER_THAN_OR_EQUAL:
                        return new BooleanLiteral(lhsNum >= rhsNum);
                    case EQUAL:
                        return new BooleanLiteral(lhsNum == rhsNum);
                    case NOT_EQUAL:
                        return new BooleanLiteral(lhsNum != rhsNum);
                    default:
                        assert false : "Invalid comparison operator " + getOperator();
                }
            } else {
                final long lhsNum = ((NumericLiteral) lhsVal).getValue().longValue();
                final long rhsNum = ((NumericLiteral) rhsVal).getValue().longValue();

                switch (getOperator()) {
                    case LESS_THAN:
                        return new BooleanLiteral(lhsNum < rhsNum);
                    case LESS_THAN_OR_EQUAL:
                        return new BooleanLiteral(lhsNum <= rhsNum);
                    case GREATER_THAN:
                        return new BooleanLiteral(lhsNum > rhsNum);
                    case GREATER_THAN_OR_EQUAL:
                        return new BooleanLiteral(lhsNum >= rhsNum);
                    case EQUAL:
                        return new BooleanLiteral(lhsNum == rhsNum);
                    case NOT_EQUAL:
                        return new BooleanLiteral(lhsNum != rhsNum);
                    default:
                        assert false : "Invalid comparison operator " + getOperator();
                }
            }
        } else if ((lhsVal instanceof StringLiteral || lhsVal instanceof CharacterLiteral) &&
                   (rhsVal instanceof StringLiteral || rhsVal instanceof CharacterLiteral)) {
            final String
                    lhsStr =
                    (lhsVal instanceof StringLiteral) ?
                    ((StringLiteral) lhsVal).getValue() :
                    (String.valueOf(((CharacterLiteral) lhsVal).getValue()));
            final String
                    rhsStr =
                    (rhsVal instanceof StringLiteral) ?
                    ((StringLiteral) rhsVal).getValue() :
                    (String.valueOf(((CharacterLiteral) rhsVal).getValue()));

            switch (getOperator()) {
                case LESS_THAN:
                    return new BooleanLiteral(lhsStr.compareTo(rhsStr) < 0);
                case LESS_THAN_OR_EQUAL:
                    return new BooleanLiteral(lhsStr.compareTo(rhsStr) <= 0);
                case GREATER_THAN:
                    return new BooleanLiteral(lhsStr.compareTo(rhsStr) > 0);
                case GREATER_THAN_OR_EQUAL:
                    return new BooleanLiteral(lhsStr.compareTo(rhsStr) >= 0);
                case EQUAL:
                    return new BooleanLiteral(lhsStr.compareTo(rhsStr) == 0);
                case NOT_EQUAL:
                    return new BooleanLiteral(lhsStr.compareTo(rhsStr) != 0);
                default:
                    assert false : "Invalid comparison operator " + getOperator();
            }
        }
        return null;
    }

    @Override
    public BasicType getType() {
        return BooleanType.createAnonymous();
    }

}
