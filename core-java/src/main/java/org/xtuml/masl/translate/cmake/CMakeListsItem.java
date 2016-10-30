//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.translate.cmake;

import java.io.IOException;
import java.io.Writer;


public interface CMakeListsItem
{
  void writeCode ( Writer writer, String indent ) throws IOException;
}
