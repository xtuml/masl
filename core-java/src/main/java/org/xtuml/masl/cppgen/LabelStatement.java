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
 * A C++ label statement of the form <code>label:</code>
 */
public class LabelStatement extends Statement
{

  private final Label label;

  /**
   * Creates a label statement marking the position of the supplied label
   * 

   *          the label to mark
   */
  public LabelStatement ( final Label label )
  {
    this.label = label;
  }


  @Override
  void write ( final Writer writer, final String indent, final Namespace currentNamespace ) throws IOException
  {
    writer.write(TextUtils.indentText(indent, getParentFunction().getLabelName(label) + ": ;"));

  }

}
