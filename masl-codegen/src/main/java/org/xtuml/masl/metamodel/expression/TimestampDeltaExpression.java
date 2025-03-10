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

public interface TimestampDeltaExpression extends Expression {

    enum Type {
        YEARS, MONTHS
    }

    Expression getArgument();

    Expression getLhs();

    Type getDeltaType();

}
