/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
  SPDX-License-Identifier: Apache-2.0 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.antlr;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MaslFileReader {

    public MaslFileReader(final File file) throws FileNotFoundException {
        this.file = file;
        fileReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.ISO_8859_1));
    }

    public File getFile() {
        return file;
    }

    public String getFileLine(final int line) {
        if (line == 0) {
            return "";
        }
        try {
            while (lines.size() < line) {
                lines.add(fileReader.readLine());
            }
            return lines.get(line - 1);
        } catch (final IOException e) {
            return "";
        }
    }

    private final File file;
    private final BufferedReader fileReader;
    private final List<String> lines = new ArrayList<>();
}
