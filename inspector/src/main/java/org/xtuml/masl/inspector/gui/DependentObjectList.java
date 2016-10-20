// 
// Filename : DependentObjectList.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui;

import java.util.HashMap;
import java.util.Map;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


public abstract class DependentObjectList<Dependent, Discriminant> extends ComboList
{

  private final Map<Object, Object>                               previousSelections = new HashMap<Object, Object>();
  private final DependentObjectListModel<Dependent, Discriminant> model;

  public DependentObjectList ( final DependentObjectListModel<Dependent, Discriminant> model )
  {
    super(model);

    this.model = model;

    addListSelectionListener(new ListSelectionListener()
    {

      public void valueChanged ( final ListSelectionEvent e )
      {
        if ( !e.getValueIsAdjusting() && getSelectedValue() != null )
        {
          if ( model.getDiscriminant() != null )
          {
            previousSelections.put(model.getDiscriminant(), getSelectedValue());
          }
        }
      }
    });

    model.addListDataListener(new ListDataListener()
    {

      public void contentsChanged ( final ListDataEvent e )
      {
        makeInitialSelection();
      }

      public void intervalAdded ( final ListDataEvent e )
      {
        makeInitialSelection();
      }

      public void intervalRemoved ( final ListDataEvent e )
      {
        makeInitialSelection();
      }

    });
  }

  public void makeInitialSelection ()
  {
    if ( model.getSize() > 0 )
    {
      if ( model.getDiscriminant() != null )
      {
        setSelectedValue(previousSelections.get(model.getDiscriminant()), true);
      }
      else
      {
        setSelectedIndex(0);
        ensureIndexIsVisible(0);
      }
    }
  }

}
