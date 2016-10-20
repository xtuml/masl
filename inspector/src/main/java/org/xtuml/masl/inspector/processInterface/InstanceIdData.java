// 
// Filename : InstanceIdData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.processInterface;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


public abstract class InstanceIdData extends DataValue<InstanceData>
    implements Comparable<InstanceIdData>
{

  public InstanceIdData ( final DomainMetaData domain )
  {
    this.domain = domain;
  }

  public InstanceIdData ( final ObjectMetaData meta )
  {
    this.meta = meta;
    this.domain = meta.getDomain();
  }

  public int compareTo ( final InstanceIdData o )
  {
    return id.compareTo(o.id);
  }


  public InstanceData getInstanceData ()
  {
    if ( instance == null && id != null )
    {
      try
      {
        instance = ProcessConnection.getConnection().getInstanceData(meta, id);
      }
      catch ( final java.rmi.RemoteException e )
      {
        e.printStackTrace();
      }
    }
    return instance;
  }

  public ObjectMetaData getMetaData ()
  {
    return meta;
  }

  public Integer getId ()
  {
    return id;
  }

  @Override
  public InstanceData getValue ()
  {
    return getInstanceData();
  }

  public void setId ( final Integer id )
  {
    this.id = id;
    instance = null;
  }

  @Override
  public void setValue ( final InstanceData value )
  {
    id = value.getPrimaryKey();
    instance = value;
  }

  @Override
  public String toString ()
  {
    if ( id == null )
    {
      return "null " + meta;
    }
    else
    {
      return meta + " " + id;
    }
  }

  public void fromXML ( final Node parent )
  {
    for ( int i = 0; parent != null && i < parent.getChildNodes().getLength(); ++i )
    {
      if ( parent.getChildNodes().item(i).getNodeType() == Node.ELEMENT_NODE )
      {
        final Element curNode = (Element)parent.getChildNodes().item(i);
        final String objectName = curNode.getNodeName();
        meta = domain.getObject(objectName);
        id = new Integer(curNode.getAttributes().getNamedItem("id").getNodeValue());
        break;
      }
    }
  }

  public Node toXML ( final Document document )
  {
    if ( id != null )
    {
      final Element instanceNode = document.createElement(meta.getName());
      instanceNode.setAttribute("id", id.toString());
      return instanceNode;
    }
    else
    {
      return document.createDocumentFragment();
    }
  }

  protected InstanceData   instance;

  protected Integer        id;

  protected ObjectMetaData meta;

  protected DomainMetaData domain;

}
