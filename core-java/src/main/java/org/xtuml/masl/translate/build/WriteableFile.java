//
// File: GeneratedFile.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.translate.build;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;


public interface WriteableFile
{

  public void writeCode ( final Writer writer ) throws IOException;

  public File getFile ();

  public default void writeToFile(final File directory) throws IOException
  {
    final Writer text = new StringWriter();
    writeCode(text);
    BuildSet.updateFile(new File(directory, getFile().getPath()), text);
  }

}
