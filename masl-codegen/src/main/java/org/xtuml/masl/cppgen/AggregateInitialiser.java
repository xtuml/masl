/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.cppgen;

import org.xtuml.masl.utils.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a C++ Agregate Initialiser. An aggregate initialiser is an
 * expression that can be used to initialise a C++ array or structure. For
 * example the expression after the {@code =}in the following,
 * <code>int a[] = {1, 2, 3, 4, 5};</code> or
 * <code>{@literal std::pair<int,std::string>} b = {1, "Fred Bloggs"};</code>
 * They can be nested to initialise arrays of structures etc.
 */
public class AggregateInitialiser extends Expression {

    /**
     * Creates an aggregate initialiser from a list of expressions. The expressions
     * will be used in the order supplied in the resulting initialiser.
     * <p>
     * <p>
     * the initialisers to use
     */
    public AggregateInitialiser(final List<? extends Expression> initialisers) {
        this.initialisers = initialisers;
    }

    public AggregateInitialiser(Expression... initialisers) {
        this.initialisers = Arrays.asList(initialisers);
    }

    @Override
    int getPrecedence() {
        return 0;
    }

    @Override
    String getCode(final Namespace currentNamespace, final String alignment) {
        final List<String> initCode = new ArrayList<>();
        for (final Expression initialiser : initialisers) {
            String code = initialiser.getCode(currentNamespace, alignment + "\t");

            // The initialisers will be separated by commas, so need to parenthesise
            // them if their precedence is the same or lower than that of the comma
            // operator.
            if (initialiser.getPrecedence() >= BinaryOperator.COMMA.getPrecedence()) {
                code = "(" + code + ")";
            }

            initCode.add(code);
        }

        return "{" + TextUtils.formatList(initCode, "", "\t", "", ",\n" + alignment, "") + "}";

    }

    /**
     * A list of initialisers
     */
    private final List<? extends Expression> initialisers;

}
