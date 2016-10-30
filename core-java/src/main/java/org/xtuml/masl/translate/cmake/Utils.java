//
// File: Utils.java
//
// UK Crown Copyright (c) 2015. All Rights Reserved.
//
package org.xtuml.masl.translate.cmake;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.xtuml.masl.translate.build.FileGroup;
import org.xtuml.masl.translate.build.ReferencedFile;
import org.xtuml.masl.translate.cmake.language.arguments.SingleArgument;


public class Utils
{


  public static List<SingleArgument> getPathArgs ( final Iterable<? extends ReferencedFile> files )
  {
    return StreamSupport.stream(files.spliterator(),false).map(Utils::getPathArg).collect(Collectors.toList());
  }

  public static List<SingleArgument> getNameArgs ( final Iterable<? extends FileGroup> targets )
  {
    return StreamSupport.stream(targets.spliterator(),false).map(Utils::getNameArg).filter(t -> t != null).collect(Collectors.toList());
  }

  public static SingleArgument getNameArg ( final FileGroup target )
  {
    return target.getName()== null?null:new SingleArgument((target.getParent() != null && target.getParent().getName() != null? target.getParent().getName() + "::":"") + target.getName());
  }

  public static SingleArgument getPathArg ( final ReferencedFile file )
  {
    return getPathArg(file.getFile());
  }

  public static SingleArgument getPathArg ( final File file )
  {
    return new SingleArgument(file.getPath());
  }
}
