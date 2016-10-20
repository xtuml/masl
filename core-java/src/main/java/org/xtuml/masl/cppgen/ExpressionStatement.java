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


/**
 * Creates a C++ statement from an expression.
 */
public class ExpressionStatement extends Statement
{

  /**
   * The expression to use to form the statement
   */
  private final Expression expression;

  /**
   * Creates an expression statement
   * 

   *          The expression to use to form the statement
   */
  public ExpressionStatement ( final Expression expression )
  {
    this.expression = expression;
  }


  @Override
  void write ( final Writer writer, final String indent, final Namespace currentNamespace ) throws IOException
  {
    writer.write(TextUtils.indentText(indent, TextUtils.alignTabs(expression.getCode(currentNamespace) + ";")));

  }

  @Override
  Set<Declaration> getForwardDeclarations ()
  {
    final Set<Declaration> result = super.getForwardDeclarations();
    result.addAll(expression.getForwardDeclarations());
    return result;
  }

  @Override
  Set<CodeFile> getIncludes ()
  {
    final Set<CodeFile> result = super.getIncludes();
    result.addAll(expression.getIncludes());
    return result;
  }

}
