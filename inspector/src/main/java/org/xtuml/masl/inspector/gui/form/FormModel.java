//
// File: FormModel.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.inspector.gui.form;

public interface FormModel {

    /**
     * Returns the number of fields in the model. A <code>Form</code> uses this
     * method to determine how many fields it should display.
     *
     * @return the number of fields in the model
     */
    public int getFieldCount();

    /**
     * Returns the name of the field at <code>fieldIndex</code>. This is used to
     * initialize the form's field header name. Note: this name does not need to be
     * unique; two fields in a form can have the same name.
     *
     * @param fieldIndex the index of the field
     * @return the name of the field
     */
    public String getFieldName(int fieldIndex);

    /**
     * Returns the most specific superclass for all the cell values in the field.
     * This is used by the <code>Form</code> to set up a default renderer and editor
     * for the field.
     *
     * @param fieldIndex the index of the field
     * @return the common ancestor class of the object values in the model.
     */
    public Class<?> getFieldClass(int fieldIndex);

    /**
     * Returns true if the value at <code>rowIndex</code> and
     * <code>fieldIndex</code> is editable. Otherwise, <code>setValueAt</code> on
     * the cell will not change the value of that cell.
     *
     * @param rowIndex   the row whose value to be queried
     * @param fieldIndex the field whose value to be queried
     * @return true if the cell is editable
     * @see #setValueAt
     */
    public boolean isValueEditable(int fieldIndex);

    /**
     * Returns the value for the field at <code>fieldIndex</code>.
     *
     * @param fieldIndex the field whose value is to be queried
     * @return the value Object at the specified cell
     */
    public Object getValueAt(int fieldIndex);

    /**
     * Sets the value in the field at <code>fieldIndex</code> to
     * <code>aValue</code>.
     *
     * @param aValue     the new value
     * @param fieldIndex the field whose value is to be changed
     * @see #getValueAt
     * @see #isCellEditable
     */
    public void setValueAt(Object aValue, int fieldIndex);

    /**
     * Adds a listener to the list that is notified each time a change to the data
     * model occurs.
     *
     * @param l the FormModelListener
     */
    public void addFormModelListener(FormModelListener l);

    /**
     * Removes a listener from the list that is notified each time a change to the
     * data model occurs.
     *
     * @param l the FormModelListener
     */
    public void removeFormModelListener(FormModelListener l);
}
