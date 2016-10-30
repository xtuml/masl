//
// File: InvokeDialog.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.inspector.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import org.xtuml.masl.inspector.gui.form.Form;
import org.xtuml.masl.inspector.processInterface.DataValue;
import org.xtuml.masl.inspector.processInterface.ObjectMetaData;
import org.xtuml.masl.inspector.processInterface.ParameterMetaData;


public abstract class InvokeDialog extends InspectorSubFrame
{

  private Form                keyForm;
  private final ParameterForm paramForm;


  public InvokeDialog ( final String title, final String invokeLabel, final ParameterMetaData[] parameters )
  {
    this(title, invokeLabel, null, null, parameters);
  }

  public InvokeDialog ( final String title,
                        final String invokeLabel,
                        final ObjectMetaData object,
                        final Object pk,
                        final ParameterMetaData[] parameters )
  {
    super(invokeLabel + " " + title);
    paramForm = new ParameterForm(parameters);

    setLayout(new BorderLayout());

    final JPanel buttons = new JPanel();

    final JButton invokeButton = new JButton(invokeLabel);
    final JCheckBox keepButton = new JCheckBox("keep");
    final JButton cancelButton = new JButton("Cancel");

    if ( pk != null )
    {
      try
      {
        keyForm = new Form(new ObjectKeyModel(object, pk));
        getContentPane().add(keyForm, BorderLayout.NORTH);
      }
      catch ( final IllegalStateException e )
      {
        final JLabel label = new JLabel("Instance Deleted");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBorder(new EmptyBorder(10, 0, 10, 0));
        getContentPane().add(label, BorderLayout.NORTH);
        invokeButton.setVisible(false);
        paramForm.setVisible(false);
      }
    }
    getContentPane().add(new JScrollPane(paramForm,
                                         ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                                         ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED),
                         BorderLayout.CENTER);
    getContentPane().add(buttons, BorderLayout.SOUTH);

    buttons.add(invokeButton);
    buttons.add(keepButton);
    buttons.add(cancelButton);
    invokeButton.addActionListener(new ActionListener()
    {

      public void actionPerformed ( final ActionEvent e )
      {
        if ( invokeButton.isFocusOwner() )
        {
          invokeClicked();
          if ( !keepButton.isSelected() )
          {
            dispose();
          }
        }
      }
    });


    cancelButton.addActionListener(new ActionListener()
    {

      public void actionPerformed ( final ActionEvent e )
      {
        dispose();
      }
    });

    setResizable(true);
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    pack();
  }

  protected void display ()
  {
    setVisible(true);
    addToWindowMenu();
  }


  abstract protected void invoke ( DataValue<?>[] parameters );

  private void invokeClicked ()
  {
    if ( paramForm.getValues() != null )
    {
      invoke(paramForm.getValues());
    }

  }

}
