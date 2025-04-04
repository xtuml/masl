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

import org.xtuml.masl.cppgen.Executable;
import org.xtuml.masl.translate.cmake.language.arguments.SingleArgument;
import org.xtuml.masl.translate.cmake.language.commands.AddExecutable;
import org.xtuml.masl.translate.cmake.language.commands.Command;
import org.xtuml.masl.translate.cmake.language.commands.TargetLinkLibraries;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class BuildExecutable implements CMakeListsItem {

    public BuildExecutable(final Executable exe, final File sourcePath) {
        var name = new SingleArgument(exe.getName());

        commands.add(new AddExecutable(name, Utils.getSourcePathArgs(exe.getBodyFiles())));
        commands.add(new TargetLinkLibraries(name, TargetLinkLibraries.Scope.PUBLIC, Utils.getNameArgs(exe.getDependencies())));
        commands.add(Utils.addHeaderPath(exe) );
        commands.add(new Command("set_property", "TARGET", exe.getName(), "PROPERTY", "RUNTIME_OUTPUT_DIRECTORY",  "${CMAKE_BINARY_DIR}/bin"));
        if ( exe.isExport()) {
            commands.add(new Command("install", "TARGETS", exe.getName()));
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
