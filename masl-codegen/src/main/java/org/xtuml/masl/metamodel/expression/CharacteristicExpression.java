/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.metamodel.expression;

import org.xtuml.masl.metamodel.type.BasicType;

import java.util.List;

public interface CharacteristicExpression extends Expression {

    enum Type {
        FIRST, FIRSTCHARPOS, GET_UNIQUE, IMAGE, LAST, LENGTH, LOWER, PRED, RANGE, SUCC, UPPER, VALUE,

        NOW,

        TIME, DATE,

    }

    Type getCharacteristic();

    Expression getLhs();

    List<? extends Expression> getArguments();

    BasicType getLhsType();
}
