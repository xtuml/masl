//
// File: AddExecutable.java
//
// UK Crown Copyright (c) 2015. All Rights Reserved.
//
package org.xtuml.masl.translate.cmake.language.commands;

import java.util.List;

import org.xtuml.masl.translate.cmake.language.arguments.Argument;
import org.xtuml.masl.translate.cmake.language.arguments.SimpleArgument;
import org.xtuml.masl.translate.cmake.language.arguments.SingleArgument;

import com.google.common.collect.ImmutableList;


public class AddExecutable extends Command
{
  public AddExecutable ( final SingleArgument name, final Iterable<SimpleArgument> sources )
  {
    super("add_executable",createArgs(name,sources));
  }

  private static List<Argument> createArgs(final SingleArgument name, final Iterable<SimpleArgument> sources)
  {
    final ImmutableList.Builder<Argument> builder = new ImmutableList.Builder<>();

    builder.add(name);
    builder.addAll(sources);
    return builder.build();
  }

}
