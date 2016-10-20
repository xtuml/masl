//  
// Filename : CollectionData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.processInterface;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;


public abstract class CollectionData extends DataValue<CollectionData>
    implements Comparable<CollectionData>
{

  @Override
  public CollectionData getValue ()
  {
    return this;
  }

  @Override
  public void setValue ( final CollectionData value )
  {
    startIndex = value.startIndex;
    data = value.data;
  }


  protected int                startIndex = 1;
  protected List<DataValue<?>> data       = new ArrayList<DataValue<?>>();
  protected TypeMetaData       type;

  public CollectionData ( final TypeMetaData containedType )
  {
    this.type = containedType;
  }

  public TypeMetaData getType ()
  {
    return type;
  }

  public int getStartIndex ()
  {
    return startIndex;
  }

  public void setStartIndex ( final int startIndex )
  {
    this.startIndex = startIndex;
  }

  public int getEndIndex ()
  {
    return startIndex + data.size() - 1;
  }

  public int getLength ()
  {
    return data.size();
  }

  public List<DataValue<?>> getData ()
  {
    return data;
  }

  public void setData ( final List<DataValue<?>> data )
  {
    this.data = data;
  }

  public Node toXML ( final Document document )
  {
    final DocumentFragment fragment = document.createDocumentFragment();
    for ( int i = 0; i < getLength(); ++i )
    {
      final Node seqNode = document.createElement("value");
      fragment.appendChild(seqNode);
      seqNode.appendChild(data.get(i).toXML(document));
    }
    return fragment;
  }

  public void fromXML ( final Node parent )
  {
    startIndex = 0;
    data.clear();

    for ( int i = startIndex; parent != null && i < parent.getChildNodes().getLength(); ++i )
    {
      if ( parent.getChildNodes().item(i).getNodeType() == Node.ELEMENT_NODE )
      {
        data.add(type.getDataObject());
        data.get(i).fromXML(parent.getChildNodes().item(i));
      }
    }
  }

  @Override
  public String toString ()
  {
    final StringBuffer buf = new StringBuffer();
    buf.append("{");
    for ( int i = 0; i < getLength(); i++ )
    {
      buf.append("[" + (i + startIndex) + "]=" + data.get(i));
      if ( i < getLength() - 1 )
      {
        buf.append(", ");
      }
    }
    buf.append("}");
    return buf.toString();
  }


  public int compareTo ( final CollectionData rhs )
  {
    return getLength() == rhs.getLength() ? 0 : (getLength() < rhs.getLength() ? -1 : 1);
  }
}
