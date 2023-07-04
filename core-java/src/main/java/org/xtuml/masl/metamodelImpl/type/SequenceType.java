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
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.expression.Expression;
import org.xtuml.masl.utils.HashCode;

import java.util.Objects;

public final class SequenceType extends CollectionType implements org.xtuml.masl.metamodel.type.SequenceType {

    private final Expression bound;

    public static SequenceType create(final Position position,
                                      final BasicType containedType,
                                      final Expression bound,
                                      final boolean anonymous) {
        if (containedType == null) {
            return null;
        }
        return new SequenceType(position, containedType, bound, anonymous);
    }

    public static SequenceType createAnonymous(final BasicType containedType) {
        return new SequenceType(null, containedType, null, true);
    }

    private SequenceType(final Position position,
                         final BasicType containedType,
                         final Expression bound,
                         final boolean anonymous) {
        super(position, containedType, anonymous);
        this.bound = bound;
    }

    @Override
    public Expression getBound() {
        return bound;
    }

    @Override
    public String toString() {
        return (isAnonymousType() ? "anonymous " : "") +
               "sequence " +
               (bound == null ? "" : "(" + bound + ") ") +
               "of " +
               getContainedType();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SequenceType rhs)) {
            return false;
        } else {

            return collEquals(rhs) && (Objects.equals(bound, rhs.bound));
        }
    }

    @Override
    public int hashCode() {
        return HashCode.combineHashes(collHashCode(), (bound != null ? bound.hashCode() : 0));
    }

    @Override
    public SequenceType getBasicType() {
        return new SequenceType(null, getContainedType().getBasicType(), bound, true);
    }

    @Override
    protected boolean isAssignableFromRelaxation(final BasicType rhs) {
        return (rhs instanceof SequenceType && getContainedType().isAssignableFrom(rhs.getContainedType())) ||
               (rhs instanceof RangeType && getContainedType().isAssignableFrom(rhs.getContainedType()));
    }

    @Override
    protected boolean isConvertibleFromRelaxation(final BasicType rhs) {
        return (rhs instanceof SequenceType && getContainedType().isConvertibleFrom(rhs.getContainedType())) ||
               (rhs instanceof RangeType && getContainedType().isConvertibleFrom(rhs.getContainedType()));
    }

    @Override
    public ActualType getActualType() {
        return ActualType.SEQUENCE;
    }

    @Override
    public <R, P> R accept(final ASTNodeVisitor<R, P> v, final P p) throws Exception {
        return v.visitSequenceType(this, p);
    }

}
