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

import org.xtuml.masl.CommandLine;
import org.xtuml.masl.translate.build.FileGroup;
import org.xtuml.masl.translate.build.ReferencedFile;
import org.xtuml.masl.translate.build.WriteableFile;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public class TextFile extends ReferencedFile implements Comparable<TextFile>, WriteableFile {

    private String commentCharacters;
    private final StringWriter bufferedText;
    private boolean copyRightEnabled;

    public TextFile(final FileGroup parent, final File file) {
        super(parent, file);
        copyRightEnabled = false;
        bufferedText = new StringWriter();
    }

    public TextFile(final FileGroup parent, final String filename) {
        this(parent, new File(filename));
    }

    public void enableCopyright(final String commentCharacters) {
        this.commentCharacters = commentCharacters;
        copyRightEnabled = true;
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
        if (copyRightEnabled) {
            String copyrightNotice = CommandLine.INSTANCE.getCopyrightNotice();
            writer.write(commentCharacters +
                         " File: " +
                         getFile().getPath() +
                         "\n" +
                         (null == copyrightNotice ?
                          "" :
                          commentCharacters +
                          "\n" +
                          commentCharacters +
                          " " +
                          copyrightNotice.replaceAll("\n", "\n" + commentCharacters + " ") +
                          "\n") +
                         commentCharacters +
                         "\n");
        }
        writer.write(bufferedText.toString());
    }
}
