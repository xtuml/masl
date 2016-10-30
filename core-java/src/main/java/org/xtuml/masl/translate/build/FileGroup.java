//
// File: Library.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.translate.build;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;


public class FileGroup
{

  private static Map<String, FileGroup> lookup = new HashMap<String, FileGroup>();

  public static FileGroup getFileGroup ( final String name )
  {
    FileGroup result = lookup.get(name);
    if ( result == null )
    {
      result = new FileGroup(name);
      lookup.put(name, result);
    }
    return result;
  }

  protected FileGroup ( final String name )
  {
    this.name = name;
  }

  public <G extends FileGroup> G addDependency ( final G dependent )
  {
    dependencies.add(dependent);
    return dependent;
  }

  public void removeDependency ( final FileGroup dependent )
  {
    dependencies.remove(dependent);
  }

  public <F extends ReferencedFile> F addFile ( final F file )
  {
    files.put(file.getFile(),file);
    return file;
  }

  public void addLibPath ( final String name )
  {
    libPaths.add(name);
  }


  public void skipDependency ( final String name )
  {
    skipDeps.add(name);
  }
  public void skipFile ( final File file )
  {
    skipFiles.add(file);
  }

  public Set<FileGroup> getDependencies ()
  {
    final Set<FileGroup> result = new LinkedHashSet<FileGroup>(dependencies);
    for ( final FileGroup group : includedGroups )
    {
      result.addAll(group.getDependencies());
    }
    for ( final ReferencedFile file : files.values() )
    {
      result.addAll(file.getDependencies());
    }
    result.removeAll(skipDeps);
    return Collections.unmodifiableSet(result);
  }

  public Set<ReferencedFile> getFiles ()
  {
    final Set<ReferencedFile> allFiles = new LinkedHashSet<ReferencedFile>(files.values());
    for ( final FileGroup group : includedGroups )
    {
      allFiles.addAll(group.getFiles());
    }

    final Set<ReferencedFile> result = new LinkedHashSet<ReferencedFile>(allFiles);

    for ( final ReferencedFile file : allFiles )
    {
      if ( skipFiles.contains(file.getFile()) )
      {
        result.remove(file);
      }
    }

    return Collections.unmodifiableSet(result);
  }

  public Set<String> getLibPaths ()
  {
    final Set<String> result = new LinkedHashSet<String>(libPaths);
    for ( final FileGroup group : includedGroups )
    {
      result.addAll(group.getLibPaths());
    }

    return Collections.unmodifiableSet(result);
  }

  public String getName ()
  {
    return name;
  }

  public void includeGroup ( final FileGroup group )
  {
    includedGroups.add(group);
  }

  private final Map<File,ReferencedFile> files      = new LinkedHashMap<>();


  private final String              name;

  private final Set<String>         libPaths       = new LinkedHashSet<>();

  private final Set<FileGroup>      dependencies   = new LinkedHashSet<>();

  private final Set<FileGroup>      includedGroups = new LinkedHashSet<>();

  private final Set<File>           skipFiles      = new LinkedHashSet<>();
  private final Set<String>         skipDeps       = new LinkedHashSet<>();

  private BuildSet parent;

  public BuildSet getParent ()
  {
    return parent;
  }

  public void setParent ( final BuildSet parent )
  {
    this.parent = parent;
  }
}
