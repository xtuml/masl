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
import org.xtuml.masl.metamodelImpl.type.BasicType;
import org.xtuml.masl.metamodelImpl.type.RealType;
import org.xtuml.masl.utils.HashCode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateDurationExpression extends Expression
        implements org.xtuml.masl.metamodel.expression.CreateDurationExpression {

    private final static Map<String, Field> fieldLookup = new HashMap<>();

    static {
        fieldLookup.put("weeks", Field.Weeks);
        fieldLookup.put("days", Field.Days);

        fieldLookup.put("hours", Field.Hours);
        fieldLookup.put("minutes", Field.Minutes);
        fieldLookup.put("seconds", Field.Seconds);

        fieldLookup.put("milliseconds", Field.Millis);
        fieldLookup.put("microseconds", Field.Micros);
        fieldLookup.put("nanoseconds", Field.Nanos);
    }

    CreateDurationExpression(final Position position,
                             final Expression lhs,
                             final String characteristic,
                             final Expression argument) throws SemanticError {
        super(position);
        this.argument = argument;
        this.characteristic = characteristic;

        field = decodeField(characteristic);

        if (!(lhs instanceof TypeNameExpression)) {
            throw new SemanticError(SemanticErrorCode.CharacteristicNotValid, position, characteristic, lhs.getType());
        } else if (((TypeNameExpression) lhs).getReferencedType().getBasicType().getActualType() !=
                   ActualType.DURATION) {
            throw new SemanticError(SemanticErrorCode.CharacteristicNotValid, position, characteristic, lhs);
        }
        this.lhs = ((TypeNameExpression) lhs).getReferencedType();

        RealType.createAnonymous().checkAssignable(argument);

    }

    private CreateDurationExpression(final Position position,
                                     final BasicType lhs,
                                     final String characteristic,
                                     final Field field,
                                     final Expression argument) {
        super(position);
        this.characteristic = characteristic;
        this.lhs = lhs;
        this.argument = argument;
        this.field = field;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj != null) {
            if (this == obj) {
                return true;
            }
            if (obj.getClass() == getClass()) {
                final CreateDurationExpression obj2 = ((CreateDurationExpression) obj);
                return lhs.equals(obj2.lhs) && argument.equals(obj2.lhs) && characteristic == obj2.characteristic;
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
        return argument.getFindAttributeCount();
    }

    @Override
    public Expression getFindSkeletonInner() {
        return new CreateDurationExpression(getPosition(), lhs, characteristic, field, argument.getFindSkeleton());
    }

    @Override
    public Expression getArgument() {
        return argument;
    }

    @Override
    public BasicType getType() {
        return lhs;
    }

    @Override
    public int hashCode() {
        return HashCode.combineHashes(field.hashCode(), argument.hashCode());
    }

    @Override
    public String toString() {
        return lhs + "'" + characteristic + "(" + argument + ")";
    }

    @Override
    protected List<Expression> getFindArgumentsInner() {
        return new ArrayList<>(argument.getFindArguments());

    }

    @Override
    protected List<FindParameterExpression> getFindParametersInner() {
        return new ArrayList<>(argument.getConcreteFindParameters());

    }

    private Field decodeField(final String name) throws SemanticError {
        final Field field = fieldLookup.get(name);
        if (field == null) {
            throw new SemanticError(SemanticErrorCode.CharacteristicNotValid, getPosition(), name, argument.getType());
        }
        return field;
    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitCreateDurationExpression(this);
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren(argument);
    }

    private final Expression argument;
    private final BasicType lhs;
    private final String characteristic;

    private final Field field;
}
