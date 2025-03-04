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

import org.xtuml.masl.cppgen.ArchiveLibrary;
import org.xtuml.masl.translate.cmake.language.arguments.SingleArgument;
import org.xtuml.masl.translate.cmake.language.commands.AddLibrary;
import org.xtuml.masl.translate.cmake.language.commands.Command;
import org.xtuml.masl.translate.cmake.language.commands.TargetLinkLibraries;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class BuildArchiveLibrary implements CMakeListsItem {

    public BuildArchiveLibrary(final ArchiveLibrary library, final File sourcePath) {
        var name = new SingleArgument(library.getName());

        commands.add(new AddLibrary(name, AddLibrary.Type.STATIC, Utils.getSourcePathArgs(library.getBodyFiles())));
        commands.add(new TargetLinkLibraries(name, TargetLinkLibraries.Scope.PUBLIC, Utils.getNameArgs(library.getDependencies())));
        commands.add(Utils.addHeaderPath(library) );
        commands.add(new Command("set_property", "TARGET", library.getName(), "PROPERTY", "ARCHIVE_OUTPUT_DIRECTORY", "${CMAKE_BINARY_DIR}/lib"));

        if ( library.isExport()) {
            commands.add(new Command("install", "TARGETS", library.getName(), "FILE_SET", "HEADERS"));
            commands.add(new Command("add_library", library.getParent().getName() + "::" + library.getName(), "ALIAS", library.getName()) );
        }
    }

    @Override
    public void writeCode(final Writer writer, final String indent) throws IOException {
        for ( var command: commands) {
            command.writeCode(writer, indent);
        }
    }

    private final List<Command> commands = new ArrayList<>();

}
