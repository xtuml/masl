//
// File: ListPrefs.java
//
// UK Crown Copyright (c) 2009. All Rights Reserved.
//
package org.xtuml.masl.translate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;


public class ListPrefs
    implements TranslatorPreferences
{

  public ListPrefs ( final String name, final String... runTranslators )
  {
    this.runTranslators = runTranslators;
    this.name = name;
  }

  @Override
  public List<String> getRunTranslators ()
  {
    return Arrays.asList(runTranslators);
  }


  @Override
  public Map<String, Properties> getTranslatorProperties ()
  {
    // At the moment the properties of a translator cannot be
    // set using list preferences so just return
    // an empty set.
    return Collections.emptyMap();
  }

  @Override
  public List<String> getSkipTranslators ()
  {
    return Collections.<String>emptyList();
  }

  @Override
  public boolean isOverride ()
  {
    return false;
  }

  private final String[] runTranslators;


  @Override
  public String getName ()
  {
    return name;
  }

  private final String name;
}
