//
// File: NullStatement.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.cppgen;

import java.io.IOException;
import java.io.Writer;

import org.xtuml.masl.utils.TextUtils;


/**
 * A C++ break statement, as used to break out of loops and case statements.
 */
public class BreakStatement extends Statement
{

  /**
   * Creates a new break statement.
   */
  public BreakStatement ()
  {
  }

  @Override
  void write ( final Writer writer, final String indent, final Namespace currentNamespace ) throws IOException
  {
    writer.write(TextUtils.indentText(indent, "break;"));
  }
}
