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

public interface CreateDurationExpression extends Expression {

    enum Field {
        Weeks, Days, Hours, Minutes, Seconds, Millis, Micros, Nanos
    }

    Field getField();

    Expression getArgument();

}
