//
// File: CommandLineBuildPrefs.java
//
// UK Crown Copyright (c) 2009. All Rights Reserved.
//
package org.xtuml.masl.translate;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.xtuml.masl.CommandLine;


public class CommandLineBuildPrefs
    implements TranslatorPreferences
{

  @Override
  public List<String> getRunTranslators ()
  {
    return CommandLine.INSTANCE.getBuildTranslator() == null ? Collections.<String>emptyList()
                                                   : Collections
                                                                .<String>singletonList(CommandLine.INSTANCE.getBuildTranslator());
  }

  @Override
  public List<String> getSkipTranslators ()
  {
    return Collections.<String>emptyList();
  }

  @Override
  public Map<String, Properties> getTranslatorProperties ()
  {
    // At the moment the properties of a translator cannot be
    // set using command line build preferences so just return
    // an empty set.
    return Collections.emptyMap();
  }

  @Override
  public boolean isOverride ()
  {
    return CommandLine.INSTANCE.getBuildDisable();
  }

  @Override
  public String getName ()
  {
    return "command line";
  }
}
