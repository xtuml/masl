// 
// Filename : EventMetaData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.processInterface;

import javax.xml.XMLConstants;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


public abstract class EventMetaData
    implements Comparable<EventMetaData>
{

  public enum EventType
  {
    Assigner, Creation, Normal
  }

  public int compareTo ( final EventMetaData lhs )
  {
    // Compare names
    int res = getName().compareTo(lhs.getName());
    if ( res != 0 )
    {
      return res;
    }

    // Names are the same so compare parameter names
    for ( int i = 0; i < getParameters().length; i++ )
    {
      // Check for less parameters on lhs, in which case we are bigger
      if ( i >= lhs.getParameters().length )
      {
        return 1;
      }

      // Compare the next parameter position
      res = getParameters()[i].getName().compareTo(lhs.getParameters()[i].getName());
      if ( res != 0 )
      {
        return res;
      }
    }

    // Check for more parameters on lhs, in which case we are smaller
    if ( getParameters().length < lhs.getParameters().length )
    {
      return -1;
    }
    else
    {
      return 0;
    }
  }

  public String getFullyQualifiedName ()
  {
    return getParentObject().getFullyQualifiedName() + "." + getName();
  }

  public abstract String getName ();

  public abstract ObjectMetaData getObject ();

  public abstract ParameterMetaData[] getParameters ();

  public abstract ObjectMetaData getParentObject ();

  public abstract EventType getType ();

  public Node getXMLSchema ( final Document document )
  {
    final Element schema = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:group");
    schema.setAttribute("name", getParentObject().getName() + "." + getName());

    final Element groupSequence = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:sequence");
    schema.appendChild(groupSequence);

    final Element event = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:element");
    event.setAttribute("name", getParentObject().getName() + "." + getName());
    groupSequence.appendChild(event);

    final Element eventType = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:complexType");
    event.appendChild(eventType);

    final Element paramSequence = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:sequence");
    eventType.appendChild(paramSequence);

    final Element source = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:element");
    source.setAttribute("name", "source");
    source.setAttribute("type", "anyObjectReference");
    source.setAttribute("minOccurs", "0");
    paramSequence.appendChild(source);

    if ( getType() == EventType.Normal )
    {
      final Element dest = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:element");
      dest.setAttribute("name", "destination");
      paramSequence.appendChild(dest);

      final Element destType = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:complexType");
      dest.appendChild(destType);

      final Element destChoice = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:choice");
      destType.appendChild(destChoice);

      final Element object = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:element");
      object.setAttribute("name", getObject().getName());
      object.setAttribute("type", "objectReference");
      destChoice.appendChild(object);

      for ( final ObjectMetaData subObject : getParentObject().getPolymorphicSubObjects() )
      {
        final Element subObj = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:element");
        subObj.setAttribute("name", subObject.getName());
        subObj.setAttribute("type", "objectReference");
        destChoice.appendChild(subObj);

      }
    }

    for ( final ParameterMetaData parameter : getParameters() )
    {
      paramSequence.appendChild(parameter.getXMLSchema(document));
    }

    return schema;
  }

  @Override
  public String toString ()
  {
    final String prefix = getParentObject() == getObject() ? "" : getParentObject().getName() + ".";

    final StringBuffer res = new StringBuffer(prefix + getName());

    if ( getParameters() != null )
    {
      res.append(" ( ");
      for ( int i = 0; i < getParameters().length; i++ )
      {
        if ( i != 0 )
        {
          res.append(", ");
        }
        res.append(getParameters()[i].toString());
      }
      res.append(" )");
    }
    return res.toString();
  }

}
