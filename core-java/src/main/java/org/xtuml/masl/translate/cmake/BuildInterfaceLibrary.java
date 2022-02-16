//
// File: ImakeMacro.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.translate.cmake;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Collections;

import org.xtuml.masl.cppgen.InterfaceLibrary;
import org.xtuml.masl.cppgen.Library;
import org.xtuml.masl.translate.build.ReferencedFile;
import org.xtuml.masl.translate.cmake.functions.SimpleAddInterfaceLibrary;
import org.xtuml.masl.translate.cmake.language.arguments.SimpleArgument;


public class BuildInterfaceLibrary
    implements CMakeListsItem
{

  public BuildInterfaceLibrary ( final InterfaceLibrary library, final File sourcePath )
  {
    addLib = new SimpleAddInterfaceLibrary(Utils.getNameArg(library),
                                           Utils.getPathArgs(Collections.<ReferencedFile>emptyList()),
                                           Utils.getNameArgs(library.getDependencies()),
                                           library instanceof Library && ((Library)library).isExport()?exportTarget:null,
                                           false,
                                           Utils.getPathArgs(library.getPublicHeaders()));
  }

  @Override
  public void writeCode ( final Writer writer, final String indent ) throws IOException
  {
    addLib.writeCode(writer, "");
  }

  private final SimpleAddInterfaceLibrary addLib;

  private static final SimpleArgument exportTarget = new Variable("MaslExportTarget").getReference();
}
