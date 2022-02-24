// 
// Filename : NoSelectTableCellRenderer.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import java.io.Serializable;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

class NoSelectTableCellRenderer extends JLabel implements TableCellRenderer, Serializable {

    public NoSelectTableCellRenderer() {
        super();
        setOpaque(true);
        setBorder(new EmptyBorder(1, 1, 1, 1));
    }

    @Override
    public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected,
            final boolean hasFocus, final int row, final int column) {
        setFont(table.getFont());
        setValue(value);
        return this;
    }

    /**
     * Overridden for performance reasons. See the
     * <a href="#override">Implementation Note</a> for more information.
     */
    @Override
    public boolean isOpaque() {
        final Color back = getBackground();
        Component p = getParent();
        if (p != null) {
            p = p.getParent();
        }
        // p should now be the JTable.
        final boolean colorMatch = (back != null) && (p != null) && back.equals(p.getBackground()) && p.isOpaque();
        return !colorMatch && super.isOpaque();
    }

    /**
     * Overridden for performance reasons. See the
     * <a href="#override">Implementation Note</a> for more information.
     */
    @Override
    public void validate() {
    }

    /**
     * Overridden for performance reasons. See the
     * <a href="#override">Implementation Note</a> for more information.
     */
    @Override
    public void revalidate() {
    }

    /**
     * Overridden for performance reasons. See the
     * <a href="#override">Implementation Note</a> for more information.
     */
    @Override
    public void repaint(final long tm, final int x, final int y, final int width, final int height) {
    }

    /**
     * Overridden for performance reasons. See the
     * <a href="#override">Implementation Note</a> for more information.
     */
    @Override
    public void repaint(final Rectangle r) {
    }

    /**
     * Overridden for performance reasons. See the
     * <a href="#override">Implementation Note</a> for more information.
     */
    @Override
    protected void firePropertyChange(final String propertyName, final Object oldValue, final Object newValue) {
        // Strings get interned...
        if (propertyName == "text") {
            super.firePropertyChange(propertyName, oldValue, newValue);
        }
    }

    /**
     * Overridden for performance reasons. See the
     * <a href="#override">Implementation Note</a> for more information.
     */
    @Override
    public void firePropertyChange(final String propertyName, final boolean oldValue, final boolean newValue) {
    }

    /**
     * Sets the <code>String</code> object for the cell being rendered to
     * <code>value</code>.
     * 
     * @param value the string value for this cell; if value is <code>null</code> it
     *              sets the text value to an empty string
     * @see JLabel#setText
     * 
     */
    protected void setValue(final Object value) {
        setText((value == null) ? "" : value.toString());
    }

    /**
     * A subclass of <code>DefaultTableCellRenderer</code> that implements
     * <code>UIResource</code>. <code>DefaultTableCellRenderer</code> doesn't
     * implement <code>UIResource</code> directly so that applications can safely
     * override the <code>cellRenderer</code> property with
     * <code>DefaultTableCellRenderer</code> subclasses.
     * <p>
     * <strong>Warning:</strong> Serialized objects of this class will not be
     * compatible with future Swing releases. The current serialization support is
     * appropriate for short term storage or RMI between applications running the
     * same version of Swing. As of 1.4, support for long term storage of all
     * JavaBeans<sup><font size="-2">TM</font></sup> has been added to the
     * <code>java.beans</code> package. Please see {@link java.beans.XMLEncoder}.
     */
    public static class UIResource extends NoSelectTableCellRenderer implements javax.swing.plaf.UIResource {
    }

}
