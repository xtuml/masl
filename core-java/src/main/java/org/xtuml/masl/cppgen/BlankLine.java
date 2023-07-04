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

import java.io.IOException;
import java.io.Writer;

/**
 * Represents a blank line in C++ code. This exists for purely aesthetic
 * purposes to aid readablility.
 */
public class BlankLine extends Statement {

    /**
     * Number of blank lines to write
     */
    private final int noLines;

    /**
     * Constructs a number of blank lines
     * <p>
     * <p>
     * the number of blank lines required
     */
    public BlankLine(final int noLines) {
        this.noLines = noLines;
    }

    /**
     * Constructs a single blank line
     */
    public BlankLine() {
        this(1);
    }

    @Override
    void write(final Writer writer, final String indent, final Namespace currentNamespace) throws IOException {
        for (int i = 0; i < noLines; ++i) {
            writer.write("\n");
        }
    }

}
