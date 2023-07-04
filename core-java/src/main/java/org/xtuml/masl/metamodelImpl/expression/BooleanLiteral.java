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
import org.xtuml.masl.metamodelImpl.type.BooleanType;

public class BooleanLiteral extends LiteralExpression implements org.xtuml.masl.metamodel.expression.BooleanLiteral {

    private final boolean value;

    public BooleanLiteral(final Position position, final boolean value) {
        super(position);
        this.value = value;
    }

    BooleanLiteral(final boolean value) {
        this(null, value);
    }

    @Override
    public String toString() {
        return value ? "true" : "false";
    }

    @Override
    public boolean getValue() {
        return value;
    }

    @Override
    public BasicType getType() {
        return BooleanType.createAnonymous();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof BooleanLiteral obj2)) {
            return false;
        } else {

            return value == obj2.value;
        }
    }

    @Override
    public int hashCode() {
        return value ? 1 : 0;
    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitBooleanLiteral(this);
    }
}
