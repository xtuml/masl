//
// File: ImakeMacro.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.translate.cmake;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.stream.Collectors;

import org.xtuml.masl.cppgen.SharedLibrary;
import org.xtuml.masl.translate.cmake.functions.SimpleAddSharedLibrary;
import org.xtuml.masl.translate.cmake.language.arguments.SimpleArgument;
import org.xtuml.masl.translate.cmake.language.commands.Command;


public class BuildSharedLibrary
    implements CMakeListsItem
{

  public BuildSharedLibrary ( final SharedLibrary library, final File sourcePath )
  {
      addLib = new SimpleAddSharedLibrary(Utils.getNameArg(library),
                                          Utils.getPathArgs(library.getBodyFiles()),
                                          Utils.getNameArgs(library.getDependencies().stream()
                                            .filter(fg -> !(fg.getName() != null && fg.getName().contains("common_metadata")))
                                            .collect(Collectors.toSet())),
                                          library.isExport()?exportTarget:null,
                                          library.isExport(),
                                          Utils.getPathArgs(library.getPublicHeaders()));
  }

  @Override
  public void writeCode ( final Writer writer, final String indent ) throws IOException
  {
    addLib.writeCode(writer, "");
  }

  private final Command addLib;

  private static final SimpleArgument exportTarget = new Variable("MaslExportTarget").getReference();
}
