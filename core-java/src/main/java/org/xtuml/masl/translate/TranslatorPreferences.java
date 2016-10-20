//
// File: TranslatorPreferences.java
//
// UK Crown Copyright (c) 2009. All Rights Reserved.
//
package org.xtuml.masl.translate;

import java.util.List;
import java.util.Map;
import java.util.Properties;


public interface TranslatorPreferences
{

  abstract List<String> getRunTranslators ();

  abstract List<String> getSkipTranslators ();

  abstract Map<String, Properties> getTranslatorProperties ();

  abstract boolean isOverride ();

  abstract String getName ();
}
