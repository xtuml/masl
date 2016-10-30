//
// File: Library.java
//
// UK Crown Copyright (c) 2015. All Rights Reserved.
//
package org.xtuml.masl.cppgen;

import static org.xtuml.masl.cppgen.CodeFile.Type.BODY;
import static org.xtuml.masl.cppgen.CodeFile.Type.INTERFACE_HEADER;
import static org.xtuml.masl.cppgen.CodeFile.Type.PRIVATE_HEADER;
import static org.xtuml.masl.cppgen.CodeFile.Type.PUBLIC_HEADER;
import static org.xtuml.masl.cppgen.CodeFile.Type.SYSTEM_HEADER;

import java.io.File;

import org.xtuml.masl.translate.build.BuildSet;
import org.xtuml.masl.translate.build.FileGroup;

import com.google.common.collect.Iterables;


public class Library extends FileGroup
{

  public Library ( final String name )
  {
    super(name);
  }

  public Library asPrivate ()
  {
    export = false;
    return this;
  }

  public CodeFile createBodyFile ( final File path, final String name )
  {
    return createCodeFile(path, name + bodyExtension, BODY);
  }

  public CodeFile createBodyFile ( final String name )
  {
    return createBodyFile(defaultBodyPath, name);
  }

  public CodeFile createInterfaceHeader ( final File path, final String name )
  {
    return createCodeFile(path, name + headerExtension, INTERFACE_HEADER);
  }

  public CodeFile createInterfaceHeader ( final String name )
  {
    return createInterfaceHeader(defaultInterfaceHeaderPath, name);
  }

  public CodeFile createPrivateHeader ( final File path, final String name )
  {
    return createCodeFile(path, name + headerExtension, PRIVATE_HEADER);
  }

  public CodeFile createPrivateHeader ( final String name )
  {
    return createPrivateHeader(defaultPrivateHeaderPath, name);
  }

  public CodeFile createPublicHeader ( final File path, final String name )
  {
    return createCodeFile(path, name + headerExtension, PUBLIC_HEADER);
  }

  public CodeFile createPublicHeader ( final String name )
  {
    return createPublicHeader(defaultPublicHeaderPath, name);
  }

  public CodeFile createSystemHeader ( final File path, final String name )
  {
    return createCodeFile(path, name + headerExtension, SYSTEM_HEADER);
  }

  public CodeFile createSystemHeader ( final String name )
  {
    return createSystemHeader(defaultPublicHeaderPath, name);
  }

  public String getBodyExtension ()
  {
    return bodyExtension;
  }

  public File getDefaultBodyPath ()
  {
    return defaultBodyPath;
  }

  public File getDefaultInterfaceHeaderPath ()
  {
    return defaultInterfaceHeaderPath;
  }

  public File getDefaultPrivateHeaderPath ()
  {
    return defaultPublicHeaderPath;
  }

  public File getDefaultPublicHeaderPath ()
  {
    return defaultPublicHeaderPath;
  }

  public File getDefaultSystemHeaderPath ()
  {
    return defaultSystemHeaderPath;
  }

  public String getHeaderExtension ()
  {
    return headerExtension;
  }

  public Library inBuildSet ( final BuildSet buildSet )
  {
    buildSet.addFileGroup(this);
    setParent(buildSet);
    return this;
  }

  public boolean isDump ()
  {
    return dump;
  }

  public boolean isExport ()
  {
    return export;
  }


  public Library noDump ()
  {
    dump = false;
    return this;
  }

  public void setBodyExtension ( final String bodyExtension )
  {
    this.bodyExtension = bodyExtension;
  }

  public void setDefaultBodyPath ( final File path )
  {
    this.defaultBodyPath = path;
  }

  public void setDefaultInterfaceHeaderPath ( final File path )
  {
    this.defaultInterfaceHeaderPath = path;
  }


  public void setDefaultPrivateHeaderPath ( final File path )
  {
    this.defaultPrivateHeaderPath = path;
  }


  public void setDefaultPublicHeaderPath ( final File path )
  {
    this.defaultPublicHeaderPath = path;
  }

  public void setDefaultSystemHeaderPath ( final File path )
  {
    this.defaultSystemHeaderPath = path;
  }


  public void setDump ( final boolean dump )
  {
    this.dump = dump;
  }


  public void setExport ( final boolean export )
  {
    this.export = export;
  }


  public void setHeaderExtension ( final String headerExtension )
  {
    this.headerExtension = headerExtension;
  }


  public Library withCCDefaultExtensions ()
  {
    bodyExtension = ".cc";
    headerExtension = ".hh";
    return this;
  }


  public Library withCDefaultExtensions ()
  {
    bodyExtension = ".c";
    headerExtension = ".h";
    return this;
  }

  public Library withCppDefaultExtensions ()
  {
    bodyExtension = ".cpp";
    headerExtension = ".hpp";
    return this;
  }

  public Library withCxxDefaultExtensions ()
  {
    bodyExtension = ".cxx";
    headerExtension = ".hxx";
    return this;
  }

  public Library withDefaultBodyExtension ( final String extension )
  {
    bodyExtension = extension;
    return this;
  }


  public Library withDefaultBodyPath ( final String path )
  {
    setDefaultPrivateHeaderPath(new File(path));
    return this;
  }

  public Library withDefaultHeaderExtension ( final String extension )
  {
    headerExtension = extension;
    return this;
  }

  public Library withDefaultHeaderPath ( final String path )
  {
    return withDefaultInterfaceHeaderPath(path).withDefaultPublicHeaderPath(path);
  }

  public Library withDefaultInterfaceHeaderPath ( final String path )
  {
    setDefaultInterfaceHeaderPath(new File(path));
    return this;
  }

  public Library withDefaultPrivateHeaderPath ( final String path )
  {
    setDefaultPrivateHeaderPath(new File(path));
    return this;
  }

  public Library withDefaultPublicHeaderPath ( final String path )
  {
    setDefaultPublicHeaderPath(new File(path));
    return this;
  }

  public Library withDefaultSystemHeaderPath ( final String path )
  {
    setDefaultSystemHeaderPath(new File(path));
    return this;
  }

  private CodeFile createCodeFile ( final File fullPath, final CodeFile.Type type )
  {
    final CodeFile codeFile = new CodeFile(this, fullPath, type);
    addFile(codeFile);
    return codeFile;
  }

  private CodeFile createCodeFile ( final File path, final String filename, final CodeFile.Type type )
  {
    return createCodeFile(new File(path, filename), type);
  }

  public Iterable<CodeFile> getPublicHeaders()
  {
    return Iterables.filter(Iterables.filter(getFiles(),CodeFile.class),f -> f.isPublicHeader());
  }

  public Iterable<CodeFile> getBodyFiles()
  {
    return Iterables.filter(Iterables.filter(getFiles(),CodeFile.class),f -> f.isBodyFile());
  }

  private String  bodyExtension              = "";
  private File    defaultBodyPath            = null;
  private File    defaultInterfaceHeaderPath = null;
  private File    defaultPrivateHeaderPath   = null;
  private File    defaultPublicHeaderPath    = null;
  private File    defaultSystemHeaderPath    = null;

  private boolean dump                       = true;
  private boolean export                     = true;

  private String  headerExtension            = "";


}
