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
package org.xtuml.masl.translate.cmake.language.arguments;

import com.google.common.collect.ImmutableList;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CompoundArgument implements SimpleArgument {

    private final List<SimpleArgument> args;

    public CompoundArgument(final Iterable<? extends SimpleArgument> args) {
        this.args = ImmutableList.copyOf(args);

    }

    public CompoundArgument(final SimpleArgument... args) {
        this.args = ImmutableList.copyOf(args);

    }

    public CompoundArgument(final String... args) {
        this.args = ImmutableList.copyOf(Arrays.stream(args).map(SingleArgument::new).collect(Collectors.toList()));

    }

    @Override
    public String getText() {
        return args.stream().map(Argument::getText).collect(Collectors.joining(" "));
    }

}