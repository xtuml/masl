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

import java.io.IOException;
import java.io.Writer;
import java.util.Set;

/**
 * Creates a C++ statement from an expression.
 */
public class ExpressionStatement extends Statement {

    /**
     * The expression to use to form the statement
     */
    private final Expression expression;

    /**
     * Creates an expression statement
     * <p>
     * <p>
     * The expression to use to form the statement
     */
    public ExpressionStatement(final Expression expression) {
        this.expression = expression;
    }

    @Override
    void write(final Writer writer, final String indent, final Namespace currentNamespace) throws IOException {
        writer.write(TextUtils.indentText(indent, TextUtils.alignTabs(expression.getCode(currentNamespace) + ";")));

    }

    @Override
    Set<Declaration> getForwardDeclarations() {
        final Set<Declaration> result = super.getForwardDeclarations();
        result.addAll(expression.getForwardDeclarations());
        return result;
    }

    @Override
    Set<CodeFile> getIncludes() {
        final Set<CodeFile> result = super.getIncludes();
        result.addAll(expression.getIncludes());
        return result;
    }

}
