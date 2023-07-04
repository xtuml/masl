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
package org.xtuml.masl.metamodelImpl.type;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.expression.Expression;

import java.util.List;

public class ConstrainedType extends FullTypeDefinition implements org.xtuml.masl.metamodel.type.ConstrainedType {

    private final BasicType fullType;
    private final TypeConstraint constraint;

    public ConstrainedType(final BasicType fullType, final TypeConstraint constraint) {
        super(fullType.getPosition());
        this.fullType = fullType;
        this.constraint = constraint;
    }

    @Override
    public BasicType getFullType() {
        return fullType;
    }

    @Override
    public TypeConstraint getConstraint() {
        return constraint;
    }

    @Override
    public String toString() {
        return fullType + " " + constraint;
    }

    @Override
    public Expression getMinValue() {
        return constraint.getRange().getMin();
    }

    @Override
    public Expression getMaxValue() {
        return constraint.getRange().getMax();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ConstrainedType rhs)) {
            return false;
        } else {

            return fullType.equals(rhs.fullType) && constraint.equals(rhs.constraint);
        }
    }

    @Override
    public int hashCode() {
        return fullType.hashCode() * 31 + constraint.hashCode();
    }

    @Override
    public BasicType getPrimitiveType() {
        return fullType.getPrimitiveType();
    }

    @Override
    public ActualType getActualType() {
        return ActualType.CONSTRAINED;
    }

    @Override
    public void checkCanBePublic() {
        fullType.checkCanBePublic();
    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitConstrainedType(this);
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren(fullType, constraint);
    }

}
