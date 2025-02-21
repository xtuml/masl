/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
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
