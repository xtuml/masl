// 
// Filename : ExecutableSourceList.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.xtuml.masl.inspector.BreakpointController;
import org.xtuml.masl.inspector.processInterface.Capability;
import org.xtuml.masl.inspector.processInterface.ExecutableSource;
import org.xtuml.masl.inspector.processInterface.SourcePosition;


public abstract class ExecutableSourceList<Dependent extends ExecutableSource, Discriminant> extends
    DependentObjectList<Dependent, Discriminant>
    implements PopupMenuListener
{

  protected JPopupMenu popup = new JPopupMenu();
  private JMenuItem    setBreakpointItem;
  private JMenuItem    clearBreakpointItem;
  private JMenuItem    removeBreakpointItem;


  public ExecutableSourceList ( final DependentObjectListModel<Dependent, Discriminant> model )
  {
    super(model);
    initPopup();
  }

  protected void initPopup ()
  {
    final JMenuItem openSourceItem = new JMenuItem("View Source");
    openSourceItem.addActionListener(new ActionListener()
    {

      public void actionPerformed ( final ActionEvent action )
      {
        SourceCodeBrowserController.getInstance().display((ExecutableSource)getSelectedValue());
      }
    });
    popup.add(openSourceItem);


    setBreakpointItem = new JMenuItem("Set Breakpoint");
    setBreakpointItem.addActionListener(new ActionListener()
    {

      public void actionPerformed ( final ActionEvent action )
      {
        BreakpointController.getInstance()
                            .setBreakpoint(((ExecutableSource)getSelectedValue())
                                                                                 .getSourcePosition(SourcePosition.FIRST_LINE));
      }
    });


    clearBreakpointItem = new JMenuItem("Clear Breakpoint");
    clearBreakpointItem.addActionListener(new ActionListener()
    {

      public void actionPerformed ( final ActionEvent action )
      {
        BreakpointController.getInstance()
                            .clearBreakpoint(((ExecutableSource)getSelectedValue())
                                                                                   .getSourcePosition(SourcePosition.FIRST_LINE));
      }
    });


    removeBreakpointItem = new JMenuItem("Remove Breakpoint");
    removeBreakpointItem.addActionListener(new ActionListener()
    {

      public void actionPerformed ( final ActionEvent action )
      {
        BreakpointController.getInstance()
                            .removeBreakpoint(((ExecutableSource)getSelectedValue())
                                                                                    .getSourcePosition(SourcePosition.FIRST_LINE));
      }
    });

    if ( Capability.SET_BREAKPOINT.isAvailable() )
    {
      popup.add(setBreakpointItem);
      popup.add(clearBreakpointItem);
      popup.add(removeBreakpointItem);
    }

    popup.addPopupMenuListener(this);
    addMouseListener(new ListPopupHandler(popup));

  }

  public void popupMenuCanceled ( final PopupMenuEvent e )
  {
  }

  public void popupMenuWillBecomeInvisible ( final PopupMenuEvent e )
  {
  }

  public void popupMenuWillBecomeVisible ( final PopupMenuEvent e )
  {
    final boolean breakpointSet = BreakpointController.getInstance()
                                                      .breakpointSet(((ExecutableSource)getSelectedValue())
                                                                                                           .getSourcePosition(SourcePosition.FIRST_LINE));
    final boolean breakpointExists = BreakpointController.getInstance()
                                                         .breakpointExists(((ExecutableSource)getSelectedValue())
                                                                                                                 .getSourcePosition(SourcePosition.FIRST_LINE));
    setBreakpointItem.setEnabled(!breakpointSet);
    clearBreakpointItem.setEnabled(breakpointSet);
    removeBreakpointItem.setEnabled(breakpointExists);
  }

  @Override
  public Dimension getPreferredScrollableViewportSize ()
  {
    final Dimension d = super.getPreferredScrollableViewportSize();

    d.width = getCellRenderer().getListCellRendererComponent(this,
                                                             "WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW",
                                                             0,
                                                             true,
                                                             true).getPreferredSize().width;

    return d;
  }


}
