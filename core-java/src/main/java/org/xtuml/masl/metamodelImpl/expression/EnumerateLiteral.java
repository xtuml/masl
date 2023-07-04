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
import org.xtuml.masl.metamodelImpl.type.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnumerateLiteral extends LiteralExpression
        implements org.xtuml.masl.metamodel.expression.EnumerateLiteral {

    public static class AmbiguousEnumerateLiteral extends LiteralExpression {

        public AmbiguousEnumerateLiteral(final Position position, final List<EnumerateItem> values) {
            super(position);
            for (final EnumerateItem value : values) {
                lookup.put(value.getEnumerate(), value.getReference(position));
            }
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof AmbiguousEnumerateLiteral obj2)) {
                return false;
            } else {

                return lookup.equals(obj2.lookup);
            }
        }

        @Override
        public LiteralExpression resolveInner(final BasicType type) {
            LiteralExpression result = this;

            if (type.getPrimitiveType() instanceof UserDefinedType udt) {
                result = lookup.get(udt.getDefinedType());
            }
            return result;
        }

        @Override
        public BasicType getType() {
            return InternalType.AMBIGUOUS_ENUM;
        }

        private final Map<EnumerateType, EnumerateLiteral> lookup = new HashMap<>();

        @Override
        public int hashCode() {

            return lookup.hashCode();
        }

        @Override
        public String toString() {
            return lookup.values().toArray(new EnumerateLiteral[0])[0].getValue().getName();
        }

        @Override
        public void accept(final ASTNodeVisitor v) {
            throw new IllegalStateException("Cannot visit AmbiguousEnumerateLiteral");
        }
    }

    private final EnumerateItem value;

    public EnumerateLiteral(final Position position, final EnumerateItem value) {
        super(position);
        this.value = value;
    }

    @Override
    public EnumerateItem getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value.getEnumerate().getTypeDeclaration().getDomain().getName() +
               "::" +
               value.getEnumerate().getTypeDeclaration().getName() +
               "." +
               value.getName();
    }

    @Override
    public BasicType getType() {
        return value.getEnumerate().getTypeDeclaration().getDeclaredType();
    }

    @Override
    public int getIndex() {
        return value.getIndex();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof EnumerateLiteral obj2)) {
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
    public void accept(final ASTNodeVisitor v) {
        v.visitEnumerateLiteral(this);
    }

}
