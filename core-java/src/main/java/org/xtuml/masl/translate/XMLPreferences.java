//
// File: XMLPreferences.java
//
// UK Crown Copyright (c) 2009. All Rights Reserved.
//
package org.xtuml.masl.translate;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.xtuml.masl.TranslatorParser;


public class XMLPreferences
    implements TranslatorPreferences
{

  public XMLPreferences ( final TranslatorParser translatorXML )
  {
    this.translatorXML = translatorXML;
  }

  @Override
  public List<String> getRunTranslators ()
  {
    return translatorXML.getTranslatorAddList();
  }

  @Override
  public List<String> getSkipTranslators ()
  {
    return translatorXML.getTranslatorSkipList();
  }

  @Override
  public Map<String, Properties> getTranslatorProperties ()
  {
    return translatorXML.getTranslatorProperties();
  }

  @Override
  public boolean isOverride ()
  {
    return translatorXML.isOverride();
  }


  private final TranslatorParser translatorXML;


  @Override
  public String getName ()
  {
    return "translator.xml";
  }

}
