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
import org.xtuml.masl.metamodelImpl.domain.Domain;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.object.ObjectDeclaration;
import org.xtuml.masl.metamodelImpl.type.BasicType;
import org.xtuml.masl.metamodelImpl.type.InternalType;

public class ObjectNameExpression extends Expression
        implements org.xtuml.masl.metamodel.expression.ObjectNameExpression {

    public static ObjectNameExpression create(final Domain.Reference domainRef, final String objName) {
        if (domainRef == null || objName == null) {
            return null;
        }

        try {
            final Position
                    position =
                    domainRef.getPosition() == null ? Position.getPosition(objName) : domainRef.getPosition();
            return domainRef.getDomain().getObject(objName).getReference(position);

        } catch (final SemanticError e) {
            e.report();
            return null;
        }
    }

    public ObjectNameExpression(final Position position, final ObjectDeclaration object) {
        super(position);
        this.object = object;
    }

    @Override
    public ObjectDeclaration getObject() {
        return object;
    }

    private final ObjectDeclaration object;

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ObjectNameExpression obj2)) {
            return false;
        } else {

            return object.equals(obj2.object);
        }
    }

    @Override
    public int hashCode() {
        return object.hashCode();
    }

    @Override
    public String toString() {
        return object.getName();
    }

    @Override
    public BasicType getType() {
        return InternalType.OBJECT;
    }

    @Override
    public <R, P> R accept(final ASTNodeVisitor<R, P> v, final P p) throws Exception {
        return v.visitObjectNameExpression(this, p);
    }

}
