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
import org.xtuml.masl.metamodelImpl.common.Position;

import java.util.List;

abstract public class CollectionType extends BasicType implements org.xtuml.masl.metamodel.type.CollectionType {

    private final BasicType containedType;

    public CollectionType(final Position position, final BasicType containedType, final boolean anonymous) {
        super(position, anonymous);
        this.containedType = containedType;
    }

    @Override
    public BasicType getContainedType() {
        return containedType;
    }

    @Override
    public String toString() {
        return (isAnonymousType() ? "anonymous " : "") + "collection of " + containedType;
    }

    protected final boolean collEquals(final CollectionType rhs) {
        return containedType.equals(rhs.containedType);
    }

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public abstract int hashCode();

    protected int collHashCode() {

        return containedType.hashCode();
    }

    @Override
    abstract public CollectionType getBasicType();

    @Override
    public SequenceType getPrimitiveType() {
        return SequenceType.createAnonymous(getContainedType());
    }

    @Override
    public boolean isCollection() {
        return true;
    }

    @Override
    public void checkCanBePublic() {
        containedType.checkCanBePublic();
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren(containedType);
    }

}
