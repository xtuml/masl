//
// File: ExpressionStatement.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.cppgen;

import java.io.IOException;
import java.io.Writer;
import java.util.Set;

import org.xtuml.masl.utils.TextUtils;



public class WhileStatement extends Statement
{

  private final Expression expression;
  private final Statement  statement;

  public WhileStatement ( final Expression expression, Statement statement )
  {
    if ( statement instanceof StatementGroup )
    {
      final CodeBlock tmp = new CodeBlock();
      tmp.appendStatement(statement);
      statement = tmp;
    }

    statement.setParent(this);

    this.expression = expression;
    this.statement = statement;

  }

  @Override
  /**
   * 


   * @throws IOException
   * @see org.xtuml.masl.cppgen.Statement#write(java.io.Writer, java.lang.String)
   */
  void write ( final Writer writer, final String indent, final Namespace currentNamespace ) throws IOException
  {
    writer.write(TextUtils.alignTabs(indent + "while ( " + expression.getCode(currentNamespace) + " )"));
    if ( statement instanceof CodeBlock )
    {
      writer.write("\n");
      statement.write(writer, indent, currentNamespace);
    }
    else
    {
      statement.write(writer, " ", currentNamespace);
    }
  }


  @Override
  Set<Declaration> getForwardDeclarations ()
  {
    final Set<Declaration> result = super.getForwardDeclarations();
    result.addAll(expression.getForwardDeclarations());
    result.addAll(statement.getForwardDeclarations());
    return result;
  }

  @Override
  Set<CodeFile> getIncludes ()
  {
    final Set<CodeFile> result = super.getIncludes();
    result.addAll(expression.getIncludes());
    result.addAll(statement.getIncludes());
    return result;
  }

}
