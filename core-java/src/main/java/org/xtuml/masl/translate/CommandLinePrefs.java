//
// File: CommandLinePrefs.java
//
// UK Crown Copyright (c) 2009. All Rights Reserved.
//
package org.xtuml.masl.translate;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.xtuml.masl.CommandLine;


public class CommandLinePrefs
    implements TranslatorPreferences
{

  @Override
  public List<String> getRunTranslators ()
  {
    return CommandLine.INSTANCE.getAddTranslators();
  }

  @Override
  public List<String> getSkipTranslators ()
  {
    return CommandLine.INSTANCE.getSkipTranslators();
  }


  @Override
  public Map<String, Properties> getTranslatorProperties ()
  {
    // At the moment the properties of a translator cannot be
    // set using command line preferences so just return
    // an empty set.
    return Collections.emptyMap();
  }

   @Override
  public boolean isOverride ()
  {
    return CommandLine.INSTANCE.isOverrideTranslators();
  }

  @Override
  public String getName ()
  {
    return "command line";
  }

}
