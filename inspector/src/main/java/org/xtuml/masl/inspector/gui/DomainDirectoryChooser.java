// 
// Filename : DomainDirectoryChooser.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui;

import javax.swing.JFileChooser;

import org.xtuml.masl.inspector.processInterface.DomainMetaData;


public class DomainDirectoryChooser extends JFileChooser
{

  public DomainDirectoryChooser ( final DomainMetaData domain )
  {
    setDialogTitle("Domain " + domain.getName() + " Source Directory");
    setCurrentDirectory(domain.getDirectory());
    setApproveButtonText("OK");
    setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

    if ( showDialog(null, null) != JFileChooser.APPROVE_OPTION )
    {
      return;
    }

    domain.setDirectory(getSelectedFile());
  }
}
