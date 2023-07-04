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
import org.xtuml.masl.metamodelImpl.common.Positioned;
import org.xtuml.masl.metamodelImpl.expression.Expression;

public abstract class TypeDefinition extends Positioned implements org.xtuml.masl.metamodel.type.TypeDefinition {

    TypeDefinition(final Position position) {
        super(position);
    }

    @Override
    public Expression getMinValue() {
        return null;
    }

    @Override
    public Expression getMaxValue() {
        return null;
    }

    @Override
    public TypeDefinition getDefinedType() {
        return this;
    }

    private TypeDeclaration typeDeclaration = null;

    @Override
    public TypeDeclaration getTypeDeclaration() {
        return typeDeclaration;
    }

    public void setTypeDeclaration(final TypeDeclaration typeDeclaration) {
        this.typeDeclaration = typeDeclaration;
    }

    // Force typedefs to implement equals and hashCode
    @Override
    abstract public boolean equals(Object obj);

    @Override
    abstract public int hashCode();

    abstract public BasicType getPrimitiveType();

    @Override
    public boolean isNumeric() {
        return false;
    }

    @Override
    public boolean isCollection() {
        return false;
    }

    @Override
    public boolean isString() {
        return false;
    }

    @Override
    public boolean isCharacter() {
        return false;
    }

    public void checkCanBePublic() {
    }

}
