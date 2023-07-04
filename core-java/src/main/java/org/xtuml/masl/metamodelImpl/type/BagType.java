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

public final class BagType extends CollectionType implements org.xtuml.masl.metamodel.type.BagType {

    public static BagType create(final Position position, final BasicType containedType, final boolean anonymous) {
        if (containedType == null) {
            return null;
        }

        return new BagType(position, containedType, anonymous);
    }

    public static BagType createAnonymous(final BasicType containedType) {
        return new BagType(null, containedType, true);
    }

    private BagType(final Position position, final BasicType containedType, final boolean anonymous) {
        super(position, containedType, anonymous);
    }

    @Override
    public String toString() {
        return (isAnonymousType() ? "anonymous " : "") + "bag of " + getContainedType();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof BagType rhs)) {
            return false;
        } else {

            return collEquals(rhs);
        }
    }

    @Override
    public int hashCode() {
        return collHashCode();
    }

    @Override
    public BagType getBasicType() {
        return new BagType(null, getContainedType().getBasicType(), true);
    }

    @Override
    public ActualType getActualType() {
        return ActualType.BAG;
    }

    @Override
    public <R, P> R accept(final ASTNodeVisitor<R, P> v, final P p) throws Exception {
        return v.visitBagType(this, p);
    }

}
