//
// Filename : InstanceData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.processInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xtuml.masl.inspector.processInterface.ObjectMetaData.FormalisedRelationship;
import org.xtuml.masl.inspector.processInterface.ObjectMetaData.FormalisedSuperSubtype;

public abstract class InstanceData implements org.xtuml.masl.inspector.processInterface.XMLSerializable {

    protected ObjectMetaData meta;
    protected Integer primaryKey;
    protected Integer currentState;
    protected DataValue<?>[] attributes;
    protected int[] relCounts;
    protected Integer[] relatedIds;

    protected InstanceData(final ObjectMetaData meta) {
        this.meta = meta;
        relCounts = new int[getMetaData().getRelationships().length];
        relatedIds = new Integer[getMetaData().getRelationships().length];
    }

    public ObjectMetaData getMetaData() {
        return meta;
    }

    public Integer getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(final Integer primaryKey) {
        this.primaryKey = primaryKey;
    }

    public DataValue<?>[] getAttributes() {
        return attributes;
    }

    public void setAttributes(final DataValue<?>[] attributes) {
        this.attributes = attributes;
    }

    public int[] getRelCounts() {
        return relCounts;
    }

    public void setRelCounts(final int[] relCounts) {
        this.relCounts = relCounts;
    }

    public Integer[] getRelatedIds() {
        return relatedIds;
    }

    public void setRelatedIds(final Integer[] relatedIds) {
        this.relatedIds = relatedIds;
    }

    @Override
    public Node toXML(final Document document) {
        final Element instance = document.createElement(meta.getName());
        instance.setAttribute("id", getPrimaryKey().toString());

        for (int i = 0; i < meta.getAttributes().length; ++i) {
            final AttributeMetaData attMeta = meta.getAttributes()[i];
            if (attMeta.isIdentifier() || !attMeta.isReferential()) {
                final Node attNode = document.createElement(meta.getAttributes()[i].getName());
                instance.appendChild(attNode);
                attNode.appendChild(attributes[i].toXML(document));
            }
        }

        if (meta.isActive()) {
            final Node csNode = document.createElement("Current_State");
            instance.appendChild(csNode);
            csNode.appendChild(document.createTextNode(meta.getStates()[currentState].getName()));
        }

        return instance;
    }

    @Override
    public void fromXML(final Node parent) {
        attributes = new DataValue[meta.getAttributes().length];

        final Map<String, Node> attributeNodes = new HashMap<String, Node>();
        for (int i = 0; parent != null && i < parent.getChildNodes().getLength(); ++i) {
            final Node curNode = parent.getChildNodes().item(i);
            if (curNode.getNodeType() == Node.ELEMENT_NODE) {
                attributeNodes.put(curNode.getNodeName(), curNode);
            }
        }

        for (int i = 0; i < meta.getAttributes().length; i++) {
            if (!meta.getAttributes()[i].isReadOnly()) {
                attributes[i] = meta.getAttributes()[i].getType().getDataObject();
                attributes[i].fromXML(attributeNodes.get(meta.getAttributes()[i].getName()));
            }
        }

        if (meta.isActive()) {
            currentState = meta.getStateIndex(attributeNodes.get("Current_State").getFirstChild().getNodeValue());
        }
    }

    public RelationshipData[] getFormalisedRelationships() {
        final ObjectMetaData.FormalisedRelationship[] rels = meta.getFormalisedRelationships();

        final List<RelationshipData> result = new ArrayList<RelationshipData>();
        for (FormalisedRelationship rel : rels) {
            Object leftId;
            Object rightId;
            Object assocId;

            if (rel.leftRelIdx == -1) {
                leftId = primaryKey;
                rightId = relatedIds[rel.rightRelIdx];
                assocId = null;
            } else if (rel.rightRelIdx == -1) {
                leftId = relatedIds[rel.leftRelIdx];
                rightId = primaryKey;
                assocId = null;
            } else {
                leftId = relatedIds[rel.leftRelIdx];
                rightId = relatedIds[rel.rightRelIdx];
                assocId = primaryKey;
            }

            // Check for null ids for conditional relationships.
            if (leftId != null && rightId != null) {
                final RelationshipData relData = rel.meta.getRelationshipData();
                relData.setLeftId(leftId);
                relData.setRightId(rightId);
                relData.setAssocId(assocId);
                result.add(relData);
            }
        }
        return result.toArray(new RelationshipData[result.size()]);
    }

    public SuperSubtypeData[] getFormalisedSuperSubtypes() {
        final ObjectMetaData.FormalisedSuperSubtype[] rels = meta.getFormalisedSuperSubtypes();

        final List<SuperSubtypeData> result = new ArrayList<SuperSubtypeData>();
        for (FormalisedSuperSubtype rel : rels) {
            if (relatedIds[rel.relIdx] != null) {
                final SuperSubtypeData relData = rel.meta.getSuperSubtypeData();
                relData.setSupertypeId(primaryKey);
                relData.setSubtypeId(relatedIds[rel.relIdx]);
                relData.setSubtypeIndex(rel.subIndex);

                result.add(relData);
            }
        }
        return result.toArray(new SuperSubtypeData[result.size()]);

    }

    public int getCurrentState() {
        return currentState;
    }

}
