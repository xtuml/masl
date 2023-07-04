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
import org.xtuml.masl.metamodelImpl.type.BooleanType;

public class BinaryLogicalExpression extends BinaryExpression {

    public BinaryLogicalExpression(Expression lhs, final OperatorRef operator, Expression rhs) throws SemanticError {
        super(lhs.getPosition(), operator);

        rhs = rhs.resolve(lhs.getType());
        lhs = lhs.resolve(rhs.getType());

        setLhs(lhs);
        setRhs(rhs);

        if (!BooleanType.createAnonymous().isAssignableFrom(getLhs())) {
            throw new SemanticError(SemanticErrorCode.ExpectedBooleanCondition,
                                    getLhs().getPosition(),
                                    getLhs().getType());
        }

        if (!BooleanType.createAnonymous().isAssignableFrom(getRhs())) {
            throw new SemanticError(SemanticErrorCode.ExpectedBooleanCondition,
                                    getRhs().getPosition(),
                                    getRhs().getType());
        }

    }

    @Override
    public BooleanLiteral evaluate() {
        final LiteralExpression lhsVal = getLhs().evaluate();
        final LiteralExpression rhsVal = getRhs().evaluate();

        if (lhsVal instanceof BooleanLiteral && rhsVal instanceof BooleanLiteral) {
            final boolean lhsBool = ((BooleanLiteral) lhsVal).getValue();
            final boolean rhsBool = ((BooleanLiteral) rhsVal).getValue();
            switch (getOperator()) {
                case AND:
                    return new BooleanLiteral(lhsBool && rhsBool);
                case OR:
                    return new BooleanLiteral(lhsBool || rhsBool);
                case XOR:
                    return new BooleanLiteral(lhsBool ^ rhsBool);
                default:
                    assert false : "Invalid logical operator " + getOperator();
            }
        }

        return null;
    }

    @Override
    public BasicType getType() {
        return BooleanType.createAnonymous();
    }

}
