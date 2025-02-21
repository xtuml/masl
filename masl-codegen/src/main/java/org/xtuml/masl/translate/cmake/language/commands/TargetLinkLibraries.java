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

public class TargetLinkLibraries extends Command {

    public enum Scope { PUBLIC, PRIVATE, INTERFACE }

    public TargetLinkLibraries(final SingleArgument name, Scope scope, final Iterable<? extends SimpleArgument> links) {
        super("target_link_libraries", createArgs(name, scope, links));
    }

    private static List<Argument> createArgs(final SingleArgument name,
                                             Scope scope,
                                             final Iterable<? extends SimpleArgument> links) {
        final ImmutableList.Builder<Argument> builder = new ImmutableList.Builder<>();

        builder.add(name);
        builder.add(new SingleArgument(scope.toString()));
        builder.addAll(links);
        return builder.build();
    }

}
