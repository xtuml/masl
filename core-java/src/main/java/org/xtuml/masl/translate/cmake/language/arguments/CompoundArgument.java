//
// File: CompoundArgument.java
//
// UK Crown Copyright (c) 2015. All Rights Reserved.
//
package org.xtuml.masl.translate.cmake.language.arguments;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

public class CompoundArgument
    implements SimpleArgument
{

  private final List<SimpleArgument> args;

  public CompoundArgument ( final Iterable<? extends SimpleArgument> args )
  {
    this.args = ImmutableList.copyOf(args);

  }

  @Override
  public String getText ()
  {
    return args.stream().map(Argument::getText).collect(Collectors.joining(" "));
  }

}