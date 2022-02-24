//
// Filename : RelationshipData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.processInterface;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class RelationshipData implements org.xtuml.masl.inspector.processInterface.XMLSerializable {

    protected RelationshipMetaData meta;
    protected Object leftId;
    protected Object rightId;
    protected Object assocId;

    public RelationshipData(final RelationshipMetaData meta) {
        this.meta = meta;
    }

    public RelationshipMetaData getMetaData() {
        return meta;
    }

    public void setLeftId(final Object leftId) {
        this.leftId = leftId;
    }

    public Object getLeftId() {
        return leftId;
    }

    public void setRightId(final Object rightId) {
        this.rightId = rightId;
    }

    public Object getRightId() {
        return rightId;
    }

    public void setAssocId(final Object assocId) {
        this.assocId = assocId;
    }

    public Object getAssocId() {
        return assocId;
    }

    @Override
    public Node toXML(final Document document) {
        String leftName = meta.getLeftObject().getName();
        String rightName = meta.getRightObject().getName();

        if (meta.getLeftObject().equals(meta.getRightObject())) {
            leftName = leftName + "." + meta.getLeftRole();
            rightName = rightName + "." + meta.getRightRole();
        }
        final Element relNode = document.createElement(meta.getNumber());
        final Element left = document.createElement(leftName);
        left.setAttribute("id", leftId.toString());
        relNode.appendChild(left);

        final Element right = document.createElement(rightName);
        right.setAttribute("id", rightId.toString());
        relNode.appendChild(right);

        if (assocId != null) {
            final Element assoc = document.createElement(meta.getAssocObject().getName());
            assoc.setAttribute("id", assocId.toString());
            relNode.appendChild(assoc);
        }

        return relNode;
    }

    @Override
    public void fromXML(final Node parent) {
        String leftName = meta.getLeftObject().getName();
        String rightName = meta.getRightObject().getName();

        if (meta.getLeftObject().equals(meta.getRightObject())) {
            leftName = leftName + "." + meta.getLeftRole();
            rightName = rightName + "." + meta.getRightRole();
        }

        for (int i = 0; i < parent.getChildNodes().getLength(); ++i) {
            final Node curNode = parent.getChildNodes().item(i);
            if (curNode.getNodeType() == Node.ELEMENT_NODE) {
                final String objName = curNode.getNodeName();
                final Integer instanceId = Integer.valueOf(curNode.getAttributes().getNamedItem("id").getNodeValue());

                if (objName.equals(leftName)) {
                    leftId = instanceId;
                } else if (objName.equals(rightName)) {
                    rightId = instanceId;
                } else if (meta.getAssocObject() != null && objName.equals(meta.getAssocObject().getName())) {
                    assocId = instanceId;
                }
            }
        }
    }
}
