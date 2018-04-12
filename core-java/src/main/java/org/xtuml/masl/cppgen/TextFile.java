/*
 * Filename : TextFile.java
 *
 * UK Crown Copyright (c) 2008. All Rights Reserved
 */
package org.xtuml.masl.cppgen;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;

import org.xtuml.masl.CommandLine;
import org.xtuml.masl.translate.build.FileGroup;
import org.xtuml.masl.translate.build.ReferencedFile;
import org.xtuml.masl.translate.build.WriteableFile;


public class TextFile extends ReferencedFile
    implements Comparable<TextFile>, WriteableFile
{

  private String             commentCharacters;
  private final StringWriter bufferedText;
  private boolean            copyRightEnabled;

  public TextFile ( final FileGroup parent, final File file )
  {
    super(parent,file);
    copyRightEnabled = false;
    bufferedText = new StringWriter();
  }

  public TextFile ( final FileGroup parent, final String filename )
  {
    this(parent, new File(filename));
  }

  public void enableCopyright ( final String commentCharacters )
  {
    this.commentCharacters = commentCharacters;
    copyRightEnabled = true;
  }

  public StringBuffer getBuffer ()
  {
    return bufferedText.getBuffer();
  }

  public StringWriter getWriter ()
  {
    return bufferedText;
  }

  @Override
  public int compareTo ( final TextFile rhs )
  {
    return getFile().compareTo(rhs.getFile());
  }

  @Override
  public int hashCode ()
  {
    return getFile().hashCode();
  }


  @Override
  public void writeCode ( final Writer writer ) throws IOException
  {
    if ( copyRightEnabled )
    {
      String copyrightNotice = CommandLine.INSTANCE.getCopyrightNotice();
      writer.write(
            commentCharacters + " File: " + getFile().getPath() + "\n"
                + ( null == copyrightNotice ? "" : commentCharacters + "\n" + commentCharacters + " " + copyrightNotice.replaceAll("\n", "\n" + commentCharacters + " ") + "\n" )
                + commentCharacters + "\n");
    }
    writer.write(bufferedText.toString());
  }
}
