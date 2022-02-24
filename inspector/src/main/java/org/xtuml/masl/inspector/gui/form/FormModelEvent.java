//
// File: FormModelEvent.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.inspector.gui.form;

import java.util.EventObject;

/**
 * FormModelEvent is used to notify listeners that a form model has changed. The
 * model event describes changes to a FormModel and all references to fields are
 * in the co-ordinate system of the model. Depending on the parameters used in
 * the constructors, the FormModelevent can be used to specify the following
 * types of changes:
 * <p>
 *
 * <pre>
 * FormModelEvent(source, false); // Values in all fields changed
 * FormModelEvent(source, true); // Form structure changed
 * FormModelEvent(source, 1); // Value in field 1 was changed
 * </pre>
 *
 * @see FormModel
 */

public class FormModelEvent extends EventObject {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /** Specifies all fields on the form. */
    public static final int ALL_FIELDS = -1;

    //
    // Instance Variables
    //

    protected boolean structureChanged;
    protected int field;

    //
    // Constructors
    //

    /**
     * All data in the table has changed, listeners should discard any state that
     * was based on the values or structure of the form and requery the
     * <code>FormModel</code> to get the new values. The structure of the form ie,
     * the field names, types and order may have changed, depending on the value of
     * <code>structureChanged</code>
     */
    public FormModelEvent(final FormModel source, final boolean structureChanged) {
        this(source, ALL_FIELDS, structureChanged);
    }

    /**
     * The value in field <I>field</I> has been updated.
     */
    public FormModelEvent(final FormModel source, final int field) {
        this(source, field, false);
    }

    /**
     * The cells from (firstRow, field) to (lastRow, field) have been changed. The
     * <I>field</I> refers to the field index of the cell in the model's co-ordinate
     * system. When <I>field</I> is ALL_FIELDS, all cells in the specified range of
     * rows are considered changed.
     * <p>
     */
    private FormModelEvent(final FormModel source, final int field, final boolean structureChanged) {
        super(source);
        this.field = field;
        this.structureChanged = structureChanged;
    }

    //
    // Querying Methods
    //

    /**
     * Returns the field for the event. If the return value is ALL_FIELDS; it means
     * every field changed.
     */
    public int getField() {
        return field;
    }

    /**
     * Returns whether the structure of the table changed.
     */
    public boolean getStructureChanged() {
        return structureChanged;
    }

}
