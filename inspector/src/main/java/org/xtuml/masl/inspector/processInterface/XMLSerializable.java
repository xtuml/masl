//
// Filename : XMLSerializable.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.processInterface;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public interface XMLSerializable {

    // DOM
    public void fromXML(Node parent);

    public Node toXML(Document document);
}
