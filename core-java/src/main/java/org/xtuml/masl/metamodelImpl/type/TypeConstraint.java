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
import org.xtuml.masl.metamodelImpl.expression.Expression;
import org.xtuml.masl.metamodelImpl.expression.RangeExpression;

import java.util.List;

public abstract class TypeConstraint implements org.xtuml.masl.metamodel.type.TypeConstraint {

    protected final Expression range;

    public TypeConstraint(final Expression range) {
        this.range = range;
    }

    @Override
    public RangeExpression getRange() {
        return (RangeExpression) range;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof TypeConstraint rhs)) {
            return false;
        } else {

            return range.equals(rhs.range);
        }
    }

    @Override
    public int hashCode() {
        return range.hashCode();
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren(range);
    }

}
