//
// File: Comment.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.cppgen;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.xtuml.masl.utils.TextUtils;


/**
 * Represents a code comment. The comment will be formatted to wrap at the max
 * line length. Explicit newline characters in the text will be preserved. A
 * line of any character 'X' can be ruled in the comment adding a line
 * containing 'X---X' directly into the comment text, or by calling
 * {@link Comment#ruleLine}passing 'X' as the parameter.
 */
public class Comment extends Statement
{

  private final static String RULED_LINE = "---";

  /**
   * Creates a C++ style single line comment of the form
   * 
   * <br>{@code // comment}
   * 

   *          the text of the comment
   * @return the comment
   */
  public static Comment createComment ( final String text )
  {
    return new Comment(null, "//", text, null, true);
  }

  /**
   * Creates a C++ Style comment. This will be of the form
   * 
   * <br/>{@code // comment line 1} <br/>{@code // comment line 2}
   * 

   *          the text of the comment

   *          whether or not to wrap the comment at the maximum line length
   * @return the Comment object created
   */
  public static Comment createComment ( final String text, final boolean wrap )
  {
    return new Comment(null, "//", text, null, wrap);
  }

  /**
   * Creates a C Style comment. This will be of the form
   * 
   * <br/>{@code /*} <br/>
   * <code>&nbsp;* comment line 1</code> <br/>
   * <code>&nbsp;* comment line 2</code> <br/>
   * <code>&nbsp;*<code>{@code /}
   * 

   *          the text of the comment
   * @return the Comment object created
   */
  public static Comment createCStyleComment ( final String text )
  {
    return new Comment("/*", " *", text, " */", true);
  }

  /**
   * Creates a JavaDoc or DOxygen Style comment. This will be of the form
   * 
   * <br/>{@code /**} <br/>
   * <code>&nbsp;* comment line 1</code> <br/>
   * <code>&nbsp;* comment line 2</code> <br/>
   * <code>&nbsp;*<code>{@code /}
   * 

   *          the text of the comment
   * @return the Comment object created
   */
  public static Comment createDocComment ( final String text )
  {
    return new Comment("/**", " *", text, " */", true);
  }

  /**
   * Creates an inline comment. This will be of the form
   * 
   * <br/>{@code /* comment *}{@code /}
   * 

   *          the text of the comment
   * @return the Comment object created
   */
  public static Comment createInlineComment ( final String text )
  {
    return new Comment("/*", null, text, "*/", false);
  }

  /**
   * Constructs a comment
   * 

   *          The characters used to start the comment. These characters will be
   *          written on a line of their own at the start of the comment block.
   *          No start line will be written if null.

   *          The characters used to start each line of the comment text.

   *          The text contained in the comment. This will be wrapped at the max
   *          line length, and the continueComment used to start the next line.
   *          Any newlines in the text are preserved. Lines of the format X---X,
   *          where X is any character will be written as a whole line of 'X's.
   *          Eg. "line1\n*---*\nline2" would produce the following:
   * 
   *          <pre>
   * 
   * 
   * 
   *                 line1
   *                 ********************************************************************************
   *                 line2
   * 
   * 
   * </pre>
   * 

   *          The characters used to close the comment. These characters will be
   *          written on a line of their own at the end of the comment block. No
   *          finishing line will be written if null.

   *          whether or not to wrap the comment at the maximum line length
   */
  private Comment ( final String openComment,
                    final String startLine,
                    final String text,
                    final String closeComment,
                    final boolean wrap )
  {
    this.openComment = openComment;
    this.startLine = startLine;
    this.text = text;
    this.closeComment = closeComment;
    this.wrap = wrap;
  }

  @Override
  public String toString ()
  {
    final Writer writer = new StringWriter();
    try
    {
      write(writer, "", null);
    }
    catch ( final IOException e )
    {
      e.printStackTrace();
    }
    return writer.toString();
  }

  /**
   * Appends the supplied text to the comment text.
   * 

   *          the text to append
   */
  void appendText ( final String text )
  {
    this.text += text;
  }

  /**
   * Rules a line of the specified character across the comment.
   * 

   *          the character to use to draw the line
   */
  void ruleLine ( final char character )
  {
    if ( text.charAt(text.length() - 1) != '\n' )
    {
      text += "\n";
    }
    text += character + RULED_LINE + character;
  }

  @Override
  void write ( final Writer writer, final String indent, final Namespace currentNamespace ) throws IOException
  {
    TextUtils.textBlock(writer, indent, openComment, startLine, text, closeComment, wrap);
  }

  /**
   * The characters used to close the comment. These characters will be written
   * on a line of their own at the end of the comment block. No finishing line
   * will be written if null.
   */
  private final String  closeComment;

  /**
   * The characters used to start the comment. These characters will be written
   * on a line of their own at the start of the comment block. No start line
   * will be written if null.
   */
  private final String  openComment;

  /**
   * The characters used to start each line of the comment text.
   */
  private final String  startLine;

  /**
   * The text contained in the comment. This will be wrapped at the max line
   * length, and the continueComment used to start the next line. Any newlines
   * in the text are preserved. Lines of the format X---X, where X is any
   * character will be written as a whole line of 'X's. Eg.
   * "line1\n*---*\nline2" would produce the following:
   * 
   * <pre>
   * 
   * 
   * 
   *                 line1
   *                 ********************************************************************************
   *                 line2
   * 
   * 
   * </pre>
   */
  private String        text;

  private final boolean wrap;

}
