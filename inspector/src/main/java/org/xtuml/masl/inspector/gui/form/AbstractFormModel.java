//
// File: AbstractFormModel.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.inspector.gui.form;

import java.util.EventListener;

import javax.swing.event.EventListenerList;


/**
 * This abstract class provides default implementations for most of the methods
 * in the <code>FormModel</code> interface. It takes care of the management of
 * listeners and provides some conveniences for generating
 * <code>FormModelEvents</code> and dispatching them to the listeners. To create
 * a concrete <code>FormModel</code> as a subclass of
 * <code>AbstractFormModel</code> you need only provide implementations for the
 * following three methods:
 *
 * <pre>
 *
 *
 * public int getFieldCount ();
 *
 * public Object getValueAt ( int field );
 * </pre>
 *
 * <p>
 *
 */
public abstract class AbstractFormModel
    implements FormModel
{

  //
  // Instance Variables
  //

  /** List of listeners */
  protected EventListenerList listenerList = new EventListenerList();

  //
  // Default Implementation of the Interface
  //

  /**
   * Returns a default name for the field using spreadsheet conventions: A, B,
   * C, ... Z, AA, AB, etc. If <code>field</code> cannot be found, returns an
   * empty string.
   *
   * @param field
   *          the field being queried
   * @return a string containing the default name of <code>field</code>
   */
  @Override
  public String getFieldName ( int field )
  {
    String result = "";
    for ( ; field >= 0; field = field / 26 - 1 )
    {
      result = (char)((char)(field % 26) + 'A') + result;
    }
    return result;
  }

  /**
   * Returns a field given its name. Implementation is naive so this should be
   * overridden if this method is to be called often. This method is not in the
   * <code>FormModel</code> interface and is not used by the <code>JForm</code>.
   *
   * @param fieldName
   *          string containing name of field to be located
   * @return the field with <code>fieldName</code>, or -1 if not found
   */
  public int findField ( final String fieldName )
  {
    for ( int i = 0; i < getFieldCount(); i++ )
    {
      if ( fieldName.equals(getFieldName(i)) )
      {
        return i;
      }
    }
    return -1;
  }

  /**
   * Returns <code>Object.class</code> regardless of <code>fieldIndex</code>.
   *
   * @param fieldIndex
   *          the field being queried
   * @return the Object.class
   */
  @Override
  public Class<?> getFieldClass ( final int fieldIndex )
  {
    return Object.class;
  }

  /**
   * Returns false. This is the default implementation for all cells.
   *
   * @param rowIndex
   *          the row being queried
   * @param fieldIndex
   *          the field being queried
   * @return false
   */
  @Override
  public boolean isValueEditable ( final int fieldIndex )
  {
    return false;
  }

  /**
   * This empty implementation is provided so users don't have to implement this
   * method if their data model is not editable.
   *
   * @param aValue
   *          value to assign
   * @param fieldIndex
   *          field
   */
  @Override
  public void setValueAt ( final Object aValue, final int fieldIndex )
  {
  }


  //
  // Managing Listeners
  //

  /**
   * Adds a listener to the list that's notified each time a change to the data
   * model occurs.
   *
   * @param l
   *          the FormModelListener
   */
  @Override
  public void addFormModelListener ( final FormModelListener l )
  {
    listenerList.add(FormModelListener.class, l);
  }

  /**
   * Removes a listener from the list that's notified each time a change to the
   * data model occurs.
   *
   * @param l
   *          the FormModelListener
   */
  @Override
  public void removeFormModelListener ( final FormModelListener l )
  {
    listenerList.remove(FormModelListener.class, l);
  }

  /**
   * Returns an array of all the form model listeners registered on this model.
   *
   * @return all of this model's <code>FormModelListener</code>s or an empty
   *         array if no form model listeners are currently registered
   *
   * @see #addFormModelListener
   * @see #removeFormModelListener
   *
   * @since 1.4
   */
  public FormModelListener[] getFormModelListeners ()
  {
    return listenerList.getListeners(FormModelListener.class);
  }

  //
  // Fire methods
  //

  /**
   * Notifies all listeners that all cell values in the form's rows may have
   * changed. The number of rows may also have changed and the
   * <code>JForm</code> should redraw the form from scratch. The structure of
   * the form (as in the order of the fields) is assumed to be the same.
   *
   * @see FormModelEvent
   * @see EventListenerList
   * @see javax.swing.JForm#formChanged(FormModelEvent)
   */
  public void fireFormDataChanged ()
  {
    fireFormChanged(new FormModelEvent(this, false));
  }

  /**
   * Notifies all listeners that the form's structure has changed. The number of
   * fields in the form, and the names and types of the new fields may be
   * different from the previous state. If the <code>JForm</code> receives this
   * event and its <code>autoCreateFieldsFromModel</code> flag is set it
   * discards any form fields that it had and reallocates default fields in the
   * order they appear in the model. This is the same as calling
   * <code>setModel(FormModel)</code> on the <code>JForm</code>.
   *
   * @see FormModelEvent
   * @see EventListenerList
   */
  public void fireFormStructureChanged ()
  {
    fireFormChanged(new FormModelEvent(this, true));
  }

  /**
   * Notifies all listeners that the value of the cell at
   * <code>[row, field]</code> has been updated.
   *
   * @param row
   *          row of cell which has been updated
   * @param field
   *          field of cell which has been updated
   * @see FormModelEvent
   * @see EventListenerList
   */
  public void fireFormValueUpdated ( final int field )
  {
    fireFormChanged(new FormModelEvent(this, field));
  }

  /**
   * Forwards the given notification event to all
   * <code>FormModelListeners</code> that registered themselves as listeners for
   * this form model.
   *
   * @param e
   *          the event to be forwarded
   *
   * @see #addFormModelListener
   * @see FormModelEvent
   * @see EventListenerList
   */
  public void fireFormChanged ( final FormModelEvent e )
  {
    // Guaranteed to return a non-null array
    final Object[] listeners = listenerList.getListenerList();
    // Process the listeners last to first, notifying
    // those that are interested in this event
    for ( int i = listeners.length - 2; i >= 0; i -= 2 )
    {
      if ( listeners[i] == FormModelListener.class )
      {
        ((FormModelListener)listeners[i + 1]).formChanged(e);
      }
    }
  }

  /**
   * Returns an array of all the objects currently registered as
   * <code><em>Foo</em>Listener</code>s upon this <code>AbstractFormModel</code>
   * . <code><em>Foo</em>Listener</code>s are registered using the
   * <code>add<em>Foo</em>Listener</code> method.
   *
   * <p>
   *
   * You can specify the <code>listenerType</code> argument with a class
   * literal, such as <code><em>Foo</em>Listener.class</code>. For example, you
   * can query a model <code>m</code> for its form model listeners with the
   * following code:
   *
   * <pre>
   *
   *
   * FormModelListener[] tmls = (FormModelListener[])(m.getListeners(FormModelListener.class));
   * </pre>
   *
   * If no such listeners exist, this method returns an empty array.
   *
   * @param listenerType
   *          the type of listeners requested; this parameter should specify an
   *          interface that descends from <code>java.util.EventListener</code>
   * @return an array of all objects registered as
   *         <code><em>Foo</em>Listener</code>s on this component, or an empty
   *         array if no such listeners have been added
   * @exception ClassCastException
   *              if <code>listenerType</code> doesn't specify a class or
   *              interface that implements <code>java.util.EventListener</code>
   *
   * @see #getFormModelListeners
   *
   * @since 1.3
   */
  public <T extends EventListener> T[] getListeners ( final Class<T> listenerType )
  {
    return listenerList.getListeners(listenerType);
  }

}
