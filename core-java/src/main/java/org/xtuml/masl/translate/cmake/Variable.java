//
// File: Variable.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.translate.cmake;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;

import org.xtuml.masl.translate.cmake.language.VariableReference;
import org.xtuml.masl.utils.TextUtils;


public class Variable
    implements CMakeListsItem
{

  public Variable ( final String name, final String... values )
  {
    this.name = name;
    this.values = Arrays.asList(values);
  }

  public void addValue ( final String value )
  {
    values.add(value);
  }

  public String getName ()
  {
    return name;
  }

  @Override
  public void writeCode ( final Writer writer, final String indent ) throws IOException
  {
    writer.write(indent + "set ( " + name);
    TextUtils.formatList(writer, values, "\n"+ indent +"   ", "\n" + indent +"   ", "");
    writer.write("\n" + indent + "  )\n");
  }

  public VariableReference getReference ()
  {
    return new VariableReference(this);
  }


  private final String                   name;

  private final List<String> values;
}
