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
 * A C++ goto statement. For those rare occasions when the alternatives are even
 * more ugly.
 */
public class GotoStatement extends Statement {

    private final Label label;

    /**
     * Create a statement to goto the specified label.
     * <p>
     * <p>
     * the label to goto
     */
    public GotoStatement(final Label label) {
        this.label = label;
    }

    @Override
    void write(final Writer writer, final String indent, final Namespace currentNamespace) throws IOException {
        writer.write(TextUtils.indentText(indent, "goto " + getParentFunction().getLabelName(label) + ";"));
    }

}
