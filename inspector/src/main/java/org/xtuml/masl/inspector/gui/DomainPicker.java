// 
// Filename : DomainPicker.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui;

import javax.swing.JComboBox;

import org.xtuml.masl.inspector.processInterface.DomainMetaData;
import org.xtuml.masl.inspector.processInterface.ProcessConnection;


public class DomainPicker extends JComboBox
{

  public DomainPicker ()
  {
    super();
    try
    {
      final java.util.Set<String> ignoredDomains = org.xtuml.masl.inspector.Preferences.getIgnoredDomains();

      final DomainMetaData[] domains = ProcessConnection.getConnection().getMetaData().getDomains();
      java.util.Arrays.sort(domains);

      for ( int i = 0; i < domains.length; i++ )
      {
        if ( !domains[i].isInterface() && !ignoredDomains.contains(domains[i].getName()) )
        {
          addItem(domains[i]);
        }
      }
    }
    catch ( final java.rmi.RemoteException e )
    {
      e.printStackTrace();
    }

    setRequestFocusEnabled(false);
  }

}
