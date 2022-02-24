// 
// Filename : ObjectMetaData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.processInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class ObjectMetaData implements Comparable<ObjectMetaData> {

    static class FormalisedRelationship {

        public FormalisedRelationship(final RelationshipMetaData meta, final int leftRelIdx, final int rightRelIdx) {
            this.leftRelIdx = leftRelIdx;
            this.rightRelIdx = rightRelIdx;
            this.meta = meta;
        }

        public RelationshipMetaData meta;
        public int leftRelIdx;

        public int rightRelIdx;
    }

    static class FormalisedSuperSubtype {

        public FormalisedSuperSubtype(final SuperSubtypeMetaData meta, final int relIdx, final int subIndex) {
            this.relIdx = relIdx;
            this.subIndex = subIndex;
            this.meta = meta;
        }

        public SuperSubtypeMetaData meta;
        public int relIdx;

        public int subIndex;
    }

    @Override
    public int compareTo(final ObjectMetaData rhs) {
        final int res = getDomain().compareTo(rhs.getDomain());
        if (res != 0) {
            return res;
        }

        return name.compareTo(rhs.name);
    }

    public abstract EventMetaData[] getAssignerEvents();

    public abstract StateMetaData[] getAssignerStates();

    public abstract AttributeMetaData[] getAttributes();

    public abstract DomainMetaData getDomain();

    public abstract EventMetaData[] getEvents();

    public FormalisedRelationship[] getFormalisedRelationships() {
        if (formalisedRelationships == null) {
            linkRelationships();
        }
        return formalisedRelationships;
    }

    public FormalisedSuperSubtype[] getFormalisedSuperSubtypes() {
        if (formalisedSuperSubtypes == null) {
            linkRelationships();
        }
        return formalisedSuperSubtypes;
    }

    public abstract InstanceData getInstanceData();

    public abstract EventMetaData[] getInstanceEvents();

    public String getInstanceIdentifier(final InstanceData instance) {
        if (instance == null) {
            return "!! Deleted !!";
        }

        final StringBuffer identifier = new StringBuffer();
        boolean needComma = false;
        for (int i = 0; i < getAttributes().length; i++) {
            if (getAttributes()[i].isIdentifier() == true) {
                if (needComma) {
                    identifier.append(", ");
                }
                identifier.append(instance.getAttributes()[i]);
                needComma = true;
            }
        }
        return identifier.toString();
    }

    public abstract ObjectServiceMetaData[] getInstanceServices();

    public abstract StateMetaData[] getInstanceStates();

    private Map<String, Integer> stateIndexLookup = null;

    public int getStateIndex(final String stateName) {
        if (stateIndexLookup == null) {
            stateIndexLookup = new HashMap<String, Integer>();
            for (int i = 0; i < getInstanceStates().length; ++i) {
                stateIndexLookup.put(getInstanceStates()[i].getName(), i);
            }
        }
        return stateIndexLookup.get(stateName);
    }

    public String getKeyLetters() {
        return keyLetters;
    }

    public String getName() {
        return name;
    }

    public abstract ObjectServiceMetaData[] getObjectServices();

    public Class<Integer> getPKClass() {
        return Integer.class;
    }

    public abstract ObjectRelationshipMetaData[] getRelationships();

    public abstract ObjectServiceMetaData[] getServices();

    public abstract StateMetaData[] getStates();

    public Node getXMLRefIntegritySchema(final Document document) {
        final DocumentFragment fragment = document.createDocumentFragment();

        // Check that the object internal id is unique across the
        // domain population
        //
        // <xs:key name="[Object]__id">
        // <xs:selector xpath="[Object]"/>
        // <xs:field xpath="@id"/>
        // </xs:key>
        final Element key = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:key");
        key.setAttribute("name", name + "-id");

        final Element selector = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:selector");
        selector.setAttribute("xpath", name);
        key.appendChild(selector);

        final Element field = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:field");
        field.setAttribute("xpath", "@id");
        key.appendChild(field);

        fragment.appendChild(key);

        final Element keyref = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:keyref");
        keyref.setAttribute("name", name + "-ref");
        keyref.setAttribute("refer", name + "-id");
        fragment.appendChild(keyref);

        final Element refSselector = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:selector");
        refSselector.setAttribute("xpath", ".//" + name);
        keyref.appendChild(refSselector);

        final Element refField = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:field");
        refField.setAttribute("xpath", "@id");
        keyref.appendChild(refField);

        return fragment;
    }

    public Node getXMLSchema(final Document document) {
        // Validate the following XML structure, describing an object instance
        // <[Object] id=[id]>
        // <[Attribute]/>
        // :
        // </[Object]>
        //
        // Schema:
        // <xs:group name="[Object]">
        // <xs:sequence>
        // <xs:element name="[Object]">
        // <xs:complexType>
        // <xs:complexContent>
        // <xs:extension base="objectReference">
        // <xs:all>
        // [AttributeSchema]
        // :
        // </xs:all>
        // </xs:extension>
        // </xs:complexContent>
        // </xs:complexType>
        // </xs:element>
        // </xs:sequence>
        // </xs:group>

        final Element group = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:group");
        group.setAttribute("name", name);

        final Element sequence = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:sequence");
        group.appendChild(sequence);

        final Element object = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:element");
        object.setAttribute("name", name);
        sequence.appendChild(object);

        final Element complex = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:complexType");
        object.appendChild(complex);

        final Element complexContent = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI,
                "xs:complexContent");
        complex.appendChild(complexContent);

        final Element extension = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:extension");
        extension.setAttribute("base", "objectReference");
        complexContent.appendChild(extension);

        final Element all = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:all");
        extension.appendChild(all);

        for (int i = 0; i < getAttributes().length; ++i) {
            if (getAttributes()[i].isIdentifier() || !getAttributes()[i].isReferential()) {
                all.appendChild(getAttributes()[i].getXMLSchema(document));
            }
        }

        if (isActive()) {
            final String currentStateTypeName = name + "-CurrentState";

            final Element currentState = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:element");
            currentState.setAttribute("name", "Current_State");
            currentState.setAttribute("type", currentStateTypeName);
            all.appendChild(currentState);

            final Element currentStateDef = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI,
                    "xs:simpleType");
            currentStateDef.setAttribute("name", currentStateTypeName);

            final Element restriction = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:restriction");
            restriction.setAttribute("base", "xs:string");
            currentStateDef.appendChild(restriction);

            for (final StateMetaData state : getStates()) {
                final Element enumeration = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI,
                        "xs:enumeration");
                enumeration.setAttribute("value", state.getName());
                restriction.appendChild(enumeration);
            }

            final Node fragment = document.createDocumentFragment();
            fragment.appendChild(group);
            fragment.appendChild(currentStateDef);
            return fragment;
        } else {
            return group;
        }

    }

    public Node getXMLUniqueIdentifierSchema(final Document document) {
        // Check that the analysis object identifier is unique
        // across the domain population
        //
        // Schema:
        // <xs:key name="[Object]__key">
        // <xs:selector xpath="[Object]"/>
        // <xs:field xpath="[Attribute]"/>
        // :
        // </xs:key>
        final Element key = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:key");
        key.setAttribute("name", name + "-key");

        final Element selector = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:selector");
        selector.setAttribute("xpath", name);
        key.appendChild(selector);

        for (int i = 0; i < getAttributes().length; ++i) {
            if (getAttributes()[i].isIdentifier()) {
                final Element field = document.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs:field");
                field.setAttribute("xpath", getAttributes()[i].getName());
                key.appendChild(field);
            }
        }
        return key;
    }

    public boolean isActive() {
        return getInstanceStates().length > 0;
    }

    @Override
    public String toString() {
        return name;
    }

    private void linkRelationships() {
        final List<FormalisedRelationship> rels = new ArrayList<FormalisedRelationship>();
        final List<FormalisedSuperSubtype> ssRels = new ArrayList<FormalisedSuperSubtype>();
        for (int i = 0; i < getRelationships().length; i++) {
            final ObjectRelationshipMetaData objRelMeta = getRelationships()[i];
            final String relNumber = objRelMeta.getNumber();

            final RelationshipMetaData relMeta = getDomain().getRelationship(relNumber);
            if (relMeta != null) {
                if (relMeta.getAssocObject() == null) {
                    // Simple Relationship
                    if (relMeta.getRightObject().equals(this)
                            && relMeta.getLeftRole().equals(objRelMeta.getRolePhrase())) {
                        // The current object is the right hand object of this relationship,
                        // so add relationship to list if it must be formalised here.
                        if (relMeta.getRightMany() || (!relMeta.getLeftMany() && relMeta.getRightConditional()
                                && !relMeta.getLeftConditional())) {
                            rels.add(new FormalisedRelationship(relMeta, i, -1));
                        }
                    } else if (relMeta.getLeftObject().equals(this)
                            && relMeta.getRightRole().equals(objRelMeta.getRolePhrase())) {
                        // The current object is the left hand object of this relationship,
                        // so add relationship to list if it can be formalised here.
                        if (!(relMeta.getRightMany() || (!relMeta.getLeftMany() && relMeta.getRightConditional()
                                && !relMeta.getLeftConditional()))) {
                            rels.add(new FormalisedRelationship(relMeta, -1, i));
                        }
                    } else {
                        // Something wrong....
                        System.err.println("Couldn't decode relationship " + relMeta.getNumber());
                        System.err.println("curObj=" + name + " curRole=" + objRelMeta.getRolePhrase());
                        System.err.println(
                                "leftObj=" + relMeta.getLeftObject().getName() + "leftRole=" + relMeta.getLeftRole());
                        System.err.println("rightObj=" + relMeta.getRightObject().getName() + "rightRole="
                                + relMeta.getRightRole());
                        System.err.println();
                    }
                } else {
                    // Associative Relationship
                    if (relMeta.getAssocObject().equals(this)) {
                        // Assume that the two object relationship components of the
                        // relationship are consecutive.
                        if (relMeta.getLeftRole().equals(objRelMeta.getRolePhrase())) {
                            rels.add(new FormalisedRelationship(relMeta, i, i + 1));
                        } else {
                            rels.add(new FormalisedRelationship(relMeta, i + 1, i));
                        }
                        ++i;
                    }
                }
            } else {
                // Relationship not found, so must be super-subtype
                final SuperSubtypeMetaData ssMeta = getDomain().getSuperSubtype(relNumber);

                if (ssMeta.getSupertype().equals(this)) {
                    final int relIdx = i;
                    boolean found = false;
                    for (int j = 0; j < ssMeta.getSubtypes().length && !found; ++j) {
                        if (ssMeta.getSubtypes()[j].equals(objRelMeta.getDestObject())) {
                            final int subIndex = j;
                            ssRels.add(new FormalisedSuperSubtype(ssMeta, relIdx, subIndex));
                            found = true;
                        }
                    }
                }
            }

        }

        formalisedRelationships = rels.toArray(new FormalisedRelationship[rels.size()]);
        formalisedSuperSubtypes = ssRels.toArray(new FormalisedSuperSubtype[ssRels.size()]);

    }

    protected String name;
    protected String keyLetters;
    private FormalisedRelationship[] formalisedRelationships = null;

    private FormalisedSuperSubtype[] formalisedSuperSubtypes = null;

    public String getFullyQualifiedName() {
        return getDomain().getName() + "::" + getName();
    }

    public abstract ObjectMetaData[] getPolymorphicSubObjects();

}
