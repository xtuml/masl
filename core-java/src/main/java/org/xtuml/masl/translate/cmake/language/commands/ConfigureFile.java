//
// File: SetVariable.java
//
// UK Crown Copyright (c) 2015. All Rights Reserved.
//
package org.xtuml.masl.translate.cmake.language.commands;

import org.xtuml.masl.translate.cmake.language.arguments.SingleArgument;

import com.google.common.collect.Lists;


public class ConfigureFile extends Command
{
  public static ConfigureFile configure ( final SingleArgument input, final SingleArgument output )
  {
    return new ConfigureFile(input,output);
  }

  public static ConfigureFile configureCopyOnly ( final SingleArgument input, final SingleArgument output )
  {
    return new ConfigureFile(input,output,COPYONLY);
  }

  public static ConfigureFile configureAtOnly ( final SingleArgument input, final SingleArgument output )
  {
    return new ConfigureFile(input,output,ATONLY);
  }

  private ConfigureFile ( final SingleArgument input, final SingleArgument output )
  {
    super( "configure_file", Lists.newArrayList(input, output) );
  }

  private ConfigureFile ( final SingleArgument input, final SingleArgument output, final SingleArgument type )
  {
    super( "configure_file", Lists.newArrayList(input, output, type) );
  }

  private static SingleArgument COPYONLY = new SingleArgument("COPYONLY");
  private static SingleArgument ATONLY = new SingleArgument("@ONLY");
}
