/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
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