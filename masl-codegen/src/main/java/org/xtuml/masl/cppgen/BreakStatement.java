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

/**
 * A C++ break statement, as used to break out of loops and case statements.
 */
public class BreakStatement extends Statement {

    /**
     * Creates a new break statement.
     */
    public BreakStatement() {
    }

    @Override
    void write(final Writer writer, final String indent, final Namespace currentNamespace) throws IOException {
        writer.write(TextUtils.indentText(indent, "break;"));
    }
}
