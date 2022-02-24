// 
// Filename : PreferredSizeLayout.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui.modelView;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

import javax.swing.JComponent;

public class PreferredSizeLayout implements LayoutManager, java.io.Serializable {

    @Override
    public void addLayoutComponent(final String name, final Component comp) {
    }

    @Override
    public void removeLayoutComponent(final Component comp) {
    }

    @Override
    public Dimension minimumLayoutSize(final Container target) {
        return ((JComponent) target).getMinimumSize();
    }

    @Override
    public Dimension preferredLayoutSize(final Container target) {
        return ((JComponent) target).getPreferredSize();
    }

    @Override
    public void layoutContainer(final Container parent) {
        for (int i = 0; i < parent.getComponentCount(); i++) {
            final Component m = parent.getComponent(i);
            if (m.isVisible()) {
                final Dimension d = m.getPreferredSize();
                m.setSize(d.width, d.height);
            }
        }

    }

}
