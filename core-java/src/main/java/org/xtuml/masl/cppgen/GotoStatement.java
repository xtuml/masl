//
// File: ExpressionStatement.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.cppgen;

import java.io.IOException;
import java.io.Writer;

import org.xtuml.masl.utils.TextUtils;


/**
 * A C++ goto statement. For those rare occasions when the alternatives are even
 * more ugly.
 */
public class GotoStatement extends Statement
{

  private final Label label;

  /**
   * Create a statement to goto the specified label.
   * 

   *          the label to goto
   */
  public GotoStatement ( final Label label )
  {
    this.label = label;
  }


  @Override
  void write ( final Writer writer, final String indent, final Namespace currentNamespace ) throws IOException
  {
    writer.write(TextUtils.indentText(indent, "goto " + getParentFunction().getLabelName(label) + ";"));
  }

}
