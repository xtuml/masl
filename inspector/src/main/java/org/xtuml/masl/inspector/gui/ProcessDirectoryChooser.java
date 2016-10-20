// 
// Filename : ProcessDirectoryChooser.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui;

import javax.swing.JFileChooser;

import org.xtuml.masl.inspector.processInterface.ProcessConnection;
import org.xtuml.masl.inspector.processInterface.ProcessMetaData;


public class ProcessDirectoryChooser extends JFileChooser
{

  public ProcessDirectoryChooser ()
  {
    try
    {
      final ProcessMetaData meta = ProcessConnection.getConnection().getMetaData();

      setDialogTitle("Process " + meta.getName() + " Source Directory");
      setCurrentDirectory(meta.getDirectory());
      setApproveButtonText("OK");
      setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

      if ( showDialog(null, null) != JFileChooser.APPROVE_OPTION )
      {
        return;
      }

      meta.setDirectory(getSelectedFile());
    }
    catch ( final java.rmi.RemoteException e )
    {
      e.printStackTrace();
    }
  }
}
