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

public class JarFile extends FileGroup {

    public JarFile(final String name) {
        super(name);

    }

    public JavaFile addJavaFile(final CompilationUnit cu) {
        final JavaFile result = new JavaFile(cu, this);
        addFile(result);
        return result;
    }

}
