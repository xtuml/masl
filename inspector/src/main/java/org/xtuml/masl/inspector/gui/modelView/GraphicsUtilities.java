//
// Filename : GraphicsUtilities.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui.modelView;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;

class GraphicsUtilities {

    public static void drawStringRight(final Graphics g, final FontMetrics m, final String str, final int x,
            final int y) {
        g.drawString(str, x - m.stringWidth(str), y);
    }

    public static void drawStringCentre(final Graphics g, final FontMetrics m, final String str, final int x1,
            final int x2, final int y) {
        g.drawString(str, (x2 + x1 - m.stringWidth(str)) / 2, y);
    }

    public static void drawAt(final Graphics2D g, final double x, final double y, final Shape s) {
        g.translate(x, y);
        g.draw(s);
        g.translate(-x, -y);
    }
}
