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
