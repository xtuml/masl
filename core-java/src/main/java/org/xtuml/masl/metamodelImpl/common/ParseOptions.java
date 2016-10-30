//
// File: ParseOptions.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.common;

import java.util.prefs.Preferences;


public class ParseOptions
{

  public static boolean defaultToNonAssocNavigate ()
  {
    return getBooleanOption("DefaultToNonAssocNavigate", false);
  }

  private static boolean getBooleanOption ( final String name, final boolean defaultValue )
  {
    return Preferences.userRoot().node("/masl/options").getBoolean(name,
                                                                   Preferences.systemRoot()
                                                                              .node("/masl/options")
                                                                              .getBoolean(name, defaultValue));
  }
}
