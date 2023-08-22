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
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.type.BasicType;

import java.util.List;

public class CharacteristicRange extends RangeExpression implements org.xtuml.masl.metamodel.CharacteristicRange {

    private final Expression min;
    private final Expression max;
    private final BasicType type;
    private final CharacteristicExpression range;
    private final TypeNameExpression typeName;

    public CharacteristicRange(final CharacteristicExpression range) throws SemanticError {
        super(range.getPosition());
        this.range = range;
        if (range.getCharacteristic() != CharacteristicExpression.Type.RANGE) {
            throw new SemanticError(SemanticErrorCode.ArrayBoundsNotRange, getPosition());
        } else if (range.getLhs() instanceof TypeNameExpression) {
            typeName = (TypeNameExpression) range.getLhs();
            min = typeName.getReferencedType().getMinValue();
            max = typeName.getReferencedType().getMaxValue();

            if (min == null || max == null) {
                throw new SemanticError(SemanticErrorCode.ArrayBoundsNotConstant, getPosition());
            }
        } else {
            throw new SemanticError(SemanticErrorCode.ArrayBoundsNotConstant, getPosition());
        }

        type = range.getType();
    }

    @Override
    public Expression getMin() {
        return min;
    }

    @Override
    public Expression getMax() {
        return max;
    }

    @Override
    public BasicType getType() {
        return type;
    }

    @Override
    public String toString() {
        return range.toString();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof CharacteristicRange rhs)) {
            return false;
        } else {

            return min.equals(rhs.min) && max.equals(rhs.max);
        }
    }

    @Override
    public int hashCode() {

        return min.hashCode() * 31 + max.hashCode();
    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitCharacteristicRange(this);
    }

    @Override
    public TypeNameExpression getTypeName() {
        return typeName;
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren(typeName);
    }

}
