//
// File: JavaPreferences.java
//
// UK Crown Copyright (c) 2009. All Rights Reserved.
//
package org.xtuml.masl.translate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;


public class JavaPreferences
    implements TranslatorPreferences
{

  public JavaPreferences ( final Preferences node )
  {
    if ( node.isUserNode() )
    {
      name = "user preferences";
    }
    else
    {
      name = "system preferences";
    }

    try
    {
      for ( final String keyName : node.keys() )
      {
        if ( keyName.equals("override") )
        {
          override = node.getBoolean(keyName, false);
        }
        else
        {
          final String value = node.get(keyName, "");
          if ( value.equals("add") )
          {
            runTranslators.add(keyName);
          }
          else if ( value.equals("skip") )
          {
            skipTranslators.add(keyName);
          }
          else
          {
            System.err.println("Warning: Unrecognised action '" + value + "' in " + node.toString());
          }
        }
      }
    }
    catch ( final BackingStoreException e )
    {
      e.printStackTrace();
    }

  }

  @Override
  public List<String> getRunTranslators ()
  {
    return runTranslators;
  }

  @Override
  public List<String> getSkipTranslators ()
  {
    return skipTranslators;
  }

  @Override
  public Map<String, Properties> getTranslatorProperties ()
  {
    // At the moment the properties of a translator cannot be
    // set using java preferences so just return
    // an empty set.
    return Collections.emptyMap();
  }

  @Override
  public boolean isOverride ()
  {
    return override;
  }


  @Override
  public String getName ()
  {
    return name;
  }


  private final String       name;
  private boolean            override        = false;

  private final List<String> runTranslators  = new ArrayList<String>();

  private final List<String> skipTranslators = new ArrayList<String>();

}
