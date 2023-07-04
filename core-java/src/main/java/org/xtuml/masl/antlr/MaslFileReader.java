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
