/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.javagen;

import org.xtuml.masl.javagen.ast.def.CompilationUnit;
import org.xtuml.masl.translate.building.FileGroup;
import org.xtuml.masl.translate.building.ReferencedFile;
import org.xtuml.masl.translate.building.WriteableFile;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

public class JavaFile extends ReferencedFile implements WriteableFile {

    public JavaFile(final CompilationUnit cu, final FileGroup jarFile) {
        super(jarFile, new File("/src/main/java/" + cu.getFileName()));
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