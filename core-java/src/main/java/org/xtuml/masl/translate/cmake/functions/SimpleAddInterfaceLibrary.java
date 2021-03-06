//
// File: SimpleAddSharedLibrary.java
//
// UK Crown Copyright (c) 2015. All Rights Reserved.
//
package org.xtuml.masl.translate.cmake.functions;

import org.xtuml.masl.translate.cmake.language.arguments.SimpleArgument;


public class SimpleAddInterfaceLibrary extends SimpleAddLibrary
{
  public SimpleAddInterfaceLibrary ( final SimpleArgument name, final Iterable<? extends SimpleArgument> sources, final Iterable<? extends SimpleArgument> links, final SimpleArgument export, final boolean install, final Iterable<? extends SimpleArgument> includes )
  {
    super("simple_add_interface_library",name,sources,links,export,install,includes);
  }

}
