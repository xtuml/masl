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

import java.io.IOException;
import java.io.Writer;

/**
 * Raw C++ code to be written verbatim into the code file. No syntax or semantic
 * checking is performed.
 */
final public class RawCode extends Statement {

    private final String code;

    /**
     * Creates raw code from the specified string.
     * <p>
     * <p>
     * the raw code
     */
    public RawCode(final String code) {
        this.code = code;
    }

    @Override
    void write(final Writer writer, final String indent, final Namespace currentNamespace) throws IOException {
        writer.write(indent + code);
    }

}
