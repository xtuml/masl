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
import com.google.common.collect.Iterables;
import org.xtuml.masl.translate.cmake.language.arguments.Argument;
import org.xtuml.masl.translate.cmake.language.arguments.SingleArgument;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

public class SetVariable extends Command {
    public SetVariable(final SingleArgument name, final Iterable<? extends Argument> values) {
        super("set", Iterables.concat(Collections.singleton(name), values));
    }

    public SetVariable(final SingleArgument name, final Argument... values) {
        this(name, ImmutableList.copyOf(values));
    }

    public SetVariable(final String name, final Argument... values) {
        this(new SingleArgument(name), values);
    }

    public SetVariable(final String name, final String... values) {
        this(new SingleArgument(name),
             ImmutableList.copyOf(Arrays.stream(values).map(SingleArgument::new).collect(Collectors.toList())));
    }

}
