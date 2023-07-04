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
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.object.AttributeDeclaration;
import org.xtuml.masl.metamodelImpl.object.ObjectDeclaration;
import org.xtuml.masl.metamodelImpl.type.BasicType;

import java.util.Collections;
import java.util.List;

public class FindAttributeNameExpression extends Expression
        implements org.xtuml.masl.metamodel.expression.FindAttributeNameExpression {

    public static FindAttributeNameExpression create(final ObjectDeclaration parentObject, final String name) {
        if (parentObject == null) {
            return null;
        }

        try {
            return new FindAttributeNameExpression(Position.getPosition(name), parentObject.getAttribute(name));
        } catch (final SemanticError e) {
            e.report();
            return null;
        }
    }

    private final AttributeDeclaration attribute;

    private FindAttributeNameExpression(final Position position, final AttributeDeclaration attribute) {
        super(position);
        this.attribute = attribute;
    }

    @Override
    public AttributeDeclaration getAttribute() {
        return attribute;
    }

    @Override
    public String toString() {
        return attribute.getName();
    }

    public AttributeDeclaration getDeclaration() {
        return attribute;
    }

    @Override
    public BasicType getType() {
        return getDeclaration().getType();
    }

    @Override
    protected List<Expression> getFindArgumentsInner() {
        return Collections.emptyList();
    }

    @Override
    public int getFindAttributeCount() {
        return 1;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof FindAttributeNameExpression obj2)) {
            return false;
        } else {

            return attribute.equals(obj2.attribute);
        }
    }

    @Override
    public int hashCode() {

        return attribute.hashCode();
    }

    @Override
    public <R, P> R accept(final ASTNodeVisitor<R, P> v, final P p) throws Exception {
        return v.visitFindAttributeNameExpression(this, p);
    }

}
