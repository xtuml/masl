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
 * A C++ return statement
 */
public class ReturnStatement extends Statement
{

  private final Expression expression;

  /**
   * Creates a return statement, returning the supplied expression. If the
   * expression is null, a bare <code>return;</code> is generated.
   * 

   *          the value to return
   */
  public ReturnStatement ( final Expression expression )
  {
    this.expression = expression;
  }

  @Override
  void write ( final Writer writer, final String indent, final Namespace currentNamespace ) throws IOException
  {
    writer.write(TextUtils.indentText(indent,
                                      TextUtils.alignTabs("return" + (expression == null ? ""
                                                                                        : " " + expression.getCode(currentNamespace))
                                                          + ";")));

  }


  @Override
  Set<Declaration> getForwardDeclarations ()
  {
    final Set<Declaration> result = super.getForwardDeclarations();
    if ( expression != null )
    {
      result.addAll(expression.getForwardDeclarations());
    }
    return result;
  }

  @Override
  Set<CodeFile> getIncludes ()
  {
    final Set<CodeFile> result = super.getIncludes();
    if ( expression != null )
    {
      result.addAll(expression.getIncludes());
    }
    return result;
  }

}
