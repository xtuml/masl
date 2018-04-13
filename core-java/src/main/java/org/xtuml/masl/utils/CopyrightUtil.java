package org.xtuml.masl.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class CopyrightUtil {

  /* Reads a text file and returns its contents with leading and trailing
   * whitespace removed. If the exact string "yyyy" exists within the file,
   * it will be replaced with the four digit representation of the current
   * calendar year. If the file does not exist, or an error occurs in reading
   * the file, null will be returned. Referring implementations should
   * consider a return value of null to mean there is no copyright notice.
   */
  public static String getCopyrightNotice( String filepath ) {
    String copyrightNotice = getRawCopyrightNotice( filepath );
    if ( null != copyrightNotice ) {
      copyrightNotice = copyrightNotice.trim().replaceAll( "yyyy", yearFormatter.format(new Date()) );
    }
    return copyrightNotice;
  }

  /* Same behavior as 'getCopyrightNotice' except "yyyy" is not replaced
   * with the current calendar year.
   */
  public static String getRawCopyrightNotice( String filepath ) {
    File copyrightFile = new File( filepath );
    String copyrightNotice = null;
    if ( copyrightFile.exists() ) {
      try {
        BufferedReader fileReader = new BufferedReader( new InputStreamReader( new FileInputStream( copyrightFile ), StandardCharsets.ISO_8859_1 ) );
        String line = "";
        while ( (line = fileReader.readLine()) != null ) {
          if ( null == copyrightNotice ) copyrightNotice = "";
          copyrightNotice += line + "\n";
        }
        if ( null != copyrightNotice ) {
          copyrightNotice = copyrightNotice.trim();
        }
      }
      catch ( Exception e ) {}
    }
    return copyrightNotice;
  }

  private static java.text.SimpleDateFormat yearFormatter = new java.text.SimpleDateFormat("yyyy");

}
