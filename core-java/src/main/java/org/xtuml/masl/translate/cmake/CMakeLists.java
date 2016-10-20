//
// File: CMakeLists.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.translate.cmake;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

import org.xtuml.masl.cppgen.TextFile;

public class CMakeLists extends TextFile
{

  public CMakeLists ()
  {
    this("CMakeLists.txt");
  }

  public CMakeLists ( final String filename )
  {
    super(null,filename);
  }

  @Override
  public void writeCode ( final Writer writer ) throws IOException
  {
    super.writeCode(writer);

    items.writeCode(writer,"");
  }

  public void add ( final CMakeListsItem item )
  {
    items.add(item);
  }

  public Iterator<CMakeListsItem> iterator ()
  {
    return items.iterator();
  }

  private final Section items = new Section();

}
