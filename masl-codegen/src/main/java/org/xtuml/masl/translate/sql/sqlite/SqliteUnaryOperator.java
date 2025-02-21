/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.translate.sql.sqlite;

import org.xtuml.masl.metamodel.expression.UnaryExpression;

import java.util.Objects;

/**
 * Define a enumeration that can provide the required mapping between the unary
 * operators used by MASL and their SQL equivalents. This will be used when a
 * MASL find Expression is being translated into a suitable SQL where clause.
 */
public enum SqliteUnaryOperator {
    NOT(" NOT ");

    private final String operatorText;

    SqliteUnaryOperator(final String operatorText) {
        this.operatorText = operatorText;
    }

    @Override
    public String toString() {
        return operatorText;
    }

    static String maslToSqlOperator(final UnaryExpression.Operator operator) {
        if (Objects.requireNonNull(operator) == UnaryExpression.Operator.NOT) {
            return SqliteUnaryOperator.NOT.toString();
        }
        throw new AssertionError("unknown MASl to SQL unary operator Mapping : " + operator);
    }

}
