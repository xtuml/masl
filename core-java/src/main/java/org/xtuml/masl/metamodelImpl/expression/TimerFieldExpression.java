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
import org.xtuml.masl.metamodel.type.TypeDefinition.ActualType;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.type.*;
import org.xtuml.masl.utils.HashCode;

import java.util.ArrayList;
import java.util.List;

public class TimerFieldExpression extends Expression
        implements org.xtuml.masl.metamodel.expression.TimerFieldExpression {

    TimerFieldExpression(final Position position, final Expression lhs, final String characteristic) throws
                                                                                                     SemanticError {
        super(position);
        this.lhs = lhs;

        if (lhs.getType().getBasicType().getActualType() == ActualType.TIMER) {
            field = Field.valueOf(characteristic);
        } else {
            throw new SemanticError(SemanticErrorCode.CharacteristicNotValid, position, characteristic, lhs.getType());
        }

    }

    private TimerFieldExpression(final Position position, final Expression lhs, final Field field) {
        super(position);
        this.lhs = lhs;
        this.field = field;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj != null) {
            if (this == obj) {
                return true;
            }
            if (obj.getClass() == getClass()) {
                final TimerFieldExpression obj2 = ((TimerFieldExpression) obj);
                return lhs.equals(obj2.lhs) && field == obj2.field;
            } else {
                return false;
            }
        }
        return false;
    }

    @Override
    public Field getField() {
        return field;
    }

    @Override
    public int getFindAttributeCount() {
        return lhs.getFindAttributeCount();
    }

    @Override
    public Expression getFindSkeletonInner() {
        return new TimerFieldExpression(getPosition(), lhs.getFindSkeleton(), field);
    }

    @Override
    public Expression getLhs() {
        return lhs;
    }

    @Override
    public BasicType getType() {
        switch (field) {
            case delta:
                return DurationType.createAnonymous();
            case scheduled_at:
                return TimestampType.createAnonymous();
            case expired_at:
                return TimestampType.createAnonymous();
            case expired:
                return BooleanType.createAnonymous();
            case scheduled:
                return BooleanType.createAnonymous();
            case missed:
                return IntegerType.createAnonymous();
            default:
                assert false;
                return null;
        }
    }

    @Override
    public int hashCode() {
        return HashCode.combineHashes(field.hashCode(), lhs.hashCode());
    }

    @Override
    public String toString() {
        return lhs + "'" + field;
    }

    @Override
    protected List<Expression> getFindArgumentsInner() {
        return new ArrayList<>(lhs.getFindArguments());

    }

    @Override
    protected List<FindParameterExpression> getFindParametersInner() {
        return new ArrayList<>(lhs.getConcreteFindParameters());

    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitTimerFieldExpression(this);
    }

    private final Expression lhs;
    private final Field field;

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren(lhs);
    }

}
