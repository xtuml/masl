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


public class TargetLinkLibraries extends Command
{

  public TargetLinkLibraries ( final SingleArgument name, final Iterable<? extends SimpleArgument> links )
  {
    super("target_link_library",createArgs(name,links));
  }

  private static List<Argument> createArgs(final SingleArgument name, final Iterable<? extends SimpleArgument> links)
  {
    final ImmutableList.Builder<Argument> builder = new ImmutableList.Builder<>();

    builder.add(name);
    builder.addAll(links);
    return builder.build();
  }

}
