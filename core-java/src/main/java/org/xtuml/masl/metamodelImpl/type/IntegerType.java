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

public class IntegerType extends NumericType {

    public static IntegerType create(final Position position, final boolean anonymous) {
        return new IntegerType(position, anonymous);
    }

    private static final IntegerType ANON = new IntegerType(null, true);

    public static IntegerType createAnonymous() {
        return ANON;
    }

    private IntegerType(final Position position, final boolean anonymous) {
        super(position, "long_integer", true, 64, anonymous);
    }

    @Override
    public IntegerType getPrimitiveType() {
        return IntegerType.createAnonymous();
    }

    @Override
    public IntegerType getBasicType() {
        return this;
    }

    @Override
    protected boolean isConvertibleFromRelaxation(final BasicType rhs) {
        return rhs instanceof RealType || rhs.getDefinedType() instanceof EnumerateType;
    }

    @Override
    public ActualType getActualType() {
        return ActualType.INTEGER;
    }

}
