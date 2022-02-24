// 
// Filename : ArrowHeads.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui.modelView;

import java.awt.geom.GeneralPath;

class ArrowHeads {

    public final static GeneralPath SINGLE_V;
    public final static GeneralPath DOUBLE_V;
    public final static GeneralPath NO_ARROW;
    public final static GeneralPath CROSS_LINE;
    public final static GeneralPath EMPTY_TRIANGLE;

    static {

        // .
        NO_ARROW = new GeneralPath();

        // /
        // -
        // \
        SINGLE_V = new GeneralPath();
        SINGLE_V.moveTo(0, 0);
        SINGLE_V.lineTo(0.5f, 0);
        SINGLE_V.moveTo(0, 0);
        SINGLE_V.lineTo(0.5f, 0.5f);
        SINGLE_V.moveTo(0, 0);
        SINGLE_V.lineTo(0.5f, -0.5f);

        // //
        // --
        // \\
        DOUBLE_V = new GeneralPath();
        DOUBLE_V.moveTo(0, 0);
        DOUBLE_V.lineTo(0.5f, 0);
        DOUBLE_V.moveTo(0, 0);
        DOUBLE_V.lineTo(0.5f, 0.5f);
        DOUBLE_V.moveTo(0, 0);
        DOUBLE_V.lineTo(0.5f, -0.5f);
        DOUBLE_V.moveTo(0.5f, 0);
        DOUBLE_V.lineTo(1, 0);
        DOUBLE_V.moveTo(0.5f, 0);
        DOUBLE_V.lineTo(1, 0.5f);
        DOUBLE_V.moveTo(0.5f, 0);
        DOUBLE_V.lineTo(1, -0.5f);

        // -|
        CROSS_LINE = new GeneralPath();
        CROSS_LINE.moveTo(0, 0);
        CROSS_LINE.lineTo(1, 0);
        CROSS_LINE.moveTo(1, -0.5f);
        CROSS_LINE.lineTo(1, 0.5f);

        // /|
        // \|
        EMPTY_TRIANGLE = new GeneralPath();
        EMPTY_TRIANGLE.moveTo(0, 0);
        EMPTY_TRIANGLE.lineTo(1, -0.5f);
        EMPTY_TRIANGLE.lineTo(1, 0.5f);
        EMPTY_TRIANGLE.lineTo(0, 0);
    }
}
