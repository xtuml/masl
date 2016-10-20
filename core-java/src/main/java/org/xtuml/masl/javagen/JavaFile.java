//
// File: JavaFile.java
//
// UK Crown Copyright (c) 2015. All Rights Reserved.
//
package org.xtuml.masl.javagen;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

import org.xtuml.masl.javagen.ast.def.CompilationUnit;
import org.xtuml.masl.translate.build.FileGroup;
import org.xtuml.masl.translate.build.ReferencedFile;
import org.xtuml.masl.translate.build.WriteableFile;

public class JavaFile extends ReferencedFile
    implements WriteableFile
{

  public JavaFile ( final CompilationUnit cu, final FileGroup jarFile )
  {
    super(jarFile,new File("javasource" + "/" + jarFile.getName() + "/" + cu.getFileName()));
    this.cu = cu;
  }

  private final CompilationUnit cu;

  @Override
  public void writeCode ( final Writer writer ) throws IOException
  {
    try
    {
      new CodeWriter().writeCode(writer, cu);
    }
    catch ( final IOException e )
    {
      throw e;
    }
    catch ( final Exception e )
    {
      e.printStackTrace();
    }
  }

}