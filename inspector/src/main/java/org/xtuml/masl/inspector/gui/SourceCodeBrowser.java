// 
// Filename : SourceCodeBrowser.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;

import org.xtuml.masl.inspector.Preferences;
import org.xtuml.masl.inspector.processInterface.ExecutableSource;
import org.xtuml.masl.inspector.processInterface.ProcessConnection;
import org.xtuml.masl.inspector.processInterface.ProcessStatusListener;
import org.xtuml.masl.inspector.processInterface.SourcePosition;
import org.xtuml.masl.inspector.processInterface.WeakProcessStatusListener;


class SourceCodeBrowser extends InspectorSubFrame
    implements ProcessStatusListener
{

  private static int                 s_defaultFontSize = Preferences.getCodeFontSize();
  private final SourceCodeTable      table;
  private final SourceCodeTableModel model;

  SourceCodeBrowser ( final ExecutableSource file, final int lineNo )
  {
    super(file.getFullyQualifiedName());

    model = new SourceCodeTableModel(file, lineNo);
    table = new SourceCodeTable(model);

    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    addWindowListener(new WindowAdapter()
    {

      @Override
      public void windowClosed ( final WindowEvent e )
      {
        SourceCodeBrowserController.getInstance().remove(file);
      }
    });

    ProcessConnection.getConnection().addProcessStatusListener(new WeakProcessStatusListener(this));

    setFont(s_defaultFontSize);

    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);

    final JToolBar toolbar = new JToolBar();
    final JPanel toolbarPanel = new JPanel(new ToolbarLayout());
    toolbar.add(toolbarPanel);

    toolbarPanel.add(ProcessConnectionGUI.getPauseComponent());
    toolbarPanel.add(ProcessConnectionGUI.getStepComponent());
    toolbarPanel.add(ProcessConnectionGUI.getSloMoComponent());
    toolbarPanel.add(ProcessConnectionGUI.getContinueComponent());
    getContentPane().add(toolbar, BorderLayout.NORTH);

    addMenus();
    pack();
    setSize(getPreferredSize());

    table.scrollToCurrentLine();

    setVisible(true);
    addToWindowMenu();
  }

  public void processStatusChanged ( final org.xtuml.masl.inspector.processInterface.ProcessStatusEvent e )
  {
    if ( e.getPosition() != null &&
         e.getPosition().getSource() == model.getSource() )
    {
      int line = e.getPosition().getLineNo();
      if ( line == SourcePosition.LAST_LINE )
      {
        line = model.getRowCount();
      }

      if ( line != SourcePosition.NO_LINE )
      {
        table.scrollToLine(line);
      }
      model.setCurrentLine(e.getPosition().getLineNo());
    }
    else
    {
      if ( e.getStatus() == ProcessConnection.RUNNING )
      {
        model.setStaleCurrentLine(true);
      }
      else
      {
        model.setCurrentLine(SourcePosition.NO_LINE);
      }
    }
  }


  public void setFont ( final int size )
  {
    final Font font = new Font(Preferences.getFontName(), Font.PLAIN, size);
    table.setFont(font);
    table.setColumnWidths();
    setSize(getPreferredSize());
    pack();
    s_defaultFontSize = size;
  }

  private void addMenus ()
  {
    final JMenuBar mainMenu = new JMenuBar();
    setJMenuBar(mainMenu);

    final JMenu fileMenu = new JMenu("File");
    mainMenu.add(fileMenu);

    final JMenuItem exitItem = new JMenuItem(new FrameCloseAction(this));
    fileMenu.add(exitItem);


    final JMenu viewMenu = new JMenu("View");
    mainMenu.add(viewMenu);

    final JMenuItem fontMenu = new JMenu("Font");
    viewMenu.add(fontMenu);

    final ButtonGroup group = new ButtonGroup();
    final FontChangeListener listener = new FontChangeListener();
    for ( int f = 6; f < 15; f++ )
    {
      final JRadioButtonMenuItem fontButton = new JRadioButtonMenuItem(f + "pt");

      group.add(fontButton);
      fontMenu.add(fontButton);
      fontButton.setActionCommand("" + f);
      fontButton.addActionListener(listener);
      if ( f == s_defaultFontSize )
      {
        fontButton.setSelected(true);
      }
    }
  }

  private class FontChangeListener
      implements ActionListener
  {

    public void actionPerformed ( final ActionEvent e )
    {
      setFont((new Integer(e.getActionCommand())).intValue());
    }
  }


}
