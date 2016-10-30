// 
// Filename : ObjectServiceList.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.JMenuItem;

import org.xtuml.masl.inspector.processInterface.Capability;
import org.xtuml.masl.inspector.processInterface.ObjectMetaData;
import org.xtuml.masl.inspector.processInterface.ObjectServiceMetaData;


public class ObjectServiceList extends ExecutableSourceList<ObjectServiceMetaData, ObjectMetaData>
{

  public ObjectServiceList ( final ObjectList objectList )
  {
    super(new DependentObjectListModel<ObjectServiceMetaData, ObjectMetaData>(objectList)
    {

      @Override
      protected ObjectServiceMetaData[] getDependentValues ( final ObjectMetaData object )
      {
        final ObjectServiceMetaData[] services = object.getObjectServices();
        Arrays.sort(services);
        return services;
      }
    });
  }

  @Override
  protected void initPopup ()
  {
    if ( Capability.RUN_OBJECT_SERVICE.isAvailable() )
    {
      final JMenuItem runServiceItem = new JMenuItem("Run service...");
      runServiceItem.addActionListener(new ActionListener()
        {

          public void actionPerformed ( final ActionEvent action )
          {
            new InvokeObjectServiceDialog((ObjectServiceMetaData)getSelectedValue());
          }
        });
      popup.add(runServiceItem);
    }

    super.initPopup();
  }

  protected ObjectServiceMetaData[] getDependentValueList ( final ObjectMetaData object )
  {
    final ObjectServiceMetaData[] services = object.getObjectServices();
    Arrays.sort(services);
    return services;
  }
}
