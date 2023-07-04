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

import org.xtuml.masl.cppgen.Executable;
import org.xtuml.masl.translate.cmake.functions.SimpleAddExecutable;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

public class BuildExecutable implements CMakeListsItem {

    public BuildExecutable(final Executable exe, final File sourcePath) {
        addLib =
                new SimpleAddExecutable(Utils.getNameArg(exe),
                                        Utils.getPathArgs(exe.getFiles()),
                                        Utils.getNameArgs(exe.getDependencies()),
                                        exe.isExport());

    }

    @Override
    public void writeCode(final Writer writer, final String indent) throws IOException {
        addLib.writeCode(writer, "");
    }

    private final SimpleAddExecutable addLib;

}
