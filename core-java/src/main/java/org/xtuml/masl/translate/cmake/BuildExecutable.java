//
// File: ImakeMacro.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.translate.cmake;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

import org.xtuml.masl.cppgen.Executable;
import org.xtuml.masl.translate.cmake.functions.SimpleAddExecutable;


public class BuildExecutable
    implements CMakeListsItem
{

  public BuildExecutable ( final Executable exe, final File sourcePath )
  {
    addLib = new SimpleAddExecutable(Utils.getNameArg(exe),
                                        Utils.getPathArgs(exe.getFiles()),
                                        Utils.getNameArgs(exe.getDependencies()),exe.isExport());

  }

  @Override
  public void writeCode ( final Writer writer, final String indent ) throws IOException
  {
    addLib.writeCode(writer, "");
  }

  private final SimpleAddExecutable addLib;


}
