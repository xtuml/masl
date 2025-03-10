/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.metamodel.code;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.expression.Expression;

import java.util.List;

/**
 * Represents a MASL case statement. e.g.
 *
 * <pre>
 * case colour is
 *   when red | green | blue  =>
 *     color_type := primary;
 *   when yellow | cyan | magenta =>
 *     colour_type := secondary;
 *   when others =>
 *     colour_type := unrecognised;
 * end case;
 * </pre>
 */
public interface CaseStatement extends Statement {

    /**
     * Represents an alternative within a MASL case statement.
     */
    interface Alternative extends ASTNode {

        /**
         * Gets a list of {@link org.xtuml.masl.metamodel.expression.Expression
         * Expression}s that must match the <code>case</code> statement's discriminator
         * before the statements are executed. If the list is empty, then this
         * alternative represents the <code>others</code> option. There will at most one
         * such alternative per <code>case</code> statement, and it will always be the
         * last one.
         *
         * @return the expressions to check against the discriminator
         */
        List<? extends Expression> getConditions();

        /**
         * Gets a list of {@link org.xtuml.masl.metamodel.expression.Statements
         * Statement}s that will be executed if the parent <code>case</code> statement's
         * discriminator matches one of the conditions. If the list of conditions is
         * empty, then this returns the statements to be executed when none of the other
         * alternatives match the discriminator. There will at most one such alternative
         * per <code>case</code> statement, and it will always be the last one.
         *
         * @return the statements to be executed
         */
        List<? extends Statement> getStatements();
    }

    /**
     * Gets a list of {@link Alternative}s that make up the case statement. An
     * alternative will be called if its condition is equal to the discriminator
     *
     * @return a list of {@link Alternative}s within the case statement
     */
    List<? extends Alternative> getAlternatives();

    /**
     * Returns the {@link org.xtuml.masl.metamodel.expression.Expression Expression}
     * that determines which of the alternatives is to be executed.
     *
     * @return the expression that determines the alternative to be executed
     */
    Expression getDiscriminator();
}
