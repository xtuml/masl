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
import org.xtuml.masl.metamodelImpl.type.RangeType;

public class MinMaxRange extends RangeExpression implements org.xtuml.masl.metamodel.expression.MinMaxRange {

    private final Expression min;
    private final Expression max;
    private final RangeType type;

    public MinMaxRange(final Expression min, final Expression max) {
        super(min.getPosition());
        this.min = min;
        this.max = max;
        type = RangeType.createAnonymous(max.getType());
    }

    @Override
    public Expression getMin() {
        return min;
    }

    @Override
    public Expression getMax() {
        return max;
    }

    @Override
    public RangeType getType() {
        return type;
    }

    @Override
    public String toString() {
        return min + ".." + max;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof MinMaxRange rhs)) {
            return false;
        } else {

            return min.equals(rhs.min) && max.equals(rhs.max);
        }
    }

    @Override
    public int hashCode() {

        return min.hashCode() * 31 + max.hashCode();
    }

    @Override
    public <R, P> R accept(final ASTNodeVisitor<R, P> v, final P p) throws Exception {
        return v.visitMinMaxRange(this, p);
    }

}
