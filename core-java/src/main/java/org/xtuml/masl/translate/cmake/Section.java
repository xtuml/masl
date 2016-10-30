//
// File: Section.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.translate.cmake;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.xtuml.masl.utils.TextUtils;


public class Section
    implements CMakeListsItem, Iterable<CMakeListsItem>
{

  private final String rule;

  public Section ()
  {
    this(null, null);
  }

  public Section ( final String comment )
  {
    this(comment, null);
  }

  public Section ( final String comment, final Character rule )
  {
    this.comment = comment;
    this.rule = rule == null ? "" : rule + TextUtils.RULED_LINE + rule + "\n";
  }

  public void add ( final CMakeListsItem item )
  {
    items.add(item);
  }

  @Override
  public Iterator<CMakeListsItem> iterator ()
  {
    return items.iterator();
  }

  @Override
  public void writeCode ( final Writer writer, final String indent ) throws IOException
  {
    if ( comment != null )
    {
      TextUtils.textBlock(writer, indent, null, "# ", rule + comment + "\n" + rule, null, true);
      writer.write("\n\n");
    }

    for ( final CMakeListsItem item : items )
    {
      item.writeCode(writer,indent);
      writer.write("\n");
    }

  }

  List<CMakeListsItem>  items = new ArrayList<>();

  private final String comment;

}
