//  
// Filename : CollectionData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.processInterface;

import java.util.Map;
import java.util.TreeMap;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class DictionaryData extends DataValue<DictionaryData> implements Comparable<DictionaryData> {

    @Override
    public DictionaryData getValue() {
        return this;
    }

    @Override
    public void setValue(final DictionaryData value) {
        startIndex = value.startIndex;
        data = value.data;
    }

    protected int startIndex = 1;
    protected Map<DataValue<?>, DataValue<?>> data = new TreeMap<DataValue<?>, DataValue<?>>();
    protected TypeMetaData keyType;
    protected TypeMetaData valueType;

    public DictionaryData(final TypeMetaData keyType, final TypeMetaData valueType) {
        this.keyType = keyType;
        this.valueType = valueType;
    }

    public TypeMetaData getKeyType() {
        return keyType;
    }

    public TypeMetaData getValueType() {
        return valueType;
    }

    public Map<DataValue<?>, DataValue<?>> getData() {
        return data;
    }

    public void setData(final Map<DataValue<?>, DataValue<?>> data) {
        this.data = data;
    }

    @Override
    public Node toXML(final Document document) {
        final DocumentFragment fragment = document.createDocumentFragment();
        for (final Map.Entry<DataValue<?>, DataValue<?>> entry : data.entrySet()) {
            final Node keyNode = document.createElement("key");
            fragment.appendChild(keyNode);
            keyNode.appendChild(entry.getKey().toXML(document));

            final Node valueNode = document.createElement("value");
            fragment.appendChild(valueNode);
            valueNode.appendChild(entry.getValue().toXML(document));

        }
        return fragment;
    }

    @Override
    public void fromXML(final Node parent) {
        data.clear();

        DataValue<?> key = null;
        DataValue<?> value = null;

        for (int i = 0; parent != null && i < parent.getChildNodes().getLength(); ++i) {
            if (parent.getChildNodes().item(i).getNodeType() == Node.ELEMENT_NODE) {
                final Element node = (Element) parent.getChildNodes().item(i);
                if (node.getTagName() == "key") {
                    key = keyType.getDataObject();
                    key.fromXML(node);
                } else if (node.getTagName() == "value") {
                    value = valueType.getDataObject();
                    value.fromXML(node);
                    data.put(key, value);
                }
            }
        }
    }

    @Override
    public String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append("{");
        int i = 0;
        for (final Map.Entry<DataValue<?>, DataValue<?>> entry : data.entrySet()) {
            buf.append("[" + entry.getKey() + "]=" + entry.getValue());
            if (++i < data.entrySet().size()) {
                buf.append(", ");
            }
        }
        buf.append("}");
        return buf.toString();
    }

    @Override
    public int compareTo(final DictionaryData rhs) {
        return data.size() == rhs.data.size() ? 0 : (data.size() < rhs.data.size() ? -1 : 1);
    }
}
