//
// Filename : EventData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.processInterface;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class EventData implements org.xtuml.masl.inspector.processInterface.XMLSerializable {

    @Override
    public void fromXML(final Node parent) {
        for (int i = 0; parent != null && i < parent.getChildNodes().getLength(); ++i) {
            final Node eventNode = parent.getChildNodes().item(i);
            if (eventNode.getNodeType() == Node.ELEMENT_NODE) {
                final String fullEventName = eventNode.getNodeName();

                final String[] split = fullEventName.split("\\.");
                final String parentObject = split[0];
                final String eventName = split[1];

                destObject = domain.getObject(parentObject);
                final Map<String, Node> attributeNodes = new HashMap<String, Node>();
                for (int j = 0; j < eventNode.getChildNodes().getLength(); ++j) {
                    final Node curNode = eventNode.getChildNodes().item(j);
                    if (curNode.getNodeType() == Node.ELEMENT_NODE) {
                        if (curNode.getNodeName().equals("source")) {
                            sourceInstanceId = createInstanceId();
                            sourceInstanceId.fromXML(curNode);
                            sourceObject = sourceInstanceId.getMetaData();
                        } else if (curNode.getNodeName().equals("destination")) {
                            destInstanceId = createInstanceId();
                            destInstanceId.fromXML(curNode);
                            destObject = destInstanceId.getMetaData();
                        } else {
                            attributeNodes.put(curNode.getNodeName(), curNode);
                        }
                    }
                }

                for (int k = 0; event == null && k < destObject.getEvents().length; ++k) {
                    if (destObject.getEvents()[k].getName().equals(eventName)) {
                        event = destObject.getEvents()[k];
                    }
                }

                parameters = new DataValue[event.getParameters().length];

                for (int j = 0; j < event.getParameters().length; j++) {
                    parameters[j] = event.getParameters()[j].getType().getDataObject();
                    parameters[j].fromXML(attributeNodes.get(event.getParameters()[j].getName()));
                }

            }

        }

    }

    @Override
    public Node toXML(final Document document) {
        final Element eventNode = document
                .createElement(getEvent().getParentObject().getName() + "." + getEvent().getName());

        if (getSourceInstanceId() != null) {
            final Element sourceNode = document.createElement("source");
            eventNode.appendChild(sourceNode);
            sourceNode.appendChild(getSourceInstanceId().toXML(document));
        }

        if (getDestInstanceId() != null) {
            final Element destNode = document.createElement("destination");
            eventNode.appendChild(destNode);
            destNode.appendChild(getDestInstanceId().toXML(document));
        }

        for (int i = 0; i < getEvent().getParameters().length; ++i) {
            final Node paramNode = document.createElement(getEvent().getParameters()[i].getName());
            eventNode.appendChild(paramNode);
            paramNode.appendChild(getParameters()[i].toXML(document));
        }

        return eventNode;
    }

    public ObjectMetaData getDestObject() {
        return destObject;
    }

    public ObjectMetaData getSourceObject() {
        return sourceObject;
    }

    public EventMetaData getEvent() {
        return event;
    }

    public InstanceIdData getSourceInstanceId() {
        return sourceInstanceId;
    }

    public InstanceIdData getDestInstanceId() {
        return destInstanceId;
    }

    public DataValue<?>[] getParameters() {
        return parameters;
    }

    protected abstract InstanceIdData createInstanceId();

    protected EventMetaData event;

    protected ObjectMetaData sourceObject;
    protected InstanceIdData sourceInstanceId;

    protected ObjectMetaData destObject;
    protected InstanceIdData destInstanceId;
    protected DataValue<?>[] parameters;

    protected DomainMetaData domain;

}
