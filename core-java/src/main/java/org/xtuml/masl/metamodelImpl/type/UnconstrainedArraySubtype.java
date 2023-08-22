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

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.expression.Expression;
import org.xtuml.masl.metamodelImpl.expression.RangeExpression;

public class UnconstrainedArraySubtype extends BasicType
        implements org.xtuml.masl.metamodel.type.UnconstrainedArraySubtype {

    private final UserDefinedType fullType;
    private final Expression range;

    public static UnconstrainedArraySubtype create(final UserDefinedType containedType, final Expression range) {
        if (containedType == null || range == null) {
            return null;
        }

        return new UnconstrainedArraySubtype(containedType, range, false);
    }

    private UnconstrainedArraySubtype(final UserDefinedType fullType, final Expression range, final boolean anonymous) {
        super(fullType.getPosition(), anonymous);
        this.fullType = fullType;
        this.range = range;
    }

    @Override
    public UserDefinedType getFullType() {
        return fullType;
    }

    @Override
    public RangeExpression getRange() {
        return (RangeExpression) range;
    }

    @Override
    public String toString() {
        return fullType + "(" + range + ")";
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof UnconstrainedArraySubtype rhs)) {
            return false;
        } else {

            return fullType.equals(rhs.fullType) && range.equals(rhs.range);
        }
    }

    @Override
    public int hashCode() {

        return fullType.hashCode() * 31 + range.hashCode();
    }

    @Override
    public SequenceType getPrimitiveType() {
        return (SequenceType) fullType.getPrimitiveType();
    }

    @Override
    public UnconstrainedArraySubtype getBasicType() {
        return new UnconstrainedArraySubtype((UserDefinedType) fullType.getBasicType(), range, true);
    }

    @Override
    public ActualType getActualType() {
        return ActualType.UNCONSTRAINED_ARRAY_SUBTYPE;
    }

    @Override
    public <R, P> R accept(final ASTNodeVisitor<R, P> v, final P p) throws Exception {
        return v.visitUnconstrainedArraySubtype(this, p);
    }

}
