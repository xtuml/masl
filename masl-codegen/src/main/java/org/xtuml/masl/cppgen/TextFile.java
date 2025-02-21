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

import org.xtuml.masl.CommandLine;
import org.xtuml.masl.translate.building.FileGroup;
import org.xtuml.masl.translate.building.ReferencedFile;
import org.xtuml.masl.translate.building.WriteableFile;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public class TextFile extends ReferencedFile implements Comparable<TextFile>, WriteableFile {

    private String commentCharacters;
    private final StringWriter bufferedText;

    public TextFile(final FileGroup parent, final File file) {
        super(parent, file);
        bufferedText = new StringWriter();
    }

    public TextFile(final FileGroup parent, final String filename) {
        this(parent, new File(filename));
    }

    public StringBuffer getBuffer() {
        return bufferedText.getBuffer();
    }

    public StringWriter getWriter() {
        return bufferedText;
    }

    @Override
    public int compareTo(final TextFile rhs) {
        return getFile().compareTo(rhs.getFile());
    }

    @Override
    public int hashCode() {
        return getFile().hashCode();
    }

    @Override
    public void writeCode(final Writer writer) throws IOException {
        writer.write(bufferedText.toString());
    }
}
