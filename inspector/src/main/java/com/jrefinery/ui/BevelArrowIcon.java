/*
 * ================================================================ JCommon : a
 * general purpose, open source, class library for Java
 * ================================================================
 *
 * Project Info: http://www.jrefinery.com/jcommon Project Lead: David Gilbert
 * (david.gilbert@jrefinery.com);
 *
 * (C) Copyright 2000, 2001 Simba Management Limited and Contributors;
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 *
 * ------------------- BevelArrowIcon.java ------------------- (C) Copyright
 * 2000, 2001 Nobuo Tamemasa;
 *
 * Original Author: Nobuo Tamemasa; Contributor(s): David Gilbert (for Simba
 * Management Limited);
 *
 * Changes (from 26-Oct-2001) -------------------------- 26-Oct-2001 : Changed
 * package to com.jrefinery.ui.*;
 */

package com.jrefinery.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.UIManager;

/**
 * An arrow icon that can point up or down (usually used to indicate the sort
 * direction in a table).
 * <P>
 * This class (and also SortButtonRenderer) is based on original code by Nobuo
 * Tamemasa (version 1.0, 26-Feb-1999) posted on www.codeguru.com.
 */
public class BevelArrowIcon implements Icon {

    /** Constant indicating that the arrow is pointing up; */
    public static final int UP = 0;

    /** Constant indicating that the arrow is pointing down; */
    public static final int DOWN = 1;

    /** The default arrow size; */
    private static final int DEFAULT_SIZE = 11;

    /** Edge color 1; */
    private Color edge1;

    /** Edge color 2; */
    private Color edge2;

    /** The fill color for the arrow icon; */
    private Color fill;

    /** The size of the icon; */
    private int size;

    /** The direction that the arrow is pointing (UP or DOWN); */
    private int direction;

    /**
     * Standard constructor - builds an icon with the specified attributes.
     *
     * @param direction     ;
     * @param isRaisedView  ;
     * @param isPressedView ;
     */
    public BevelArrowIcon(final int direction, final boolean isRaisedView, final boolean isPressedView) {
        if (isRaisedView) {
            if (isPressedView) {
                init(UIManager.getColor("controlLtHighlight"), UIManager.getColor("controlDkShadow"),
                        UIManager.getColor("controlShadow"), DEFAULT_SIZE, direction);
            } else {
                init(UIManager.getColor("controlHighlight"), UIManager.getColor("controlShadow"),
                        UIManager.getColor("control"), DEFAULT_SIZE, direction);
            }
        } else {
            if (isPressedView) {
                init(UIManager.getColor("controlDkShadow"), UIManager.getColor("controlLtHighlight"),
                        UIManager.getColor("controlShadow"), DEFAULT_SIZE, direction);
            } else {
                init(UIManager.getColor("controlShadow"), UIManager.getColor("controlHighlight"),
                        UIManager.getColor("control"), DEFAULT_SIZE, direction);
            }
        }
    }

    /**
     * Standard constructor - builds an icon with the specified attributes.
     *
     * @param edge1     The color of edge1;
     * @param edge2     The color of edge2;
     * @param fill      The fill color;
     * @param size      The size of the arrow icon;
     * @param direction The direction that the arrow points;
     */
    public BevelArrowIcon(final Color edge1, final Color edge2, final Color fill, final int size, final int direction) {
        init(edge1, edge2, fill, size, direction);
    }

    /**
     * Paints the icon at the specified position. Supports the Icon interface.
     *
     * @param c ;
     * @param g ;
     * @param x ;
     * @param y ;
     */
    @Override
    public void paintIcon(final Component c, final Graphics g, final int x, final int y) {
        switch (direction) {
        case DOWN:
            drawDownArrow(g, x, y);
            break;
        case UP:
            drawUpArrow(g, x, y);
            break;
        }
    }

    public void setSize(final int size) {
        this.size = size;
    }

    /**
     * Returns the width of the icon. Supports the Icon interface.
     *
     * @return The icon width;
     */
    @Override
    public int getIconWidth() {
        return size;
    }

    /**
     * Returns the height of the icon. Supports the Icon interface.
     *
     * @return The icon height;
     */
    @Override
    public int getIconHeight() {
        return size;
    }

    /**
     * Initialises the attributes of the arrow icon.
     */
    private void init(final Color edge1, final Color edge2, final Color fill, final int size, final int direction) {
        this.edge1 = edge1;
        this.edge2 = edge2;
        this.fill = fill;
        this.size = size;
        this.direction = direction;
    }

    /**
     * Draws the arrow pointing down.
     */
    private void drawDownArrow(final Graphics g, final int xo, final int yo) {
        g.setColor(edge1);
        g.drawLine(xo, yo, xo + size - 1, yo);
        g.drawLine(xo, yo + 1, xo + size - 3, yo + 1);
        g.setColor(edge2);
        g.drawLine(xo + size - 2, yo + 1, xo + size - 1, yo + 1);
        int x = xo + 1;
        int y = yo + 2;
        int dx = size - 6;
        while (y + 1 < yo + size) {
            g.setColor(edge1);
            g.drawLine(x, y, x + 1, y);
            g.drawLine(x, y + 1, x + 1, y + 1);
            if (0 < dx) {
                g.setColor(fill);
                g.drawLine(x + 2, y, x + 1 + dx, y);
                g.drawLine(x + 2, y + 1, x + 1 + dx, y + 1);
            }
            g.setColor(edge2);
            g.drawLine(x + dx + 2, y, x + dx + 3, y);
            g.drawLine(x + dx + 2, y + 1, x + dx + 3, y + 1);
            x += 1;
            y += 2;
            dx -= 2;
        }
        g.setColor(edge1);
        g.drawLine(xo + (size / 2), yo + size - 1, xo + (size / 2), yo + size - 1);
    }

    /**
     * Draws the arrow pointing up.
     */
    private void drawUpArrow(final Graphics g, final int xo, final int yo) {
        g.setColor(edge1);
        int x = xo + (size / 2);
        g.drawLine(x, yo, x, yo);
        x--;
        int y = yo + 1;
        int dx = 0;
        while (y + 3 < yo + size) {
            g.setColor(edge1);
            g.drawLine(x, y, x + 1, y);
            g.drawLine(x, y + 1, x + 1, y + 1);
            if (0 < dx) {
                g.setColor(fill);
                g.drawLine(x + 2, y, x + 1 + dx, y);
                g.drawLine(x + 2, y + 1, x + 1 + dx, y + 1);
            }
            g.setColor(edge2);
            g.drawLine(x + dx + 2, y, x + dx + 3, y);
            g.drawLine(x + dx + 2, y + 1, x + dx + 3, y + 1);
            x -= 1;
            y += 2;
            dx += 2;
        }
        g.setColor(edge1);
        g.drawLine(xo, yo + size - 3, xo + 1, yo + size - 3);
        g.setColor(edge2);
        g.drawLine(xo + 2, yo + size - 2, xo + size - 1, yo + size - 2);
        g.drawLine(xo, yo + size - 1, xo + size, yo + size - 1);
    }

}
