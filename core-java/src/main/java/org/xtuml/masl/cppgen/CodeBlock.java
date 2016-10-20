//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
package org.xtuml.masl.cppgen;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

import org.xtuml.masl.utils.TextUtils;


/**
 * Represents a scoped block of C++ statements, delimited by curly braces. For
 * example <code>
 {
 statement1;
 statement1;
 }
 </code>
 */
public class CodeBlock extends Statement
{


  /**
   * Creates an empty code block
   */
  public CodeBlock ()
  {
    this(null);
  }

  public CodeBlock ( final Comment comment )
  {
    if ( comment != null )
    {
      comment.setParent(this);
    }
    this.comment = comment;
  }

  /**
   * Adds the supplied expression to the end of the code block as a statement
   * 

   *          The expression to append
   */
  public void appendExpression ( final Expression expression )
  {
    final Statement statement = new ExpressionStatement(expression);
    statement.setParent(this);
    statements.addLast(statement);
  }

  /**
   * Adds the supplied statement to the end of the code block
   * 

   *          The statement to append
   */
  public void appendStatement ( final Statement statement )
  {
    statement.setParent(this);
    statements.addLast(statement);
  }

  /**
   * Adds the supplied expression to the start of the code block as a statement
   * 

   *          The expression to append
   */
  public void prependExpression ( final Expression expression )
  {
    final Statement statement = new ExpressionStatement(expression);
    statement.setParent(this);
    statements.addFirst(statement);
  }

  /**
   * Adds the supplied statement to the start of the code block
   * 

   *          The statement to append
   */
  public void prependStatement ( final Statement statement )
  {
    statement.setParent(this);
    statements.addFirst(statement);
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

  @Override
  Set<Declaration> getForwardDeclarations ()
  {
    final Set<Declaration> result = new LinkedHashSet<Declaration>();
    for ( final Statement statement : statements )
    {
      result.addAll(statement.getForwardDeclarations());
    }
    return result;
  }

  @Override
  Set<CodeFile> getIncludes ()
  {
    final Set<CodeFile> result = new LinkedHashSet<CodeFile>();
    for ( final Statement statement : statements )
    {
      result.addAll(statement.getIncludes());
    }
    return result;
  }

  @Override
  /**
   * {@inheritDoc}The curly braces around the block will each appear on their
   * own line indented by the supplied indent. Each statement in the block will
   * be indented by and extra indent as supplied by
   * {@link TextUtils#getIndent()}.
   * 

   *          {@inheritDoc}

   *          {@inheritDoc}

   *          {@inheritDoc}
   * @throws IOException
   */
  void write ( final Writer writer, final String indent, final Namespace currentNamespace ) throws IOException
  {
    write(writer, indent, currentNamespace, false);
  }

  /**
   * Writes the code block to the supplied Writer. The curly braces around the
   * block will each appear on their own line indented by the supplied indent.
   * Each statement in the block will be indented by and extra indent as
   * supplied by {@link TextUtils#getIndent()}. If <code>trysameLine</code> is
   * set, then an attempt will be made to put the entire block on a single line
   * surrounded by the curly braces, if this is not possible becasue the line is
   * too long, then the default behaviour will be used.
   * 

   *          the Writer to write the code to

   *          the initial indent for the code block

   *          the current namespace in effect for this code block

   *          if set, then an attempt will be made to put the whole code block
   *          on one line. This is typically used for inline functions or very
   *          simple if statements.
   * @throws IOException
   */
  void write ( final Writer writer, final String indent, final Namespace currentNamespace, final boolean trySameLine ) throws IOException
  {
    if ( comment != null )
    {
      writer.write("\n");
      comment.write(writer, indent, currentNamespace);
      writer.write("\n");
    }
    if ( trySameLine )
    {
      if ( statements.size() == 1 )
      {
        writer.write(" { ");
        statements.get(0).write(writer, "", currentNamespace);
        writer.write(" }");
      }
      else
      {
        writer.write("\n" + indent + "{\n");
        for ( final Statement statement : statements )
        {
          statement.write(writer, indent + TextUtils.getIndent(), currentNamespace);
          if ( !(statement instanceof StatementGroup) )
          {
            writer.write("\n");
          }
        }
        writer.write(indent + "}");
      }
    }
    else
    {
      writer.write(indent + "{\n");
      for ( final Statement statement : statements )
      {
        statement.write(writer, indent + TextUtils.getIndent(), currentNamespace);
        if ( !(statement instanceof StatementGroup) )
        {
          writer.write("\n");
        }
      }
      writer.write(indent + "}");
    }
  }

  /**
   * The list of statements in this code block
   */
  private final Comment               comment;
  private final LinkedList<Statement> statements = new LinkedList<Statement>();

  public void clear ()
  {
    statements.clear();
  }

}
