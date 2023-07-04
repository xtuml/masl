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
import org.xtuml.masl.metamodelImpl.expression.Expression;
import org.xtuml.masl.metamodelImpl.expression.IntegerLiteral;

import java.math.BigInteger;

public abstract class NumericType extends BuiltinType {

    static final BigInteger ZERO = BigInteger.ZERO;
    static final BigInteger ONE = BigInteger.ONE;
    static final BigInteger TWO = BigInteger.valueOf(2);

    private NumericType(final Position position,
                        final String name,
                        final Expression min,
                        final Expression max,
                        final boolean anonymous) {
        super(position, name, anonymous);
        this.min = min;
        this.max = max;
    }

    protected NumericType(final Position position, final String name, final boolean anonymous) {
        this(position, name, null, null, anonymous);
    }

    protected NumericType(final Position position,
                          final String name,
                          final boolean signed,
                          final int bits,
                          final boolean anonymous) {

        super(position, name, anonymous);
        if (signed) {
            final BigInteger max = TWO.pow(bits - 1);
            final BigInteger min = max.negate().subtract(ONE);
            this.max = new IntegerLiteral(max.longValue());
            this.min = new IntegerLiteral(min.longValue());
        } else {
            final BigInteger max = TWO.pow(bits);
            final BigInteger min = ZERO;
            this.max = new IntegerLiteral(max.longValue());
            this.min = new IntegerLiteral(min.longValue());
        }
    }

    private final Expression min;
    private final Expression max;

    @Override
    public Expression getMinValue() {
        return min;
    }

    @Override
    public Expression getMaxValue() {
        return max;
    }

    @Override
    abstract public NumericType getPrimitiveType();

    @Override
    abstract public NumericType getBasicType();

    @Override
    public boolean isNumeric() {
        return true;
    }

}
