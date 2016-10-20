// 
// Filename : StackSource.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui;

import java.awt.ItemSelectable;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import org.xtuml.masl.inspector.processInterface.SourcePosition;
import org.xtuml.masl.inspector.processInterface.StackFrame;


public class StackSource extends SourceCodeTable
{

  private final ItemSelectable selector;

  public StackSource ( final ItemSelectable selector, final SourceCodeTableModel model )
  {
    super(model);
    this.selector = selector;

    selector.addItemListener(new ItemListener()
      {

        public void itemStateChanged ( final ItemEvent event )
        {
          if ( event.getStateChange() == ItemEvent.SELECTED )
          {
            updateSource();
          }
          else if ( event.getStateChange() == ItemEvent.DESELECTED )
          {
            clearSource();
          }
        }
      });

  }


  private void updateSource ()
  {
    final Object[] selectedObjects = selector.getSelectedObjects();
    if ( selectedObjects != null && selectedObjects.length > 0 && selectedObjects[0] != null )
    {
      final StackFrame selected = (StackFrame)selectedObjects[0];

      if ( selected != null )
      {
        model.setSource(selected.getPosition().getSource());
        model.setCurrentLine(selected.getPosition().getLineNo());
        scrollToCurrentLine();
      }
    }
  }


  private void clearSource ()
  {
    model.setSource(null);
    model.setCurrentLine(SourcePosition.NO_LINE);
  }

}
