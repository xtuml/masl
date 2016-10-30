//
// File: SetVariable.java
//
// UK Crown Copyright (c) 2015. All Rights Reserved.
//
package org.xtuml.masl.translate.cmake.language.commands;

import java.util.Collections;

import org.xtuml.masl.translate.cmake.language.arguments.SimpleArgument;
import org.xtuml.masl.translate.cmake.language.arguments.SingleArgument;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;


public class SetVariable extends Command
{
  public SetVariable ( final SingleArgument name, final SimpleArgument value )
  {
    super( "set", Lists.newArrayList(name, value) );
  }

  public SetVariable ( final SingleArgument name, final Iterable<? extends SimpleArgument> values )
  {
    super( "set", Iterables.concat(Collections.singleton(name),values) );
  }

}
