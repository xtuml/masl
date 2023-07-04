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
