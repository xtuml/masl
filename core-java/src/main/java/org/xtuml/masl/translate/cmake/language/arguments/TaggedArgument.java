//
// File: TaggedArgument.java
//
// UK Crown Copyright (c) 2015. All Rights Reserved.
//
package org.xtuml.masl.translate.cmake.language.arguments;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

public class TaggedArgument implements Argument
{

  private final SimpleArgument tag;
  private final List<SimpleArgument>    args;

  public TaggedArgument ( final SimpleArgument tag )
  {
    this(tag,ImmutableList.of());
  }

  public TaggedArgument ( final SimpleArgument tag, final SimpleArgument arg )
  {
    this(tag,ImmutableList.of(arg));
  }

  public TaggedArgument ( final SimpleArgument tag, final Iterable<? extends SimpleArgument> args )
  {
    this.tag = tag;
    this.args = ImmutableList.copyOf(args);
  }

  @Override
  public String getText ()
  {
    if ( args.size() == 0 )
    {
      return tag.getText() + "\t";
    }
    else if ( args.size() == 1 )
    {
      return tag.getText() + "\t" + args.get(0).getText();
    }
    else
    {
      return tag.getText() + "\t" +
             args.stream().map(Argument::getText).sorted().collect(Collectors.joining("\n\t", "\n\t", ""));
    }
  }

}
