// 
// Filename : RelationshipMetaData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.processInterface;

import javax.xml.XMLConstants;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class RelationshipMetaData implements Comparable<RelationshipMetaData> {

    public abstract String getNumber();

    public abstract ObjectMetaData getLeftObject();

    public abstract ObjectMetaData getRightObject();

    public abstract String getLeftRole();

    public abstract String getRightRole();

    public abstract boolean getLeftMany();

    public abstract boolean getRightMany();

    public abstract boolean getLeftConditional();

    public abstract boolean getRightConditional();

    public abstract ObjectMetaData getAssocObject();

    public abstract boolean getAssocMany();

    public abstract RelationshipData getRelationshipData();

    public abstract DomainMetaData getDomain();

    @Override
    public String toString() {
        return getNumber();
    }

    @Override
    public int compareTo(final RelationshipMetaData o) {
        return getNumber().compareTo(o.getNumber());
    }

    public Node getXMLSchema(final Document document) {
        // Validate the following XML structure, describing a relationship instance
        // <[Relationship]>
        // <[LeftObject] id="[id]"/>
        // <[RightObject] id="[id]"/>
        // <[AssocObject] id="[id]"/>
        // </[Relationship]>
        //
        // Validate the structure of a relationship instance
        // <xs:element name=[Relationship]>
        // <xs:complexType>
        // <xs:all>
        // <xs:element name="[LeftObject]" type="objectReference"/>
        // <xs:element name="[RightObject]" type="objectReference"/>
        // <xs:element name="[AssocObject]" type="objectReference"/>
        // </xs:all>
        // </xs:complexType>
        // <xs:element/>

        String leftName = getLeftObject().getName();
        String rightName = getRightObject().getName();

        // If left and right objects are the same (ie a reflexive
        // relationship), add the role to the name of the object
        // to discriminate between them. We could have put the
        // role as an attribute, but this would mean that we could
        // not do integrity checking, because <xs:key> selections
        // cannot select using attribute values, only element
        // names.
        if (leftName.equals(rightName)) {
            leftName = leftName + "." + getLeftRole();
            rightName = rightName + "." + getRightRole();
        }
        final Element group = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:group");
        group.setAttribute("name", getNumber());

        final Element sequence = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:sequence");
        group.appendChild(sequence);

        final Element relationship = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:element");
        relationship.setAttribute("name", getNumber());
        sequence.appendChild(relationship);

        final Element complex = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:complexType");
        relationship.appendChild(complex);

        final Element all = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:all");
        complex.appendChild(all);

        final Element leftObj = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:element");
        leftObj.setAttribute("name", leftName);
        leftObj.setAttribute("type", "objectReference");
        all.appendChild(leftObj);

        final Element rightObj = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:element");
        rightObj.setAttribute("name", rightName);
        rightObj.setAttribute("type", "objectReference");
        all.appendChild(rightObj);

        if (getAssocObject() != null) {
            final Element assocObj = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:element");
            assocObj.setAttribute("name", getAssocObject().getName());
            assocObj.setAttribute("type", "objectReference");
            all.appendChild(assocObj);
        }

        return group;
    }

    public Node getXMLRefIntegritySchema(final Document document) {
        final DocumentFragment fragment = document.createDocumentFragment();
        String leftName = getLeftObject().getName();
        String rightName = getRightObject().getName();

        // If left and right objects are the same (ie a reflexive
        // relationship), add the role to the name of the object
        // to discriminate between them
        if (leftName.equals(rightName)) {
            leftName = leftName + "." + getLeftRole();
            rightName = rightName + "." + getRightRole();

            // Check that the left object refers to a valid instance
            //
            // <xs:keyref name="[Relationship]_[LeftObject]" refer=[LeftObject]__id>
            // <xs:selector xpath="[Relationship]/[LeftObject]"/>
            // <xs:field xpath="@id"/>
            // </xs:key>
            {
                final Element keyref = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:keyref");
                keyref.setAttribute("name", getNumber() + "-" + leftName + "-ref");
                keyref.setAttribute("refer", getLeftObject() + "-id");
                fragment.appendChild(keyref);

                final Element selector = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:selector");
                selector.setAttribute("xpath", getNumber() + "/" + leftName);
                keyref.appendChild(selector);

                final Element field = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:field");
                field.setAttribute("xpath", "@id");
                keyref.appendChild(field);
            }

            // Check that the right object refers to a valid instance
            //
            // <xs:keyref name="[Relationship]_[RightObject]__ref"
            // refer=[RightObject]__id>
            // <xs:selector xpath="[Relationship]/[RightObject]"/>
            // <xs:field xpath="@id"/>
            // </xs:key>
            {
                final Element keyref = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:keyref");
                keyref.setAttribute("name", getNumber() + "-" + rightName + "-ref");
                keyref.setAttribute("refer", getRightObject() + "-id");
                fragment.appendChild(keyref);

                final Element selector = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:selector");
                selector.setAttribute("xpath", getNumber() + "/" + rightName);
                keyref.appendChild(selector);

                final Element field = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:field");
                field.setAttribute("xpath", "@id");
                keyref.appendChild(field);

            }
        }

        // If the right cardinality is 1, check that each
        // left-hand object is only used once for this
        // relationship. Note that this is the correct way
        // round... draw an instance diagram and then think about
        // 'instances' of relationships.
        // eg A <<---> B
        // 1 ------ 1 1 ------ 1
        // 2 ---/ <=> 2 ------ 1
        // 3 ------ 2 3 ------ 2
        // 4 ---/ 4 ------ 2
        if (!getRightMany()) {
            // <xs:key name="[Relationship]_[LeftObject]">
            // <xs:selector xpath="[Relationship]/[LeftObject]"/>
            // <xs:field xpath="@id"/>
            // </xs:key>
            final Element key = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:key");
            key.setAttribute("name", getNumber() + "-" + leftName);
            fragment.appendChild(key);

            final Element selector = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:selector");
            selector.setAttribute("xpath", getNumber() + "/" + leftName);
            key.appendChild(selector);

            final Element field = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:field");
            field.setAttribute("xpath", "@id");
            key.appendChild(field);
        }

        // If the right cardinality is 1, check that each
        // left-hand object is only used once for this
        // relationship. Note that this is the correct way
        // round... draw an instance diagram and then think about
        // 'instances' of relationships.
        // eg A <--->> B
        // 1 ------ 1 1 ------ 1
        // \--- 2 <=> 1 ------ 2
        // 2 ------ 3 2 ------ 3
        // \--- 4 2 ------ 4
        if (!getLeftMany()) {
            // Check that each right object is only used once for this relationship
            //
            // <xs:key name="[Relationship]_[RightObject]">
            // <xs:selector xpath="[Relationship]/[RightObject]"/>
            // <xs:field xpath="@id"/>
            // </xs:key>
            final Element key = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:key");
            key.setAttribute("name", getNumber() + "-" + rightName);
            fragment.appendChild(key);

            final Element selector = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:selector");
            selector.setAttribute("xpath", getNumber() + "/" + rightName);
            key.appendChild(selector);

            final Element field = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:field");
            field.setAttribute("xpath", "@id");
            key.appendChild(field);
        }

        if (getAssocObject() != null) {
            // Check that each associative object is only used once for this
            // relationship
            //
            // <xs:key name="[Relationship]_[AssocObject]">
            // <xs:selector xpath="[Relationship]/[AssocObject]"/>
            // <xs:field xpath="@id"/>
            // </xs:key>
            {
                final Element key = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:key");
                key.setAttribute("name", getNumber() + "-" + getAssocObject());
                fragment.appendChild(key);

                final Element selector = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:selector");
                selector.setAttribute("xpath", getNumber() + "/" + getAssocObject());
                key.appendChild(selector);

                final Element field = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:field");
                field.setAttribute("xpath", "@id");
                key.appendChild(field);
            }
        }

        return fragment;

    }

}
