//
// File: RawCode.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.cppgen;

import java.io.IOException;
import java.io.Writer;


/**
 * Raw C++ code to be written verbatim into the code file. No syntax or semantic
 * checking is performed.
 */
final public class RawCode extends Statement
{

  private final String code;

  /**
   * Creates raw code from the specified string.
   * 

   *          the raw code
   */
  public RawCode ( final String code )
  {
    this.code = code;
  }

  @Override
  void write ( final Writer writer, final String indent, final Namespace currentNamespace ) throws IOException
  {
    writer.write(indent + code);
  }


}
