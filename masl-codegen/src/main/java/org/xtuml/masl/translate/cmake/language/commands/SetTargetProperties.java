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
import org.xtuml.masl.translate.cmake.language.arguments.CompoundArgument;
import org.xtuml.masl.translate.cmake.language.arguments.SimpleArgument;
import org.xtuml.masl.translate.cmake.language.arguments.SingleArgument;

import java.util.List;

public class SetTargetProperties extends Command {
    public static class PropertyValue extends CompoundArgument {
        public PropertyValue(final SimpleArgument property, final SimpleArgument value) {
            super(ImmutableList.of(property, value));
        }
    }

    public SetTargetProperties(final Iterable<SimpleArgument> targets,
                               final Iterable<? extends PropertyValue> properties) {
        super("set_target_properties", createArgs(targets, properties));
    }

    private static List<Argument> createArgs(final Iterable<SimpleArgument> targets,
                                             final Iterable<? extends PropertyValue> properties) {
        final ImmutableList.Builder<Argument> builder = new ImmutableList.Builder<>();

        builder.addAll(targets);

        builder.add(PROPERTIES);
        builder.addAll(properties);

        return builder.build();
    }

    private static final SimpleArgument PROPERTIES = new SingleArgument("PROPERTIES");
}
