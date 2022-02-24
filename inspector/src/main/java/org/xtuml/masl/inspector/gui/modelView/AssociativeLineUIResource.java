// 
// Filename : AssociativeLineUIResource.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui.modelView;

import java.awt.Stroke;

import javax.swing.UIManager;

public class AssociativeLineUIResource implements javax.swing.plaf.UIResource {

    private final java.awt.Shape arrowHead;
    private final Stroke lineStyle;

    public AssociativeLineUIResource(final java.awt.Shape arrowHead, final Stroke lineStyle) {
        this.arrowHead = arrowHead;
        this.lineStyle = lineStyle;
    }

    public java.awt.Shape getArrowHead() {
        return arrowHead;
    }

    public Stroke getLineStyle() {
        return lineStyle;
    }

    public static AssociativeLineUIResource getLine(final boolean many) {
        if (many) {
            return (AssociativeLineUIResource) UIManager.get("Associative.M");
        } else {
            return (AssociativeLineUIResource) UIManager.get("Associative.1");
        }
    }
}
