// 
// Filename : TypeMetaData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.processInterface;

import javax.xml.XMLConstants;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


public abstract class TypeMetaData
{

  public enum BasicType
  {
    AnyInstance,
    Boolean,
    Byte,
    Character,
    Device,
    Duration,
    Enumeration,
    Event,
    Instance,
    Integer,
    LongInteger,
    LongNatural,
    Natural,
    Real,
    State,
    String,
    Structure,
    Timestamp,
    WCharacter,
    WString,
    Timer,
    Dictionary;
  }

  public abstract BasicType getBasicType ();

  public abstract DataValue<?> getDataObject ();

  public abstract StructureMetaData getStructure ();

  public abstract EnumerateMetaData getEnumerate ();

  public abstract ObjectMetaData getObject ();

  public abstract DomainMetaData getDomain ();

  private static java.util.Map<String, TypeMetaData> xmlReferenceTypes = new java.util.HashMap<String, TypeMetaData>();

  public static TypeMetaData[] getXMLReferenceTypes ()
  {
    return xmlReferenceTypes.values().toArray(new TypeMetaData[xmlReferenceTypes.size()]);
  }

  public String getXMLSchemaName ()
  {
    if ( getCollectionDepth() > 0 )
    {
       // force contained type into list
       getContainedType().getXMLSchemaName();
       return null;
    }

    String typeName = null;
    switch ( getBasicType() )
    {
      case AnyInstance:
        return "anyObjectReference";
      case Boolean:
        return "xs:boolean";
      case Byte:
        return "xs:byte";
      case Character:
        typeName = "characterType";
        break;
      case Device:
        typeName = "device";
        break;
      case Duration:
        return "xs:duration";
      case Enumeration:
        typeName = getEnumerate().getDomain().getName() + "-" + getEnumerate().getName();
        break;
      case Event:
        typeName = "eventType";
        break;
      case Instance:
        return null;
      case Integer:
        return "xs:int";
      case LongInteger:
        return "xs:long";
      case LongNatural:
        return "xs:unsignedLong";
      case Natural:
        return "xs:unsignedInt";
      case Real:
        return "xs:double";
      case State:
        typeName = getObject().getDomain().getName() + "-" + getObject().getName();
        break;
      case String:
        return "xs:string";
      case Structure:
        typeName = getStructure().getDomain().getName() + "-" + getStructure().getName();
        break;
      case Timestamp:
        return "xs:dateTime";
      case WCharacter:
        typeName = "characterType";
        break;
      case WString:
        return "xs:string";
      case Timer:
        return "timerType";
      case Dictionary:
        return null;
    }

    if ( !xmlReferenceTypes.containsKey(typeName) )
    {
      System.out.println("adding " + typeName);
      xmlReferenceTypes.put(typeName, this);

      // force contained types into list
      if ( getContainedType() != null )
      {
        getContainedType().getXMLSchemaName();
      }

      if ( getDictionaryKey() != null )
      {
        getDictionaryKey().getXMLSchemaName();
        getDictionaryValue().getXMLSchemaName();
      }

      if ( getStructure() != null )
      {
        for ( int i = 0; i < getStructure().getAttributes().length; ++i )
        {
          getStructure().getAttributes()[i].getType().getXMLSchemaName();
        }

      }

    }

    return typeName;

  }

  public Node getXMLSchema ( final Document document )
  {
    Element typeDef = null;

    if ( CharacterData.class.isAssignableFrom(getStorageClass()) )
    {
      // The 'characterType' type is a one character long string, used to
      // validate
      // the masl 'Standard::Character' type.
      //
      // <xs:simpleType name="characterType">
      // <xs:restriction base="xs:string">
      // <xs:length value="1"/>
      // </xs:restriction>
      // </xs:simpleType>
      //
      typeDef = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:simpleType");
      typeDef.setAttribute("name", getXMLSchemaName());

      final Element restriction = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:restriction");
      restriction.setAttribute("base", "xs:string");
      typeDef.appendChild(restriction);

      final Element length = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:length");
      length.setAttribute("value", "1");
      restriction.appendChild(length);
    }
    else if ( DeviceData.class.isAssignableFrom(getStorageClass()) )
    {
      // The 'deviceType' type is an empty node, used to validate
      // the masl 'Standard::Device' type. Note that this type is
      // not persisted, so does not contain any data, and is therefore
      // effectively a placeholder.
      //
      // <xs:complexType name="deviceType">
      // </xs:complexType>
      typeDef = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:complexType");
      typeDef.setAttribute("name", getXMLSchemaName());
    }
    else if ( AnyEventData.class.isAssignableFrom(getStorageClass()) )
    {
      // The 'eventType' type is an empty node, used to validate
      // the masl 'Standard::Device' type. Note that this type is
      // not persisted, so does not contain any data, and is therefore
      // effectively a placeholder.
      //
      // <xs:complexType name="eventType">
      // </xs:complexType>
      typeDef = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:complexType");
      typeDef.setAttribute("name", getXMLSchemaName());
    }
    else if ( EnumerateData.class.isAssignableFrom(getStorageClass()) )
    {
      // Validate enumerate type
      //
      // <xs:simpleType name="[getXMLSchemaName()]">
      // <xs:restriction base="xs:string">
      // <xs:enumeration valeu="[Value]"/>
      // :
      // </xs:restriction>
      // </xs:simpleType>
      typeDef = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:simpleType");
      typeDef.setAttribute("name", getXMLSchemaName());

      final Element restriction = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:restriction");
      restriction.setAttribute("base", "xs:string");
      typeDef.appendChild(restriction);

      for ( final String string : getEnumerate().getNames() )
      {
        final Element enumeration = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:enumeration");
        enumeration.setAttribute("value", string);
        restriction.appendChild(enumeration);
      }
    }
    else if ( CollectionData.class.isAssignableFrom(getStorageClass()) )
    {
      typeDef = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:complexType");

      final Element sequence = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:sequence");
      sequence.setAttribute("minOccurs", "0");
      sequence.setAttribute("maxOccurs", "unbounded");
      typeDef.appendChild(sequence);

      final Element element = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:element");
      element.setAttribute("name", "value");
      sequence.appendChild(element);
      final String typeName = getContainedType().getXMLSchemaName();
      if ( typeName == null )
      {
        element.appendChild(getContainedType().getXMLSchema(document));
      }
      else
      {
        element.setAttribute("type", typeName);
      }

    }
    else if ( DictionaryData.class.isAssignableFrom(getStorageClass()) )
    {
      typeDef = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:complexType");

      final Element sequence = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:sequence");
      sequence.setAttribute("minOccurs", "0");
      sequence.setAttribute("maxOccurs", "unbounded");
      typeDef.appendChild(sequence);

      final Element keyElement = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:element");
      keyElement.setAttribute("name", "key");
      sequence.appendChild(keyElement);
      final String keyTypeName = getDictionaryKey().getXMLSchemaName();
      if ( keyTypeName == null )
      {
        keyElement.appendChild(getDictionaryKey().getXMLSchema(document));
      }
      else
      {
        keyElement.setAttribute("type", keyTypeName);
      }

      final Element valueElement = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:element");
      valueElement.setAttribute("name", "value");
      sequence.appendChild(valueElement);
      final String valueTypeName = getDictionaryValue().getXMLSchemaName();
      if ( valueTypeName == null )
      {
        valueElement.appendChild(getDictionaryValue().getXMLSchema(document));
      }
      else
      {
        valueElement.setAttribute("type", valueTypeName);
      }


    }
    else if ( StructureData.class.isAssignableFrom(getStorageClass()) )
    {
      typeDef = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:complexType");
      typeDef.setAttribute("name", getXMLSchemaName());

      final Element all = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:all");
      typeDef.appendChild(all);

      for ( int i = 0; i < getStructure().getAttributes().length; ++i )
      {
        all.appendChild(getStructure().getAttributes()[i].getXMLSchema(document));
      }
    }
    else if ( InstanceIdData.class.isAssignableFrom(getStorageClass()) )
    {
      typeDef = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:complexType");
      final Element sequence = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:sequence");
      sequence.setAttribute("minOccurs", "0");
      typeDef.appendChild(sequence);

      final Element instanceElement = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:element");
      instanceElement.setAttribute("name", getObject().getName());
      instanceElement.setAttribute("type", "objectReference");
      sequence.appendChild(instanceElement);


    }
    else
    {
      System.err.println("Error: unrecognised type " + getXMLSchemaName() + " " + getStorageClass());
      typeDef = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:complexType");
      typeDef.setAttribute("name", getXMLSchemaName());
    }

    return typeDef;

  }

  public abstract TypeMetaData getContainedType ();

  public abstract TypeMetaData getDictionaryKey ();

  public abstract TypeMetaData getDictionaryValue ();

  public abstract int getCollectionDepth ();

  public abstract Class<?> getStorageClass ();


}
