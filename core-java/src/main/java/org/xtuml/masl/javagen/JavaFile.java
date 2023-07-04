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
package org.xtuml.masl.javagen;

import org.xtuml.masl.javagen.ast.def.CompilationUnit;
import org.xtuml.masl.translate.build.FileGroup;
import org.xtuml.masl.translate.build.ReferencedFile;
import org.xtuml.masl.translate.build.WriteableFile;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

public class JavaFile extends ReferencedFile implements WriteableFile {

    public JavaFile(final CompilationUnit cu, final FileGroup jarFile) {
        super(jarFile, new File("javasource" + "/" + jarFile.getName() + "/" + cu.getFileName()));
        this.cu = cu;
    }

    private final CompilationUnit cu;

    @Override
    public void writeCode(final Writer writer) throws IOException {
        try {
            new CodeWriter().writeCode(writer, cu);
        } catch (final IOException e) {
            throw e;
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

}