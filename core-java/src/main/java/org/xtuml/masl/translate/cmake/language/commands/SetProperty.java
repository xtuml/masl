//
// File: AddExecutable.java
//
// UK Crown Copyright (c) 2015. All Rights Reserved.
//
package org.xtuml.masl.translate.cmake.language.commands;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.xtuml.masl.translate.cmake.language.arguments.Argument;
import org.xtuml.masl.translate.cmake.language.arguments.CompoundArgument;
import org.xtuml.masl.translate.cmake.language.arguments.SimpleArgument;
import org.xtuml.masl.translate.cmake.language.arguments.SingleArgument;
import org.xtuml.masl.translate.cmake.language.arguments.TaggedArgument;

import com.google.common.collect.ImmutableList;


public class SetProperty extends Command
{

  public static class TargetProperty extends SingleArgument
  {

    private TargetProperty ( final String name )
    {
      super(name);
    }
  }

  public static final TargetProperty EXCLUDE_FROM_ALL = new TargetProperty("EXCLUDE_FROM_ALL");
  public static final TargetProperty OUTPUT_NAME      = new TargetProperty("OUTPUT_NAME");
  public static final TargetProperty SOVERSION        = new TargetProperty("SOVERSION");
  public static final TargetProperty VERSION          = new TargetProperty("VERSION");

  public static SetProperty setPropertyTrue ( final SimpleArgument target, final TargetProperty property )
  {
    return setProperty(target, property, TRUE);
  }

  public static SetProperty setPropertyFalse ( final SimpleArgument target, final TargetProperty property )
  {
    return setProperty(target, property, FALSE);
  }


  public static SetProperty setProperty ( final SimpleArgument target,
                                          final TargetProperty property,
                                          final SimpleArgument value )
  {
    return new SetProperty(Type.TARGET, Collections.singleton(target), null, property, Collections.singleton(value));
  }

  private static SimpleArgument TRUE  = new SingleArgument("TRUE");
  private static SimpleArgument FALSE = new SingleArgument("FALSE");


  private enum Type
  {
    GLOBAL, DIRECTORY, TARGET, SOURCE, INSTALL, TEST, CACHE;

    private final SingleArgument argument = new SingleArgument(toString());
  }

  public enum Append
  {
    APPEND, APPEND_STRING;

    private final SingleArgument argument = new SingleArgument(toString());
  }

  private enum Words
  {
    PROPERTY;

    private final SingleArgument argument = new SingleArgument(toString());
  }

  private SetProperty ( final Type type,
                        final Iterable<SimpleArgument> objects,
                        final Append append,
                        final SimpleArgument name,
                        final Iterable<? extends SimpleArgument> values )
  {
    super("set_property", createArgs(type, objects, append, name, values));
  }

  private static List<Argument> createArgs ( final Type type,
                                             final Iterable<SimpleArgument> objects,
                                             final Append append,
                                             final SimpleArgument name,
                                             final Iterable<? extends SimpleArgument> values )
  {
    final ImmutableList.Builder<Argument> builder = new ImmutableList.Builder<>();

    builder.add(new TaggedArgument(type.argument, objects));

    if ( append != null )
    {
      builder.add(append.argument);
    }

    builder.add(new TaggedArgument(new CompoundArgument(Arrays.asList(Words.PROPERTY.argument, name)),values));


    return builder.build();
  }

}
