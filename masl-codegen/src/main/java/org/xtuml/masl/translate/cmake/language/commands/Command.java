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
import org.xtuml.masl.translate.cmake.CMakeListsItem;
import org.xtuml.masl.translate.cmake.language.arguments.Argument;
import org.xtuml.masl.translate.cmake.language.arguments.SimpleArgument;
import org.xtuml.masl.translate.cmake.language.arguments.SingleArgument;
import org.xtuml.masl.utils.TextUtils;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Command implements CMakeListsItem {

    public Command(final String name) {
        this(name, ImmutableList.of());
    }

    public Command(final String name, final Argument... arg) {
        this(name, ImmutableList.copyOf(arg));
    }

    public Command(final String name, final String... args) {
        this(name, ImmutableList.copyOf(Arrays.stream(args).map(SingleArgument::new).collect(Collectors.toList())));
    }

    public Command(final String name, final Iterable<? extends Argument> args) {
        this.name = name;
        this.args = ImmutableList.copyOf(args);

    }

    @Override
    public void writeCode(final Writer writer, final String indent) throws IOException {
        writer.write(indent + name);

        if (args.isEmpty()) {
            writer.write("()\n");
        } else if (args.size() == 1 && args.get(0) instanceof SimpleArgument) {
            writer.write(" ( " + args.get(0).getText() + " )\n");
        } else {
            writer.write(" (\n");
            final String
                    argsText =
                    TextUtils.alignTabs(args.stream().map(Argument::getText).collect(Collectors.joining("\n")));
            TextUtils.indentText(writer, indent + "  ", argsText);
            writer.write("\n" + indent + "  )\n");
        }

    }

    private final String name;
    private final List<Argument> args;

}
