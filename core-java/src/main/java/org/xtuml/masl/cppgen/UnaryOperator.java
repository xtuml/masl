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
package org.xtuml.masl.cppgen;

public enum UnaryOperator {
    PLUS("+", true, 3), MINUS("-", true, 3), NOT("!", true, 3), BITNOT("~", true, 3), ADDRESS_OF("&",
                                                                                                 true,
                                                                                                 3), DEREFERENCE("*",
                                                                                                                 true,
                                                                                                                 3), POSTINCREMENT(
            "++",
            false,
            2), PREINCREMENT("++", true, 3), POSTDECREMENT("--", false, 2), PREDECREMENT("--", true, 3);

    private final String operator;
    boolean prefix;
    int precedence;

    UnaryOperator(final String operator, final boolean prefix, final int precedence) {
        this.operator = operator;
        this.prefix = prefix;
        this.precedence = precedence;
    }

    String getCode() {
        return operator;
    }

    boolean isPrefix() {
        return prefix;
    }

    /**
     * Determines the precedence for the operator. Precedence is determined
     * according to the table in C++ Programming Language (Third Edition)
     * Stroustrup, Section 6.2. See {@link Expression#getPrecedence()} for
     * details.
     *
     * @return the precedence of the operator
     */
    int getPrecedence() {
        return precedence;
    }

}
