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
import org.xtuml.masl.metamodelImpl.expression.Expression;

public class DeltaConstraint extends TypeConstraint implements org.xtuml.masl.metamodel.type.DeltaConstraint {

    private final Expression delta;

    public DeltaConstraint(final Expression delta, final RangeConstraint range) {
        super(range.getRange());
        this.delta = delta;
    }

    @Override
    public Expression getDelta() {
        return delta;
    }

    @Override
    public String toString() {
        return "delta " + delta + " range " + range;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof DeltaConstraint rhs)) {
            return false;
        } else {

            return super.equals(obj) && delta.equals(rhs.delta);
        }
    }

    @Override
    public int hashCode() {

        return super.hashCode() * 31 + delta.hashCode();
    }

    @Override
    public <R, P> R accept(final ASTNodeVisitor<R, P> v, final P p) throws Exception {
        return v.visitDeltaConstraint(this, p);
    }

}
