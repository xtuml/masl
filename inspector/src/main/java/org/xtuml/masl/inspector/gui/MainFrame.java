// 
// Filename : MainFrame.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;

import org.xml.sax.SAXParseException;
import org.xtuml.masl.inspector.DomainExport;
import org.xtuml.masl.inspector.DomainImport;
import org.xtuml.masl.inspector.DomainSchema;
import org.xtuml.masl.inspector.processInterface.DomainMetaData;
import org.xtuml.masl.inspector.processInterface.Plugin;
import org.xtuml.masl.inspector.processInterface.ProcessConnection;
import org.xtuml.masl.inspector.processInterface.ProcessMetaData;


public class MainFrame extends InspectorFrame
{

  private static String          defaultDir = System.getProperty("user.dir");
  private static ProcessMetaData procMeta;

  public MainFrame ()
  {
    super("Inspector");

    try
    {
      setTitle(ProcessConnection.getConnection().getConnectionTitle());
      procMeta = ProcessConnection.getConnection().getMetaData();
    }
    catch ( final java.rmi.RemoteException e )
    {
      e.printStackTrace();
    }

    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    final JToolBar toolbar = new JToolBar();
    final JPanel toolbarPanel = new JPanel(new ToolbarLayout());
    toolbar.add(toolbarPanel);

    toolbarPanel.add(new JLabel("Domain: "));
    final DomainPicker domainPicker = new DomainPicker();
    toolbarPanel.add(domainPicker);

    final String curDomain = ((DomainMetaData)domainPicker.getSelectedItem()).getName();


    toolbarPanel.add(ProcessConnectionGUI.getPauseComponent());
    toolbarPanel.add(ProcessConnectionGUI.getStepComponent());
    toolbarPanel.add(ProcessConnectionGUI.getSloMoComponent());
    toolbarPanel.add(ProcessConnectionGUI.getContinueComponent());


    final JMenuItem setProcessDirectoryItem = new JMenuItem(new AbstractAction("Set Process Directory...")
      {

        public void actionPerformed ( final ActionEvent e )
        {
          new ProcessDirectoryChooser();
        }
      });

    final JMenuItem exitItem = new JMenuItem(new AbstractAction("Exit")
      {

        public void actionPerformed ( final ActionEvent e )
        {
          try
          {
            ProcessConnection.getConnection().closeConnection();
          }
          catch ( final java.rmi.RemoteException ex )
          {
            ex.printStackTrace();
          }
          System.exit(0);
        }
      });

    final JMenuItem setDomainDirectoryItem = new JMenuItem(new AbstractAction("Set " + curDomain + " Directory...")
      {

        public void actionPerformed ( final ActionEvent e )
        {
          new DomainDirectoryChooser((DomainMetaData)domainPicker.getSelectedItem());
        }
      });

    final JMenuItem importDomainItem = new JMenuItem(new AbstractAction("Import " + curDomain + " Data")
      {

        public void actionPerformed ( final ActionEvent e )
        {
          final JFileChooser chooser = new JFileChooser(defaultDir);
          chooser.setFileFilter(new ExtensionFileFilter("xml", "XML file"));
          chooser.setMultiSelectionEnabled(false);
          if ( chooser.showOpenDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION )
          {
            defaultDir = chooser.getSelectedFile().getPath();
            try
            {
              new DomainImport(chooser.getSelectedFile(), (DomainMetaData)domainPicker.getSelectedItem());
            }
            catch ( final SAXParseException spe )
            {
              JOptionPane.showMessageDialog(MainFrame.this, "Parse error in " + spe.getSystemId()
                                                            + " line "
                                                            + spe.getLineNumber()
                                                            + "\n"
                                                            + spe.getMessage(), "XML Parse Error", JOptionPane.ERROR_MESSAGE);
            }
          }
        }
      });

    final JMenuItem exportDomainItem = new JMenuItem(new AbstractAction("Export " + curDomain + " Data")
      {

        public void actionPerformed ( final ActionEvent e )
        {
          final String fileName = procMeta.getName() + "_" + ((DomainMetaData)domainPicker.getSelectedItem()).getName() + ".xml";
          new DomainExport((DomainMetaData)domainPicker.getSelectedItem(), new File(fileName));
        }
      });

    final JMenuItem createDomainSchemaItem = new JMenuItem(new AbstractAction("Create " + curDomain + " Schema")
      {

        public void actionPerformed ( final ActionEvent e )
        {
          final DomainMetaData domainMeta = (DomainMetaData)domainPicker.getSelectedItem();
          new DomainSchema(domainMeta, true, true).writeToFile(new File(domainMeta.getName() + "_full.xsd"));
          new DomainSchema(domainMeta, false, false).writeToFile(new File(domainMeta.getName() + "_quick.xsd"));
          new DomainSchema(domainMeta, true, false).writeToFile(new File(domainMeta.getName() + "_ref_integ.xsd"));
          new DomainSchema(domainMeta, false, true).writeToFile(new File(domainMeta.getName() + "_unique_id.xsd"));
        }
      });

    final JMenuBar mainMenu = new JMenuBar();
    setJMenuBar(mainMenu);

    final JMenuItem fileMenu = new JMenu("File");

    fileMenu.add(setProcessDirectoryItem);
    fileMenu.add(ProcessConnectionGUI.getRunScheduleMenuItem());
    fileMenu.add(exitItem);
    mainMenu.add(fileMenu);

    final JMenuItem domainMenu = new JMenu(curDomain);
    domainMenu.add(setDomainDirectoryItem);
    domainMenu.add(exportDomainItem);
    domainMenu.add(importDomainItem);
    domainMenu.add(createDomainSchemaItem);
    mainMenu.add(domainMenu);

    mainMenu.add(getTraceMenu());
    mainMenu.add(getStepMenu());
    mainMenu.add(getMonitorMenu());
    mainMenu.add(getWindowMenu());

    for ( final Plugin plugin : procMeta.getPlugins() )
    {
      mainMenu.add(getPluginMenu(plugin));
    }


    domainPicker.addItemListener(new ItemListener()
      {

        public void itemStateChanged ( final ItemEvent event )
        {
          if ( event.getStateChange() == ItemEvent.SELECTED )
          {
            final String name = ((DomainMetaData)domainPicker.getSelectedItem()).getName();
            domainMenu.setText(name);
            setDomainDirectoryItem.setText("Set " + name + " Directory...");
            importDomainItem.setText("Import " + name + " Data");
            exportDomainItem.setText("Export " + name + " Data");
            createDomainSchemaItem.setText("Create " + name + " Schema");
          }
        }
      });


    final DomainDetailsPane domainDetailsPane = new DomainDetailsPane(domainPicker);

    final JPanel statusBar = new JPanel();
    final GridBagLayout statusBarLayout = new GridBagLayout();
    statusBar.setLayout(statusBarLayout);
    final ProcessStatusLabel psLabel = new ProcessStatusLabel();
    final BacklogLabel backlogLabel = new BacklogLabel();
    statusBar.add(psLabel);
    statusBar.add(backlogLabel);
    final GridBagConstraints psConstraints = new GridBagConstraints();
    final GridBagConstraints backlogConstraints = new GridBagConstraints();
    psConstraints.anchor = GridBagConstraints.WEST;
    psConstraints.weightx = 1;
    backlogConstraints.anchor = GridBagConstraints.EAST;
    statusBarLayout.setConstraints(psLabel, psConstraints);
    statusBarLayout.setConstraints(backlogLabel, backlogConstraints);


    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(statusBar, BorderLayout.SOUTH);
    getContentPane().add(toolbar, BorderLayout.NORTH);
    getContentPane().add(domainDetailsPane, BorderLayout.CENTER);
    pack();
    setVisible(true);

    // Force initialisation
    SourceCodeBrowserController.getInstance();

  }

  private JMenu getPluginMenu ( final Plugin plugin )
  {
    final JMenu menu = new JMenu(plugin.getName());
    for ( final String actionName : plugin.getActions() )
    {
      menu.add(ProcessConnectionGUI.getPluginActionMenuItem(plugin.getName(), actionName));
    }

    for ( final Plugin.Flag flag : plugin.getFlags() )
    {
      menu.add(ProcessConnectionGUI.getPluginFlagMenuItem(plugin.getName(), flag));
    }

    for ( final Plugin.Property property : plugin.getProperties() )
    {
      ProcessConnectionGUI.addPluginPropertyMenuItem(menu, plugin.getName(), property);
    }

    return menu;
  }

  private JMenu getTraceMenu ()
  {
    final JMenu menu = new JMenu("Trace");

    menu.add(ProcessConnectionGUI.getTraceLinesMenuItem());
    menu.add(ProcessConnectionGUI.getTraceBlocksMenuItem());
    menu.add(ProcessConnectionGUI.getTraceExceptionsMenuItem());
    menu.add(ProcessConnectionGUI.getTraceEventsMenuItem());
    return menu;
  }

  private JMenu getStepMenu ()
  {
    final JMenu menu = new JMenu("Breakpoints");

    menu.add(ProcessConnectionGUI.getShowBreakpointsMenuItem());
    menu.add(ProcessConnectionGUI.getStepLinesMenuItem());
    menu.add(ProcessConnectionGUI.getStepBlocksMenuItem());
    menu.add(ProcessConnectionGUI.getStepExceptionsMenuItem());
    menu.add(ProcessConnectionGUI.getStepEventsMenuItem());

    return menu;
  }

  private JMenu getMonitorMenu ()
  {
    final JMenu menu = new JMenu("Monitor");
    menu.add(ProcessConnectionGUI.getShowStackMenuItem());
    menu.add(ProcessConnectionGUI.getCatchConsoleMenuItem());
    menu.add(ProcessConnectionGUI.getEnableTimersMenuItem());

    return menu;
  }

  private JMenu getWindowMenu ()
  {
    return WindowMenu.getInstance();
  }

  public static void main ( final String[] args )
  {
    GUI.installUI();

    new MainFrame();
  }
}
