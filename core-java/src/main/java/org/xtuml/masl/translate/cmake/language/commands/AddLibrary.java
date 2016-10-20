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


public class AddLibrary extends Command
{

  public enum Type
  {
    STATIC, SHARED, MODULE
  }

  public static AddLibrary addShared ( final SingleArgument name, final Iterable<? extends SimpleArgument> sources )
  {
    return new AddLibrary(name,Type.SHARED,sources);
  }

  public static AddLibrary addStatic ( final SingleArgument name, final Iterable<? extends SimpleArgument> sources )
  {
    return new AddLibrary(name,Type.STATIC,sources);
  }

  public AddLibrary ( final SingleArgument name, final Type type, final Iterable<? extends SimpleArgument> sources )
  {
    super("add_library",createArgs(name,type,sources));
  }

  private static List<Argument> createArgs(final SingleArgument name, final Type type, final Iterable<? extends SimpleArgument> sources)
  {
    final ImmutableList.Builder<Argument> builder = new ImmutableList.Builder<>();

    builder.add(name);
    if ( type != null )
    {
      builder.add(new SingleArgument(type.toString()));
    }
    builder.addAll(sources);
    return builder.build();
  }

}
