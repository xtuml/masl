/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.translate.cmake;

import com.google.common.collect.Lists;
import org.xtuml.masl.cppgen.InterfaceLibrary;
import org.xtuml.masl.translate.cmake.language.arguments.SingleArgument;
import org.xtuml.masl.translate.cmake.language.commands.AddLibrary;
import org.xtuml.masl.translate.cmake.language.commands.Command;
import org.xtuml.masl.translate.cmake.language.commands.TargetLinkLibraries;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BuildInterfaceLibrary implements CMakeListsItem {

    public BuildInterfaceLibrary(final InterfaceLibrary library, final File sourcePath) {
        var name = new SingleArgument(library.getName());

        commands.add(new AddLibrary(name, AddLibrary.Type.INTERFACE, Collections.emptyList()));
        commands.add(new TargetLinkLibraries(name, TargetLinkLibraries.Scope.INTERFACE, Utils.getNameArgs(library.getDependencies())));
        commands.add(new Command("add_library", library.getParent().getName() + "::" + library.getName(), "ALIAS", library.getName()) );
    }

    @Override
    public void writeCode(final Writer writer, final String indent) throws IOException {
        for ( var command: commands) {
            command.writeCode(writer, indent);
        }
    }

    private final List<Command> commands = new ArrayList<>();

}
