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
import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.expression.ObjectNameExpression;
import org.xtuml.masl.metamodelImpl.object.ObjectDeclaration;

import java.util.List;

public class InstanceType extends BasicType implements org.xtuml.masl.metamodel.type.InstanceType {

    private final ObjectDeclaration object;

    public static InstanceType create(final Position position,
                                      final ObjectNameExpression object,
                                      final boolean anonymous) {
        if (object == null) {
            return null;
        }

        return new InstanceType(position, object.getObject(), anonymous);
    }

    public static InstanceType createAnonymous(final ObjectDeclaration object) {
        return new InstanceType(null, object, true);
    }

    private InstanceType(final Position position, final ObjectDeclaration object, final boolean anonymous) {
        super(position, anonymous);
        this.object = object;
    }

    @Override
    public String toString() {
        return (isAnonymousType() ? "anonymous " : "") + "instance of " + object.getName();
    }

    @Override
    public ObjectDeclaration getObjectDeclaration() {
        return object;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof InstanceType rhs)) {
            return false;
        } else {

            return object.equals(rhs.object);
        }
    }

    @Override
    protected boolean isAssignableFromRelaxation(final BasicType rhs) {
        // Special case for assignment from anonymous anyinstance - this would be
        // the type of the null literal.
        return rhs instanceof AnyInstanceType && rhs.isAnonymousType();
    }

    @Override
    protected boolean isConvertibleFromRelaxation(final BasicType rhs) {
        // Special case for assignment from anonymous anyinstance - this would be
        // the type of the null literal.
        return rhs instanceof AnyInstanceType && rhs.isAnonymousType();
    }

    @Override
    public int hashCode() {
        return object.hashCode();
    }

    @Override
    public InstanceType getBasicType() {
        return this;
    }

    @Override
    public InstanceType getPrimitiveType() {
        return createAnonymous(object);
    }

    @Override
    public ActualType getActualType() {
        return ActualType.INSTANCE;
    }

    @Override
    public void checkCanBePublic() {
        new SemanticError(SemanticErrorCode.PrivateTypeCannotBeUsedPublicly, getPosition(), toString()).report();
    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitInstanceType(this);
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren();
    }

}
