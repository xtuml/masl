//
// Filename : SuperSubtypeData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.processInterface;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class SuperSubtypeData implements org.xtuml.masl.inspector.processInterface.XMLSerializable {

    protected SuperSubtypeMetaData meta;
    protected Object supertypeId;
    protected Object subtypeId;
    protected int subtypeIndex;

    public SuperSubtypeData(final SuperSubtypeMetaData meta) {
        this.meta = meta;
    }

    public SuperSubtypeMetaData getMetaData() {
        return meta;
    }

    public void setSupertypeId(final Object supertypeId) {
        this.supertypeId = supertypeId;
    }

    public Object getSupertypeId() {
        return supertypeId;
    }

    public void setSubtypeId(final Object subtypeId) {
        this.subtypeId = subtypeId;
    }

    public Object getSubtypeId() {
        return subtypeId;
    }

    public void setSubtypeIndex(final int subtypeIndex) {
        this.subtypeIndex = subtypeIndex;
    }

    public int getSubtypeIndex() {
        return subtypeIndex;
    }

    @Override
    public Node toXML(final Document document) {
        final Element relNode = document.createElement(meta.getNumber());
        final Element supertype = document.createElement(meta.getSupertype().getName());
        supertype.setAttribute("id", supertypeId.toString());
        relNode.appendChild(supertype);

        final Element subtype = document.createElement(meta.getSubtypes()[subtypeIndex].getName());
        subtype.setAttribute("id", subtypeId.toString());
        relNode.appendChild(subtype);

        return relNode;
    }

    @Override
    public void fromXML(final Node parent) {
        for (int i = 0; i < parent.getChildNodes().getLength(); ++i) {
            final Node curNode = parent.getChildNodes().item(i);
            if (curNode.getNodeType() == Node.ELEMENT_NODE) {
                final String objName = curNode.getNodeName();
                final Integer instanceId = Integer.valueOf(curNode.getAttributes().getNamedItem("id").getNodeValue());

                if (objName.equals(meta.getSupertype().getName())) {
                    supertypeId = instanceId;
                } else {
                    subtypeId = null;
                    for (int j = 0; j < meta.getSubtypes().length && subtypeId == null; ++j) {
                        if (objName.equals(meta.getSubtypes()[j].getName())) {
                            subtypeId = instanceId;
                            subtypeIndex = j;
                        }
                    }
                }
            }
        }
    }
}
