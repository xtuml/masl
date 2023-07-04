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

public class UnconstrainedArrayType extends FullTypeDefinition
        implements org.xtuml.masl.metamodel.type.UnconstrainedArrayType {

    private final BasicType containedType;
    private final BasicType indexType;

    public UnconstrainedArrayType(final Position position, final BasicType containedType, final BasicType indexType) {
        super(position);
        this.containedType = containedType;
        this.indexType = indexType;
        primitive = SequenceType.createAnonymous(containedType);
    }

    @Override
    public BasicType getContainedType() {
        return containedType;
    }

    @Override
    public BasicType getIndexType() {
        return indexType;
    }

    @Override
    public String toString() {
        return "array (" + indexType + " range<>) of " + containedType;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof UnconstrainedArrayType rhs)) {
            return false;
        } else {

            return containedType.equals(rhs.containedType) && indexType.equals(rhs.indexType);
        }
    }

    @Override
    public int hashCode() {

        return containedType.hashCode() * 31 + indexType.hashCode();
    }

    @Override
    public SequenceType getPrimitiveType() {
        return primitive;
    }

    @Override
    public ActualType getActualType() {
        return ActualType.UNCONSTRAINED_ARRAY;
    }

    private final SequenceType primitive;

    @Override
    public <R, P> R accept(final ASTNodeVisitor<R, P> v, final P p) throws Exception {
        return v.visitUnconstrainedArrayType(this, p);
    }

}
