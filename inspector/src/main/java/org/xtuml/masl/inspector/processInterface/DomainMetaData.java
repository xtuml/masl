//
// Filename : DomainMetaData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.processInterface;

import java.io.File;

import javax.xml.XMLConstants;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class DomainMetaData implements Comparable<DomainMetaData> {

    public abstract int getId();

    public abstract String getName();

    public abstract boolean isInterface();

    public abstract ProcessMetaData getProcess();

    public abstract DomainServiceMetaData[] getServices();

    public abstract DomainServiceMetaData[] getDomainServices();

    public abstract DomainServiceMetaData[] getExternals();

    public abstract DomainServiceMetaData[] getScenarios();

    public abstract TerminatorMetaData[] getTerminators();

    public abstract ObjectMetaData[] getObjects();

    public abstract RelationshipMetaData[] getRelationships();

    public abstract SuperSubtypeMetaData[] getSuperSubtypes();

    public abstract StructureMetaData[] getStructures();

    public abstract EnumerateMetaData[] getEnumerates();

    public abstract DomainData getDomainData();

    private final java.util.Map<String, ObjectMetaData> objectLookup = new java.util.HashMap<String, ObjectMetaData>();

    public ObjectMetaData getObject(final String objectName) {
        return objectLookup.get(objectName);
    }

    private final java.util.Map<String, TerminatorMetaData> terminatorLookup = new java.util.HashMap<String, TerminatorMetaData>();

    public TerminatorMetaData getTerminator(final String terminatorName) {
        return terminatorLookup.get(terminatorName);
    }

    private final java.util.Map<String, RelationshipMetaData> relationshipLookup = new java.util.HashMap<String, RelationshipMetaData>();

    public RelationshipMetaData getRelationship(final String number) {
        return relationshipLookup.get(number);
    }

    private final java.util.Map<String, SuperSubtypeMetaData> superSubtypeLookup = new java.util.HashMap<String, SuperSubtypeMetaData>();

    public SuperSubtypeMetaData getSuperSubtype(final String number) {
        return superSubtypeLookup.get(number);
    }

    private final java.util.Map<String, StructureMetaData> structureLookup = new java.util.HashMap<String, StructureMetaData>();

    public StructureMetaData getStructure(final String structName) {
        return structureLookup.get(structName);
    }

    private final java.util.Map<String, EnumerateMetaData> enumerateLookup = new java.util.HashMap<String, EnumerateMetaData>();

    public EnumerateMetaData getEnumerate(final String enumName) {
        return enumerateLookup.get(enumName);
    }

    private File directory = null;

    public void setDirectory(final File directory) {
        this.directory = directory;
    }

    public File getDirectory() {
        return directory == null ? getDefaultDirectory() : directory;
    }

    protected File defaultDirectory;

    protected File getDefaultDirectory() {
        return defaultDirectory;
    }

    protected void linkChildren() {
        for (final ObjectMetaData object : getObjects()) {
            objectLookup.put(object.getName(), object);
        }

        for (final TerminatorMetaData terminator : getTerminators()) {
            terminatorLookup.put(terminator.getName(), terminator);
        }

        for (final RelationshipMetaData relationship : getRelationships()) {
            relationshipLookup.put(relationship.getNumber(), relationship);
        }

        for (final SuperSubtypeMetaData superSubtype : getSuperSubtypes()) {
            superSubtypeLookup.put(superSubtype.getNumber(), superSubtype);
        }

        for (final StructureMetaData structure : getStructures()) {
            structureLookup.put(structure.getName(), structure);
        }

        for (final EnumerateMetaData enumerate : getEnumerates()) {
            enumerateLookup.put(enumerate.getName(), enumerate);
        }

    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public int compareTo(final DomainMetaData o) {
        return getName().compareTo(o.getName());
    }

    public Node getXMLSchema(final Document document, final boolean refIntegrityCheck,
            final boolean uniqueIdentifierCheck) {
        final Element schema = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:schema");
        schema.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, XMLConstants.XMLNS_ATTRIBUTE + ":xs",
                XMLConstants.W3C_XML_SCHEMA_NS_URI);

        // Validate the following XML structure.
        // <[Domain]>
        // <[Object]/>
        // :
        // <[Relationship]/>
        // :
        // </[Domain]>
        //
        // Schema:
        // <xs:element name="[Domain]">
        // <xs:complexType>
        // <xs:choice minOccurs="0" maxOccurs="unbounded">
        // <xs:element ref="[Object]"/>
        // :
        // <xs:element ref="[Relationship]"/>
        // :
        // </xs:choice>
        // </xs:complexType>
        // [ObjectKeyChecks]
        // [RelationshipKeyChecks]
        // [SuperSubtypeKeyChecks]
        // </xs:element>
        // [ObjectDefinitions]
        // [RelationshipDefinitions]
        // [SuperSubtypeDefinitions]
        // <xs:complexType name="objectReference">
        // <xs:attribute name="id" type="xs:int" use="required"/>
        // </xs:complexType>
        // [TypeDefinitions]

        final Element domain = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:element");
        domain.setAttribute("name", getName());

        final Element complex = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:complexType");
        domain.appendChild(complex);

        final Element choice = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:choice");
        choice.setAttribute("minOccurs", "0");
        choice.setAttribute("maxOccurs", "unbounded");
        complex.appendChild(choice);

        for (final ObjectMetaData object : getObjects()) {
            final Element obj = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:group");
            obj.setAttribute("ref", object.getName());
            choice.appendChild(obj);
            if (uniqueIdentifierCheck) {
                domain.appendChild(object.getXMLUniqueIdentifierSchema(document));
            }
            if (refIntegrityCheck) {
                domain.appendChild(object.getXMLRefIntegritySchema(document));
            }
        }

        for (final RelationshipMetaData relationship : getRelationships()) {
            final Element obj = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:group");
            obj.setAttribute("ref", relationship.getNumber());
            choice.appendChild(obj);
            if (refIntegrityCheck) {
                domain.appendChild(relationship.getXMLRefIntegritySchema(document));
            }
        }

        for (final SuperSubtypeMetaData superSubtype : getSuperSubtypes()) {
            final Element obj = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:group");
            obj.setAttribute("ref", superSubtype.getNumber());
            choice.appendChild(obj);
            if (refIntegrityCheck) {
                domain.appendChild(superSubtype.getXMLRefIntegritySchema(document));
            }
        }

        schema.appendChild(domain);

        for (final ObjectMetaData object : getObjects()) {
            schema.appendChild(object.getXMLSchema(document));
            for (final EventMetaData event : object.getEvents()) {
                if (event.getObject() == event.getParentObject()) {
                    schema.appendChild(event.getXMLSchema(document));
                }
            }
        }

        for (final RelationshipMetaData relationship : getRelationships()) {
            schema.appendChild(relationship.getXMLSchema(document));
        }

        for (final SuperSubtypeMetaData superSubtype : getSuperSubtypes()) {
            schema.appendChild(superSubtype.getXMLSchema(document));
        }

        schema.appendChild(getTimerSchema(document));
        schema.appendChild(getAnyObjectRefSchema(document));

        schema.appendChild(getObjectRefSchema(document, schema));

        for (final TypeMetaData type : TypeMetaData.getXMLReferenceTypes()) {
            schema.appendChild(type.getXMLSchema(document));
        }

        return schema;
    }

    private Node getObjectRefSchema(final Document document, final Element schema) {
        final Element objRefType = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:complexType");
        objRefType.setAttribute("name", "objectReference");

        final Element id = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:attribute");
        id.setAttribute("name", "id");
        id.setAttribute("type", "xs:int");
        id.setAttribute("use", "required");
        objRefType.appendChild(id);

        return objRefType;
    }

    private Node getAnyObjectRefSchema(final Document document) {
        final Element typeDef = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:complexType");
        typeDef.setAttribute("name", "anyObjectReference");

        final Element choice = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:choice");
        typeDef.appendChild(choice);
        for (final ObjectMetaData object : getObjects()) {
            final Element objRef = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:element");
            objRef.setAttribute("name", object.getName());
            objRef.setAttribute("type", "objectReference");
            choice.appendChild(objRef);
        }

        return typeDef;
    }

    private Node getTimerSchema(final Document document) {
        final Element typeDef = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:complexType");
        typeDef.setAttribute("name", "timerType");

        final Element sequence = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:sequence");
        sequence.setAttribute("minOccurs", "0");
        typeDef.appendChild(sequence);

        final Element expiry = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:element");
        expiry.setAttribute("name", "expiry");
        expiry.setAttribute("type", "xs:dateTime");
        sequence.appendChild(expiry);

        final Element period = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:element");
        period.setAttribute("name", "period");
        period.setAttribute("type", "xs:duration");
        period.setAttribute("minOccurs", "0");
        sequence.appendChild(period);

        final Element eventNode = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:element");
        eventNode.setAttribute("name", "event");
        sequence.appendChild(eventNode);

        final Element eventType = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:complexType");
        eventNode.appendChild(eventType);

        final Element eventChoice = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:choice");
        eventType.appendChild(eventChoice);

        for (final ObjectMetaData object : getObjects()) {
            for (final EventMetaData event : object.getEvents()) {
                if (event.getObject() == event.getParentObject()) {
                    final Element eventRef = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:group");
                    eventChoice.appendChild(eventRef);
                    eventRef.setAttribute("ref", object.getName() + "." + event.getName());
                }
            }

        }

        return typeDef;

    }
}
