//
// File: AddExecutable.java
//
// UK Crown Copyright (c) 2015. All Rights Reserved.
//
package org.xtuml.masl.translate.cmake.language.commands;

import java.util.List;

import org.xtuml.masl.translate.cmake.language.arguments.Argument;
import org.xtuml.masl.translate.cmake.language.arguments.CompoundArgument;
import org.xtuml.masl.translate.cmake.language.arguments.SimpleArgument;
import org.xtuml.masl.translate.cmake.language.arguments.SingleArgument;

import com.google.common.collect.ImmutableList;


public class SetTargetProperties extends Command
{
  public static class PropertyValue extends CompoundArgument
  {
    public PropertyValue ( final SimpleArgument property, final SimpleArgument value )
    {
      super(ImmutableList.of(property,value));
    }
  }

  public SetTargetProperties (
                        final Iterable<SimpleArgument> targets,
                        final Iterable<? extends PropertyValue> properties )
  {
    super("set_target_properties", createArgs( targets, properties));
  }

  private static List<Argument> createArgs ( final Iterable<SimpleArgument> targets,
                                             final Iterable<? extends PropertyValue> properties )
  {
    final ImmutableList.Builder<Argument> builder = new ImmutableList.Builder<>();

    builder.addAll(targets);

    builder.add(PROPERTIES);
    builder.addAll(properties);

    return builder.build();
  }

  private static SimpleArgument PROPERTIES = new SingleArgument("PROPERTIES");
}
