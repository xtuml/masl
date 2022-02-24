//
// Filename : StructureData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.processInterface;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;

public abstract class StructureData extends DataValue<StructureData> {

    protected StructureMetaData meta;
    protected DataValue<?>[] attributes;

    protected StructureData(final StructureMetaData meta) {
        this.meta = meta;
        attributes = new DataValue[meta.getAttributes().length];
        for (int i = 0; i < attributes.length; ++i) {
            attributes[i] = meta.getAttributes()[i].getDefaultValue();
        }

    }

    public StructureMetaData getMetaData() {
        return meta;
    }

    public DataValue<?>[] getAttributes() {
        return attributes;
    }

    public void setAttributes(final DataValue<?>[] attributes) {
        this.attributes = attributes;
    }

    @Override
    public Node toXML(final Document document) {
        final DocumentFragment fragment = document.createDocumentFragment();

        for (int i = 0; i < meta.getAttributes().length; ++i) {
            final Node attNode = document.createElement(meta.getAttributes()[i].getName());
            fragment.appendChild(attNode);
            attNode.appendChild(attributes[i].toXML(document));
        }
        return fragment;
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
    }

    @Override
    public String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append("{");
        for (int i = 0; i < attributes.length; i++) {
            buf.append(meta.getAttributes()[i].getName() + "=" + attributes[i]);
            if (i < attributes.length - 1) {
                buf.append(", ");
            }
        }
        buf.append("}");
        return buf.toString();
    }

    @Override
    public StructureData getValue() {
        return this;
    }

    @Override
    public void setValue(final StructureData value) {
        attributes = value.attributes;
    }

}
