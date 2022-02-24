// 
// Filename : InstanceData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.processInterface;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class TimerData extends DataValue<TimerData> implements Comparable<TimerData> {

    public EventData getEventData() {
        return event;
    }

    public boolean isScheduled() {
        return isScheduled;
    }

    public TimestampData getExpiryTime() {
        return expiryTime;
    }

    public DurationData getPeriod() {
        return period;
    }

    public Integer getId() {
        return id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        if (isScheduled()) {
            return "Scheduled @ " + getExpiryTime();
        } else {
            return "Inactive";
        }
    }

    protected abstract TimestampData getExpiryObject();

    protected abstract DurationData getPeriodObject();

    protected abstract EventData getEventObject();

    @Override
    public void fromXML(final Node parent) {
        id = null;
        period = null;
        isScheduled = false;
        event = null;
        expiryTime = null;

        for (int i = 0; parent != null && i < parent.getChildNodes().getLength(); ++i) {
            final Node curNode = parent.getChildNodes().item(i);
            if (curNode.getNodeType() == Node.ELEMENT_NODE) {
                if (curNode.getNodeName() == "expiry") {
                    isScheduled = true;
                    expiryTime = getExpiryObject();
                    expiryTime.fromXML(curNode);
                } else if (curNode.getNodeName() == "period") {
                    period = getPeriodObject();
                    period.fromXML(curNode);
                } else if (curNode.getNodeName() == "event") {
                    event = getEventObject();
                    event.fromXML(curNode);
                }
            }

        }

    }

    @Override
    public Node toXML(final Document document) {
        final DocumentFragment fragment = document.createDocumentFragment();
        if (isScheduled()) {
            final Element expiryNode = document.createElement("expiry");
            expiryNode.appendChild(getExpiryTime().toXML(document));
            fragment.appendChild(expiryNode);

            if (getPeriod() != null) {
                final Element periodNode = document.createElement("period");
                periodNode.appendChild(getPeriod().toXML(document));
                fragment.appendChild(periodNode);
            }

            final Element eventNode = document.createElement("event");
            eventNode.appendChild(getEventData().toXML(document));
            fragment.appendChild(eventNode);

        }

        return fragment;
    }

    protected Integer id;
    protected TimestampData expiryTime;
    protected DurationData period;
    protected EventData event;
    protected boolean isScheduled;

}
