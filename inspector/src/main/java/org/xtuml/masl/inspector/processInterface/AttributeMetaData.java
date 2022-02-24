//
// Filename : AttributeMetaData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.processInterface;

import javax.xml.XMLConstants;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class AttributeMetaData {

    public abstract String getName();

    public abstract ObjectMetaData getObject();

    public abstract String getRelationshipText();

    public abstract StructureMetaData getStructure();

    public abstract TypeMetaData getType();

    public abstract String getTypeName();

    public Node getXMLSchema(final Document document) {
        // Check that the attribute value is of the correct type
        //
        // <xs:element name="[Attribute]" type="[Type]"/>
        final Element attribute = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:element");
        attribute.setAttribute("name", getName());

        final String schemaType = getType().getXMLSchemaName();
        if (schemaType == null) {
            attribute.appendChild(getType().getXMLSchema(document));
        } else {
            attribute.setAttribute("type", getType().getXMLSchemaName());
        }

        return attribute;
    }

    public abstract boolean isIdentifier();

    public abstract boolean isReadOnly();

    public abstract boolean isReferential();

    public abstract DataValue<?> getDefaultValue();

}
