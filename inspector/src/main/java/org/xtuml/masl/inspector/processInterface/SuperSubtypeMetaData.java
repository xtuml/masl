// 
// Filename : SuperSubtypeMetaData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.processInterface;

import javax.xml.XMLConstants;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xtuml.masl.inspector.socketConnection.DomainMetaData;

public abstract class SuperSubtypeMetaData implements Comparable<SuperSubtypeMetaData> {

    @Override
    public int compareTo(final SuperSubtypeMetaData o) {
        return getNumber().compareTo(o.getNumber());
    }

    public abstract DomainMetaData getDomain();

    public abstract String getNumber();

    public abstract ObjectMetaData[] getSubtypes();

    public abstract SuperSubtypeData getSuperSubtypeData();

    public abstract ObjectMetaData getSupertype();

    public Node getXMLRefIntegritySchema(final Document document) {
        final DocumentFragment fragment = document.createDocumentFragment();

        // Check that the getSupertype() is only used once for this relationship
        //
        // <xs:key name="[Relationship]_[SubtypeObject]">
        // <xs:selector xpath="[Relationship]/[SubtypeObject]"/>
        // <xs:field xpath="@id"/>
        // </xs:key>
        {
            final Element key = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:key");
            key.setAttribute("name", getNumber() + "-" + getSupertype());
            fragment.appendChild(key);

            final Element selector = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:selector");
            selector.setAttribute("xpath", getNumber() + "/" + getSupertype());
            key.appendChild(selector);

            final Element field = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:field");
            field.setAttribute("xpath", "@id");
            key.appendChild(field);
        }

        for (int i = 0; i < getSubtypes().length; ++i) {
            // Check that each subtype is only used once for this relationship
            //
            // <xs:key name="[Relationship]_[SubtypeObject]">
            // <xs:selector xpath="[Relationship]/[SubtypeObject]"/>
            // <xs:field xpath="@id"/>
            // </xs:key>
            {
                final Element key = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:key");
                key.setAttribute("name", getNumber() + "-" + getSubtypes()[i]);
                fragment.appendChild(key);

                final Element selector = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:selector");
                selector.setAttribute("xpath", getNumber() + "/" + getSubtypes()[i]);
                key.appendChild(selector);

                final Element field = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:field");
                field.setAttribute("xpath", "@id");
                key.appendChild(field);
            }

        }

        return fragment;

    }

    public Node getXMLSchema(final Document document) {
        // Validate the following XML structure, describing a relationship instance
        // <[Relationship]>
        // <[SuperObject] id="[id]"/>
        // <[SubObject_n] id="[id]"/>
        // </[Relationship]>
        //
        // <xs:element name=[Relationship]>
        // <xs:complexType>
        // <xs:choice>
        // <xs:sequence>
        // <xs:element name="[SuperObject] type="objectReference"/>
        // <xs:choice>
        // <xs:element name="[SubObject_n] type="objectReference"/>
        // :
        // </xs:choice>
        // </xs:sequence>
        // <xs:sequence>
        // <xs:choice>
        // <xs:element name="[SubObject_n] type="objectReference"/>
        // :
        // </xs:choice>
        // <xs:element name="[SuperObject] type="objectReference"/>
        // </xs:sequence>
        // </xs:choice>
        // </xs:complexType>
        // </xs:element>

        final Element group = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:group");
        group.setAttribute("name", getNumber());

        final Element sequence = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:sequence");
        group.appendChild(sequence);

        final Element relationship = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:element");
        relationship.setAttribute("name", getNumber());
        sequence.appendChild(relationship);

        final Element complex = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:complexType");
        relationship.appendChild(complex);

        final Element choice = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:choice");
        complex.appendChild(choice);

        {
            final Element subSequence = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:sequence");
            choice.appendChild(subSequence);

            final Element superObj = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:element");
            superObj.setAttribute("name", getSupertype().getName());
            superObj.setAttribute("type", "objectReference");
            subSequence.appendChild(superObj);

            final Element subChoice = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:choice");
            subSequence.appendChild(subChoice);

            for (int i = 0; i < getSubtypes().length; ++i) {
                final Element subObj = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:element");
                subObj.setAttribute("name", getSubtypes()[i].getName());
                subObj.setAttribute("type", "objectReference");
                subChoice.appendChild(subObj);
            }
        }

        {
            final Element subSequence = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:sequence");
            choice.appendChild(subSequence);

            final Element subChoice = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:choice");
            subSequence.appendChild(subChoice);

            for (int i = 0; i < getSubtypes().length; ++i) {
                final Element subObj = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:element");
                subObj.setAttribute("name", getSubtypes()[i].getName());
                subObj.setAttribute("type", "objectReference");
                subChoice.appendChild(subObj);
            }

            final Element superObj = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:element");
            superObj.setAttribute("name", getSupertype().getName());
            superObj.setAttribute("type", "objectReference");
            subSequence.appendChild(superObj);
        }

        return group;
    }

    @Override
    public String toString() {
        return getNumber();
    }

}
