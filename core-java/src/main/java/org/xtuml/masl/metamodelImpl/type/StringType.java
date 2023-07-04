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

import org.xtuml.masl.metamodelImpl.common.Position;

public class StringType extends BuiltinType {

    public static StringType create(final Position position, final boolean anonymous) {
        return new StringType(position, anonymous);
    }

    private static final StringType ANON = new StringType(null, true);

    public static StringType createAnonymous() {
        return ANON;
    }

    private StringType(final Position position, final boolean anonymous) {
        super(position, "string", anonymous);
    }

    @Override
    public SequenceType getPrimitiveType() {
        return SequenceType.createAnonymous(getContainedType());
    }

    @Override
    public StringType getBasicType() {
        return this;
    }

    @Override
    public BasicType getContainedType() {
        return isAnonymousType() ? CharacterType.createAnonymous() : CharacterType.create(null, false);
    }

    @Override
    public ActualType getActualType() {
        return ActualType.STRING;
    }

    @Override
    public boolean isString() {
        return true;
    }

}
