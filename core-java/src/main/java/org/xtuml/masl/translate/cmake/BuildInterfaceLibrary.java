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
package org.xtuml.masl.translate.cmake;

import org.xtuml.masl.cppgen.InterfaceLibrary;
import org.xtuml.masl.translate.cmake.functions.SimpleAddInterfaceLibrary;
import org.xtuml.masl.translate.cmake.language.arguments.SingleArgument;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Collections;

public class BuildInterfaceLibrary implements CMakeListsItem {

    public BuildInterfaceLibrary(final InterfaceLibrary library, final File sourcePath) {
        addLib =
                new SimpleAddInterfaceLibrary(new SingleArgument(library.getName()),
                                              Utils.getPathArgs(Collections.emptyList()),
                                              Utils.getNameArgs(library.getDependencies()),
                                              library.isExport() ?
                                              new SingleArgument(library.getParent().getName()) :
                                              null,
                                              library.isExport(),
                                              Utils.getPathArgs(library.getPublicHeaders()));
    }

    @Override
    public void writeCode(final Writer writer, final String indent) throws IOException {
        addLib.writeCode(writer, "");
    }

    private final SimpleAddInterfaceLibrary addLib;

}
