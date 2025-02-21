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
 * A C++ label statement of the form <code>label:</code>
 */
public class LabelStatement extends Statement {

    private final Label label;

    /**
     * Creates a label statement marking the position of the supplied label
     * <p>
     * <p>
     * the label to mark
     */
    public LabelStatement(final Label label) {
        this.label = label;
    }

    @Override
    void write(final Writer writer, final String indent, final Namespace currentNamespace) throws IOException {
        writer.write(TextUtils.indentText(indent, getParentFunction().getLabelName(label) + ": ;"));

    }

}
