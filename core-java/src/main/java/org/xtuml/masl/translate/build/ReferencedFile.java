//
// File: GeneratedFile.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.translate.build;

import java.io.File;
import java.util.Collections;
import java.util.Set;


public class ReferencedFile
{

  private final File file;
  private final FileGroup parent;

  public ReferencedFile ( final FileGroup parent, final String filename )
  {
    this(parent,new File(filename));
  }

  public ReferencedFile (final FileGroup parent, final File file )
  {
    this.parent = parent;
    this.file = file;
  }

  public File getFile ()
  {
    return file;
  }

  public FileGroup getParent ()
  {
    return parent;
  }

  public Set<FileGroup> getDependencies () { return Collections.emptySet(); }

}
