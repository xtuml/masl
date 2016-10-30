// 
// Filename : DependentObjectList.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui;

import java.awt.ItemSelectable;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.AbstractListModel;


public abstract class DependentObjectListModel<Dependent, Discriminant> extends AbstractListModel
{

  private final ItemSelectable selector;

  public DependentObjectListModel ( final ItemSelectable selector )
  {
    super();
    this.selector = selector;

    updateList();

    selector.addItemListener(new ItemListener()
      {

        public void itemStateChanged ( final ItemEvent event )
        {
          if ( event.getStateChange() == ItemEvent.SELECTED )
          {
            updateList();
          }
          else if ( event.getStateChange() == ItemEvent.DESELECTED )
          {
            clearList();
          }
        }
      });
  }

  private void clearList ()
  {
    fireIntervalRemoved(this, 0, data.length);
    data = null;
  }

  private Dependent[] data;

  @SuppressWarnings("unchecked")
  public Discriminant getDiscriminant ()
  {
    final Object[] selectedObjects = selector.getSelectedObjects();
    if ( selectedObjects != null && selectedObjects.length > 0 && selectedObjects[0] != null )
    {
      return (Discriminant)selectedObjects[0];
    }
    else
    {
      return null;
    }

  }

  private void updateList ()
  {
    final Discriminant discriminant = getDiscriminant();
    if ( discriminant != null )
    {
      data = getDependentValues(discriminant);
      fireContentsChanged(this, 0, Integer.MAX_VALUE);
    }
    else
    {
      if ( data != null )
      {
        clearList();
      }
    }
  }


  protected abstract Dependent[] getDependentValues ( Discriminant dependee );

  public Object getElementAt ( final int index )
  {
    return data == null ? null : data[index];
  }

  public int getSize ()
  {
    return data == null ? 0 : data.length;
  }

}
