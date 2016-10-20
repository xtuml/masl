// 
// Filename : Preferences.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;


public class Preferences
{

  private final static String     modellingModeLabel     = "inspector.modellingMode";
  private final static String     dateFormatLabel        = "inspector.dateFormat";
  private final static String     timestampFormatLabel   = "inspector.timestampFormat";
  private final static String     fontSizeLabel          = "inspector.fontSize";
  private final static String     codeFontSizeLabel      = "inspector.codeFontSize";
  private final static String     fontNameLabel          = "inspector.fontName";
  private final static String     codeFontNameLabel      = "inspector.codeFontName";
  private final static String     aboveContextLinesLabel = "inspector.aboveContextLines";
  private final static String     belowContextLinesLabel = "inspector.belowContextLines";
  private final static String     useStackBrowserLabel   = "inspector.useStackBrowser";
  private final static String     ignoredDomainsLabel    = "inspector.ignoredDomains";
  private final static String     traceLinesLabel        = "inspector.traceLines";
  private final static String     traceBlocksLabel       = "inspector.traceBlocks";
  private final static String     traceEventsLabel       = "inspector.traceEvents";
  private final static String     traceExceptionsLabel   = "inspector.traceExceptions";
  private final static String     stepLinesLabel         = "inspector.stepLines";
  private final static String     stepBlocksLabel        = "inspector.stepBlocks";
  private final static String     stepExceptionsLabel    = "inspector.stepExceptions";
  private final static String     stepEventsLabel        = "inspector.stepEvents";
  private final static String     enableTimersLabel      = "inspector.enableTimers";
  private final static String     catchConsoleLabel      = "inspector.catchConsole";
  private final static String     sortLocalVarsLabel     = "inspector.sortLocalVars";
  private final static String     showInternalIdLabel    = "inspector.showInternalId";

  private final static Properties prefs                  = new Properties();

  static
  {
    prefs.setProperty(modellingModeLabel, "UML");
    prefs.setProperty(dateFormatLabel, "");
    prefs.setProperty(timestampFormatLabel, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    prefs.setProperty(fontSizeLabel, "11");
    prefs.setProperty(codeFontSizeLabel, "11");
    prefs.setProperty(fontNameLabel, "dialog");
    prefs.setProperty(codeFontNameLabel, "monospaced");
    prefs.setProperty(aboveContextLinesLabel, "5");
    prefs.setProperty(belowContextLinesLabel, "10");
    prefs.setProperty(useStackBrowserLabel, "true");
    prefs.setProperty(ignoredDomainsLabel, "Text_IO,Calendar,Device_IO");
    prefs.setProperty(traceLinesLabel, "false");
    prefs.setProperty(traceBlocksLabel, "false");
    prefs.setProperty(traceEventsLabel, "false");
    prefs.setProperty(traceExceptionsLabel, "true");
    prefs.setProperty(stepLinesLabel, "false");
    prefs.setProperty(stepBlocksLabel, "false");
    prefs.setProperty(stepExceptionsLabel, "false");
    prefs.setProperty(stepEventsLabel, "false");
    prefs.setProperty(enableTimersLabel, "true");
    prefs.setProperty(catchConsoleLabel, "true");
    prefs.setProperty(sortLocalVarsLabel, "true");
    prefs.setProperty(showInternalIdLabel, "false");
  }


  public static void savePreferences ( final File file ) throws IOException
  {
    final OutputStream f = new FileOutputStream(file);
    prefs.store(f, "Inspector Preferences");
    f.close();
  }


  public static void loadPreferences ( final File file ) throws IOException
  {
    final InputStream f = new FileInputStream(file);
    prefs.load(f);
    f.close();
  }


  static
  {
    try
    {
      final File f = new File(System.getProperty("user.home") + System.getProperty("file.separator") + ".inspector");
      if ( f.exists() )
      {
        // Load user specified preferences to override defaults
        loadPreferences(f);
      }

      // Save all preferences (including defaults) so that any new options
      // appear in the file.
      savePreferences(f);
    }
    catch ( final IOException e )
    {
      e.printStackTrace();
    }
  }


  public static class ModellingMode
      implements java.io.Serializable
  {

    public static final ModellingMode UML = new ModellingMode("UML");
    public static final ModellingMode SM  = new ModellingMode("SM");

    private ModellingMode ( final String text )
    {
      this.text = text;
    }

    private final String text;

    @Override
    public String toString ()
    {
      return text;
    }
  }

  public static ModellingMode getModellingMode ()
  {
    return prefs.getProperty(modellingModeLabel).equals("SM") ? ModellingMode.SM : ModellingMode.UML;
  }

  public static int getFontSize ()
  {
    return Integer.parseInt(prefs.getProperty(fontSizeLabel));
  }

  public static int getCodeFontSize ()
  {
    return Integer.parseInt(prefs.getProperty(codeFontSizeLabel));
  }

  public static String getFontName ()
  {
    return prefs.getProperty(fontNameLabel);
  }

  public static String getCodeFontName ()
  {
    return prefs.getProperty(codeFontNameLabel);
  }

  public static int getAboveContextLines ()
  {
    return Integer.parseInt(prefs.getProperty(aboveContextLinesLabel));
  }

  public static int getBelowContextLines ()
  {
    return Integer.parseInt(prefs.getProperty(belowContextLinesLabel));
  }

  public static String getDateFormat ()
  {
    return prefs.getProperty(dateFormatLabel);
  }

  public static String getTimestampFormat ()
  {
    return prefs.getProperty(timestampFormatLabel);
  }

  public static boolean getUseStackBrowser ()
  {
    return new Boolean(prefs.getProperty(useStackBrowserLabel)).booleanValue();
  }

  public static Set<String> getIgnoredDomains ()
  {
    final Set<String> ignoredDomains = new HashSet<String>();
    final StringTokenizer tokenizer = new StringTokenizer(prefs.getProperty(ignoredDomainsLabel), ",");

    while ( tokenizer.hasMoreTokens() )
    {
      ignoredDomains.add(tokenizer.nextToken());
    }
    return ignoredDomains;
  }

  public static boolean getTraceLines ()
  {
    return new Boolean(prefs.getProperty(traceLinesLabel)).booleanValue();
  }

  public static boolean getTraceBlocks ()
  {
    return new Boolean(prefs.getProperty(traceBlocksLabel)).booleanValue();
  }

  public static boolean getTraceEvents ()
  {
    return new Boolean(prefs.getProperty(traceEventsLabel)).booleanValue();
  }

  public static boolean getTraceExceptions ()
  {
    return new Boolean(prefs.getProperty(traceExceptionsLabel)).booleanValue();
  }

  public static boolean getStepLines ()
  {
    return new Boolean(prefs.getProperty(stepLinesLabel)).booleanValue();
  }

  public static boolean getStepBlocks ()
  {
    return new Boolean(prefs.getProperty(stepBlocksLabel)).booleanValue();
  }

  public static boolean getStepExceptions ()
  {
    return new Boolean(prefs.getProperty(stepExceptionsLabel)).booleanValue();
  }

  public static boolean getStepEvents ()
  {
    return new Boolean(prefs.getProperty(stepEventsLabel)).booleanValue();
  }

  public static boolean getEnableTimers ()
  {
    return new Boolean(prefs.getProperty(enableTimersLabel)).booleanValue();
  }

  public static boolean getCatchConsole ()
  {
    return new Boolean(prefs.getProperty(catchConsoleLabel)).booleanValue();
  }

  public static boolean getSortLocalVars ()
  {
    return new Boolean(prefs.getProperty(sortLocalVarsLabel)).booleanValue();
  }

  public static boolean getShowInternalId ()
  {
    return new Boolean(prefs.getProperty(showInternalIdLabel)).booleanValue();
  }

}
