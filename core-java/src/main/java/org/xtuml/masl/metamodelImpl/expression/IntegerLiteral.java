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
package org.xtuml.masl.metamodelImpl.expression;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.type.BasicType;
import org.xtuml.masl.metamodelImpl.type.DurationType;
import org.xtuml.masl.metamodelImpl.type.IntegerType;

public class IntegerLiteral extends NumericLiteral implements org.xtuml.masl.metamodel.expression.IntegerLiteral {

    final private String text;

    final private Long value;

    public IntegerLiteral(final Position position, final String text) {
        super(position);
        this.text = text;
        final String[] components = text.split("#");

        if (components.length == 1) {
            // Normal numeric literal
            this.value = Long.parseLong(text);
        } else {
            // Based literal
            final int radix = Integer.parseInt(components[0]);
            value = Long.parseLong(components[1], radix);
        }
    }

    public IntegerLiteral(final Long value) {
        super(null);
        this.text = String.valueOf(value);
        this.value = value;
    }

    @Override
    public Long getValue() {
        return value;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return text;
    }

    @Override
    public BasicType getType() {
        return IntegerType.createAnonymous();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof IntegerLiteral obj2)) {
            return false;
        } else {

            return value.equals(obj2.value);
        }
    }

    @Override
    public int hashCode() {

        return value.hashCode();
    }

    @Override
    protected Expression resolveInner(final BasicType requiredType) {
        if (requiredType.getPrimitiveType() instanceof DurationType && value == 0) {
            return new DurationLiteral(getPosition(), this);
        }
        return this;
    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitIntegerLiteral(this);
    }

}
