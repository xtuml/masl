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
package org.xtuml.masl.translate.cmake.functions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import org.xtuml.masl.translate.cmake.language.arguments.Argument;
import org.xtuml.masl.translate.cmake.language.arguments.SimpleArgument;
import org.xtuml.masl.translate.cmake.language.arguments.SingleArgument;
import org.xtuml.masl.translate.cmake.language.arguments.TaggedArgument;
import org.xtuml.masl.translate.cmake.language.commands.Command;

import java.util.List;

public class SimpleAddLibrary extends Command {
    public SimpleAddLibrary(final String commandName,
                            final SimpleArgument name,
                            final Iterable<? extends SimpleArgument> sources,
                            final Iterable<? extends SimpleArgument> links,
                            final SimpleArgument export,
                            final boolean install,
                            final Iterable<? extends SimpleArgument> includes) {
        super(commandName, createArgs(name, sources, links, export, install, includes));
    }

    private static List<Argument> createArgs(final SimpleArgument name,
                                             final Iterable<? extends SimpleArgument> sources,
                                             final Iterable<? extends SimpleArgument> links,
                                             final SimpleArgument export,
                                             final boolean install,
                                             final Iterable<? extends SimpleArgument> includes) {
        final ImmutableList.Builder<Argument> builder = new ImmutableList.Builder<>();

        builder.add(new TaggedArgument(NAME, name));
        builder.add(new TaggedArgument(SOURCES, sources));
        builder.add(new TaggedArgument(LINKS, links));
        if (install) {
            builder.add(new TaggedArgument(INSTALL));
        }
        if (export != null) {
            builder.add(new TaggedArgument(EXPORT, export));
        }
        if (Iterables.size(includes) > 0) {
            builder.add(new TaggedArgument(INCLUDES, includes));
        }
        return builder.build();
    }

    final static SingleArgument NAME = new SingleArgument("NAME");
    final static SingleArgument SOURCES = new SingleArgument("SOURCES");
    final static SingleArgument LINKS = new SingleArgument("LINKS");
    final static SingleArgument INCLUDES = new SingleArgument("INCLUDES");
    final static SingleArgument EXPORT = new SingleArgument("EXPORT");
    final static SingleArgument INSTALL = new SingleArgument("INSTALL");
}
