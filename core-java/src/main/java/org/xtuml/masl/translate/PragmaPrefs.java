//
// File: PragmaPrefs.java
//
// UK Crown Copyright (c) 2009. All Rights Reserved.
//
package org.xtuml.masl.translate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.xtuml.masl.metamodel.common.PragmaList;


public class PragmaPrefs
    implements TranslatorPreferences
{

  static private final String RUN_TRANSLATOR      = "RunTranslator";
  static private final String SKIP_TRANSLATOR     = "SkipTranslator";
  static private final String OVERRIDE_TRANSLATOR = "OverrideTranslator";
  static private final String ONLY_TRANSLATOR     = "OnlyTranslator";

  public PragmaPrefs ( final PragmaList pragmas )
  {
    this.pragmas = pragmas;
  }

  @Override
  public List<String> getRunTranslators ()
  {
    final List<String> result = new ArrayList<String>(getList(ONLY_TRANSLATOR));
    result.addAll(getList(RUN_TRANSLATOR));
    return result;
  }

  @Override
  public List<String> getSkipTranslators ()
  {
    return getList(SKIP_TRANSLATOR);
  }

  @Override
  public Map<String, Properties> getTranslatorProperties ()
  {
    // At the moment the properties of a translator cannot be
    // set using pragma preferences so just return
    // an empty set.
    return Collections.emptyMap();
  }

  @Override
  public boolean isOverride ()
  {
    for ( final String value : getList(OVERRIDE_TRANSLATOR) )
    {
      if ( value.equals("true") )
      {
        return true;
      }
    }
    return getList(ONLY_TRANSLATOR).size() > 0;
  }

  private final List<String> getList ( final String key )
  {
    final List<String> result = pragmas.getPragmaValues(key);
    return result == null ? Collections.<String>emptyList() : result;
  }

  private final PragmaList pragmas;


  @Override
  public String getName ()
  {
    return "pragmas";
  }

}
