// 
// Filename : DomainScenarioList.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.JMenuItem;

import org.xtuml.masl.inspector.processInterface.Capability;
import org.xtuml.masl.inspector.processInterface.DomainMetaData;
import org.xtuml.masl.inspector.processInterface.DomainServiceMetaData;
import org.xtuml.masl.inspector.processInterface.ProcessConnection;


public class DomainScenarioList extends ExecutableSourceList<DomainServiceMetaData, DomainMetaData>
{

  public DomainScenarioList ( final DomainPicker domainPicker )
  {
    super(new DependentObjectListModel<DomainServiceMetaData, DomainMetaData>(domainPicker)
    {

      @Override
      protected DomainServiceMetaData[] getDependentValues ( final DomainMetaData domain )
      {
        final DomainServiceMetaData[] scenarios = domain.getScenarios();
        Arrays.sort(scenarios);
        return scenarios;
      }
    });
  }


  @Override
  protected void initPopup ()
  {
    if ( Capability.RUN_SCENARIO.isAvailable() )
    {
      final JMenuItem runScenarioItem = new JMenuItem("Run Scenario...");
      runScenarioItem.addActionListener(new ActionListener()
        {

          public void actionPerformed ( final ActionEvent action )
          {
            try
            {
              ProcessConnection.getConnection().runScenario((DomainServiceMetaData)getSelectedValue());
            }
            catch ( final java.rmi.RemoteException e )
            {
              e.printStackTrace();
            }
          }
        });

      popup.add(runScenarioItem);
    }

    super.initPopup();
  }

}
