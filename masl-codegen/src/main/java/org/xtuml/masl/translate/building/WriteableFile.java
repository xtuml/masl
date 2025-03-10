/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.translate.building;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public interface WriteableFile {

    void writeCode(final Writer writer) throws IOException;

    File getFile();

    default void writeToFile(final File directory) throws IOException {
        final Writer text = new StringWriter();
        writeCode(text);
        BuildSet.updateFile(new File(directory, getFile().getPath()), text);
    }

    default boolean isPublicHeader() {
        return false;
    }

    default boolean isSourceFile() {
        return false;
    }

}
