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
import org.xtuml.masl.utils.HashCode;

public final class RangeType extends CollectionType {

    public static RangeType createAnonymous(final BasicType containedType) {
        return new RangeType(null, containedType);
    }

    private RangeType(final Position position, final BasicType containedType) {
        super(position, containedType, true);
    }

    @Override
    public String toString() {
        return (isAnonymousType() ? "anonymous " : "") + "range of " + getContainedType();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof RangeType rhs)) {
            return false;
        } else {

            return collEquals(rhs);
        }
    }

    @Override
    public int hashCode() {
        return HashCode.combineHashes(collHashCode());
    }

    @Override
    public RangeType getBasicType() {
        return new RangeType(null, getContainedType().getBasicType());
    }

    @Override
    public ActualType getActualType() {
        return null;
    }

    @Override
    public void accept(final ASTNodeVisitor v) {
    }

}
