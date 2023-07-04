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
package org.xtuml.masl.metamodel.type;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.expression.Expression;

public interface TypeDefinition extends ASTNode {

    enum ActualType {
        ANY_INSTANCE, BOOLEAN, CHARACTER, DEVICE, DURATION, EVENT, BYTE, INTEGER, REAL, SMALL_INTEGER, STRING, TIMER, TIMESTAMP, WCHARACTER, WSTRING, BAG, SET, ARRAY, SEQUENCE, INSTANCE, USER_DEFINED, UNCONSTRAINED_ARRAY_SUBTYPE, UNCONSTRAINED_ARRAY, CONSTRAINED, STRUCTURE, ENUMERATE, DICTIONARY
    }

    ActualType getActualType();

    TypeDefinition getDefinedType();

    Expression getMinValue();

    Expression getMaxValue();

    TypeDeclaration getTypeDeclaration();

    boolean isNumeric();

    boolean isCollection();

    boolean isString();

    boolean isCharacter();

}
