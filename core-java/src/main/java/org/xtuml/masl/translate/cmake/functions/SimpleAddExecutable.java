//
// File: SimpleAddSharedLibrary.java
//
// UK Crown Copyright (c) 2015. All Rights Reserved.
//
package org.xtuml.masl.translate.cmake.functions;

import java.util.Collections;

import org.xtuml.masl.translate.cmake.language.arguments.SimpleArgument;


public class SimpleAddExecutable extends SimpleAddLibrary
{
  public SimpleAddExecutable ( final SimpleArgument name, final Iterable<? extends SimpleArgument> sources, final Iterable<? extends SimpleArgument> links, final boolean install )
  {
    super("simple_add_executable",name,sources,links, null, install, Collections.<SimpleArgument>emptyList() );
  }

}
