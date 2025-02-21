/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.metamodelImpl.expression;

import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.type.*;

import java.util.ArrayList;
import java.util.List;

public class BinaryCollectionExpression extends BinaryExpression {

    public BinaryCollectionExpression(Expression lhs, final OperatorRef operator, Expression rhs) throws SemanticError {
        super(lhs.getPosition(), operator);

        final List<BasicType> lhsTypes = new ArrayList<>();
        BasicType lhsContained = lhs.getType();
        while (lhsContained.getContainedType() != null) {
            lhsTypes.add(lhsContained.getBasicType());
            lhsContained = lhsContained.getContainedType();
        }

        final List<BasicType> rhsTypes = new ArrayList<>();
        BasicType rhsContained = rhs.getType();
        while (rhsContained.getContainedType() != null) {
            rhsTypes.add(rhsContained.getBasicType());
            rhsContained = rhsContained.getContainedType();
        }

        // Make sure at least one deep
        if (lhsTypes.size() == 0 && rhsTypes.size() == 0) {
            final BasicType anonSeq = SequenceType.createAnonymous(lhs.getType());
            lhsTypes.add(anonSeq);
            lhs = lhs.resolve(anonSeq);
        }

        for (int i = lhsTypes.size(); i < rhsTypes.size(); ++i) {
            lhs = calcType(lhs, rhsTypes.get(rhsTypes.size() - i - 1));
        }

        final boolean allowRhsNonColl = operator.getOperator() == BinaryExpression.Operator.CONCATENATE;

        for (int i = rhsTypes.size(); i < lhsTypes.size(); ++i) {
            if (i == lhsTypes.size() - 1 && allowRhsNonColl) {
                rhs = rhs.resolve(lhsTypes.get(lhsTypes.size() - i - 1).getContainedType());
            } else {
                rhs = calcType(rhs, lhsTypes.get(lhsTypes.size() - i - 1));
            }
        }

        setLhs(lhs);
        setRhs(rhs);

        final BasicType lhsType = getLhs().getType();
        final BasicType rhsType = getRhs().getType();

        final boolean leftToRight;

        if (allowRhsNonColl &&
            lhsType.getContainedType() != null &&
            lhsType.getContainedType().isAssignableFrom(getRhs())) {
            final BasicType rhsCollType = calcType(rhs, lhsTypes.get(0)).getType();
            resultType = isLeftType(lhsType, rhsCollType, true) ? lhsType : rhsCollType;
        } else {
            if (lhsType.isAssignableFrom(getRhs())) {
                leftToRight = true;
            } else if (rhsType.isAssignableFrom(getLhs())) {
                leftToRight = false;
            } else {
                throw new SemanticError(SemanticErrorCode.OperatorOperandsNotCompatible,
                                        getOperatorRef().getPosition(),
                                        lhsType,
                                        rhsType,
                                        getOperatorRef());
            }

            resultType = isLeftType(lhsType, rhsType, leftToRight) ? lhsType : rhsType;
        }
    }

    private Expression calcType(final Expression curType, final BasicType reqdType) {
        Expression newType = curType;
        if (reqdType instanceof SequenceType) {
            newType = curType.resolve(SequenceType.createAnonymous(curType.getType()));
        } else if (reqdType instanceof SetType) {
            newType = curType.resolve(SetType.createAnonymous(curType.getType()));
        } else if (reqdType instanceof BagType) {
            newType = curType.resolve(BagType.createAnonymous(curType.getType()));
        } else if (reqdType instanceof StringType) {
            if (curType.getType().isAnonymousType()) {
                newType = curType.resolve(StringType.createAnonymous());
            } else if (curType.getType() instanceof CharacterType) {
                newType = curType.resolve(StringType.create(null, false));
            } else {
                newType = curType.resolve(SequenceType.createAnonymous(curType.getType()));
            }
        }
        return newType;
    }

    private boolean isLeftType(final BasicType lhsType, final BasicType rhsType, final boolean leftToRight) {

        if (rhsType == null) {
            return lhsType != null || leftToRight;
        }

        final boolean lhsAnon = lhsType.isAnonymousType();
        final boolean rhsAnon = rhsType.isAnonymousType();

        if (lhsAnon && rhsAnon) {
            return isLeftType(lhsType.getContainedType(), rhsType.getContainedType(), leftToRight);
        } else {
            return !lhsAnon;
        }

    }

    @Override
    public LiteralExpression evaluate() {
        return null;
    }

    @Override
    public BasicType getType() {
        return resultType;
    }

    private final BasicType resultType;

}
