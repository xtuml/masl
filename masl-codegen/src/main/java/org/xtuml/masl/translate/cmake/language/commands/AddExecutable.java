/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.translate.cmake.language.commands;

import com.google.common.collect.ImmutableList;
import org.xtuml.masl.translate.cmake.language.arguments.Argument;
import org.xtuml.masl.translate.cmake.language.arguments.SimpleArgument;
import org.xtuml.masl.translate.cmake.language.arguments.SingleArgument;

import java.util.List;

public class AddExecutable extends Command {
    public AddExecutable(final SingleArgument name, final Iterable<? extends SimpleArgument> sources) {
        super("add_executable", createArgs(name, sources));
    }

    private static List<Argument> createArgs(final SingleArgument name, final Iterable<? extends SimpleArgument> sources) {
        final ImmutableList.Builder<Argument> builder = new ImmutableList.Builder<>();

        builder.add(name);
        builder.addAll(sources);
        return builder.build();
    }

}
