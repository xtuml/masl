//
// File: MaslFileReader.java
//
// UK Crown Copyright (c) 2009. All Rights Reserved.
//
package org.xtuml.masl.antlr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MaslFileReader
{

  public MaslFileReader ( final File file ) throws FileNotFoundException
  {
    this.file = file;
    fileReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.ISO_8859_1));
  }

  public File getFile ()
  {
    return file;
  }

  public String getFileLine ( final int line )
  {
    if ( line == 0 )
    {
      return "";
    }
    try
    {
      while ( lines.size() < line )
      {
        lines.add(fileReader.readLine());
      }
      return lines.get(line - 1);
    }
    catch ( final IOException e )
    {
      return "";
    }
  }

  private final File           file;
  private final BufferedReader fileReader;
  private final List<String>   lines = new ArrayList<String>();
}
