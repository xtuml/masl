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
package org.xtuml.masl.translate.cmake.language.commands;

import com.google.common.collect.ImmutableList;
import org.xtuml.masl.translate.cmake.language.arguments.Argument;
import org.xtuml.masl.translate.cmake.language.arguments.SimpleArgument;
import org.xtuml.masl.translate.cmake.language.arguments.SingleArgument;

import java.util.List;

public class TargetLinkLibraries extends Command {

    public TargetLinkLibraries(final SingleArgument name, final Iterable<? extends SimpleArgument> links) {
        super("target_link_library", createArgs(name, links));
    }

    private static List<Argument> createArgs(final SingleArgument name,
                                             final Iterable<? extends SimpleArgument> links) {
        final ImmutableList.Builder<Argument> builder = new ImmutableList.Builder<>();

        builder.add(name);
        builder.addAll(links);
        return builder.build();
    }

}
