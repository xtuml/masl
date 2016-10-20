// 
// Filename : ObjectButton.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui.modelView;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import org.xtuml.masl.inspector.Preferences;
import org.xtuml.masl.inspector.processInterface.AttributeMetaData;
import org.xtuml.masl.inspector.processInterface.ObjectMetaData;
import org.xtuml.masl.inspector.processInterface.ObjectServiceMetaData;


public class ObjectButton extends JButton
{

  private static final Color   background = Color.white;
  private static final Color   foreground = Color.black;

  private final ObjectMetaData metaObj;

  public ObjectButton ( final ObjectMetaData metaObj )
  {
    super();

    this.metaObj = metaObj;

    setBackground(background);
    setBorder(BorderFactory.createLineBorder(foreground));

    if ( Preferences.getModellingMode() == Preferences.ModellingMode.SM )
    {

      final GridBagLayout layout = new GridBagLayout();
      setLayout(layout);

      final GridBagConstraints titleConstraints = new GridBagConstraints();
      titleConstraints.anchor = GridBagConstraints.NORTH;
      titleConstraints.gridwidth = GridBagConstraints.REMAINDER;
      titleConstraints.weightx = 1;
      titleConstraints.weighty = 0;
      titleConstraints.insets = new Insets(5, 5, 5, 5);

      final GridBagConstraints idConstraints = new GridBagConstraints();
      idConstraints.anchor = GridBagConstraints.NORTHWEST;
      idConstraints.gridwidth = GridBagConstraints.RELATIVE;
      idConstraints.weightx = 0;
      idConstraints.weighty = 0;
      idConstraints.insets = new Insets(0, 5, 0, 5);

      final GridBagConstraints nameConstraints = new GridBagConstraints();
      nameConstraints.weightx = 1;
      nameConstraints.weighty = 0;
      nameConstraints.fill = GridBagConstraints.HORIZONTAL;
      nameConstraints.anchor = GridBagConstraints.NORTHWEST;
      nameConstraints.gridwidth = GridBagConstraints.REMAINDER;
      nameConstraints.insets = new Insets(0, 0, 0, 5);

      final JLabel objLabel = new JLabel(metaObj.getName());
      objLabel.setForeground(foreground);
      objLabel.setFont(getFont().deriveFont(Font.BOLD));
      layout.setConstraints(objLabel, titleConstraints);
      add(objLabel);

      for ( int a = 0; a < metaObj.getAttributes().length; a++ )
      {
        if ( a == metaObj.getAttributes().length - 1 )
        {
          nameConstraints.weighty = 1;
          nameConstraints.insets = new Insets(0, 0, 5, 5);
          idConstraints.insets = new Insets(0, 5, 5, 5);
        }

        final AttributeMetaData att = metaObj.getAttributes()[a];
        final JLabel idLabel = new JLabel(att.isIdentifier() ? "*" : ".");
        idLabel.setForeground(foreground);
        idLabel.setFont(getFont().deriveFont(Font.BOLD));
        layout.setConstraints(idLabel, idConstraints);
        add(idLabel);
        String name;
        if ( att.isReferential() )
        {
          name = att.getName() + " (" + att.getRelationshipText() + ")";
        }
        else
        {
          name = att.getName();
        }
        final JLabel nameLabel = new JLabel(name);
        nameLabel.setForeground(foreground);
        layout.setConstraints(nameLabel, nameConstraints);
        add(nameLabel);
      }
    }
    else
    {
      final GridBagLayout layout = new GridBagLayout();
      setLayout(layout);

      final GridBagConstraints titleConstraints = new GridBagConstraints();
      titleConstraints.anchor = GridBagConstraints.NORTH;
      titleConstraints.gridwidth = GridBagConstraints.REMAINDER;
      titleConstraints.weightx = 1;
      titleConstraints.weighty = 0;
      titleConstraints.insets = new Insets(5, 5, 0, 5);

      final GridBagConstraints lineConstraints = new GridBagConstraints();
      lineConstraints.anchor = GridBagConstraints.NORTH;
      lineConstraints.gridwidth = GridBagConstraints.REMAINDER;
      lineConstraints.fill = GridBagConstraints.HORIZONTAL;
      lineConstraints.weightx = 1;
      lineConstraints.weighty = 0;
      lineConstraints.insets = new Insets(2, 0, 2, 0);

      final GridBagConstraints nameConstraints = new GridBagConstraints();
      nameConstraints.weightx = 1;
      nameConstraints.weighty = 0;
      nameConstraints.fill = GridBagConstraints.HORIZONTAL;
      nameConstraints.anchor = GridBagConstraints.NORTHWEST;
      nameConstraints.gridwidth = GridBagConstraints.REMAINDER;
      nameConstraints.insets = new Insets(0, 5, 0, 5);

      final JLabel objLabel = new JLabel(metaObj.getName());
      objLabel.setForeground(foreground);
      objLabel.setFont(getFont().deriveFont(Font.BOLD));
      layout.setConstraints(objLabel, titleConstraints);
      add(objLabel);

      final JSeparator line1 = new JSeparator(SwingConstants.HORIZONTAL);
      line1.setForeground(foreground);
      layout.setConstraints(line1, lineConstraints);
      add(line1);

      for ( int a = 0; a < metaObj.getAttributes().length; a++ )
      {
        if ( a == metaObj.getAttributes().length - 1 )
        {
          nameConstraints.weighty = 1;
        }
        final AttributeMetaData att = metaObj.getAttributes()[a];
        final String idText = att.isIdentifier() ? "I=(*1)" : "";
        String tag;
        if ( !att.isReferential() )
        {
          tag = att.isIdentifier() ? " {" + idText + "}" : "";
        }
        else
        {
          tag = " {" + (att.isIdentifier() ? idText + "," : "") + "R=(" + att.getRelationshipText() + ")}";
        }
        final JLabel nameLabel = new JLabel(" " + att.getName() + " : " + att.getTypeName() + " " + tag + " ");
        nameLabel.setForeground(foreground);
        layout.setConstraints(nameLabel, nameConstraints);
        add(nameLabel);
      }

      nameConstraints.weighty = 0;

      final JSeparator line2 = new JSeparator(SwingConstants.HORIZONTAL);
      line2.setForeground(foreground);
      layout.setConstraints(line2, lineConstraints);
      add(line2);

      for ( int s = 0; s < metaObj.getInstanceServices().length; s++ )
      {
        if ( s == metaObj.getInstanceServices().length - 1 )
        {
          nameConstraints.weighty = 1;
          nameConstraints.insets = new Insets(0, 5, 5, 5);
        }
        final ObjectServiceMetaData svc = metaObj.getInstanceServices()[s];
        final JLabel nameLabel = new JLabel(" " + svc + " ");
        nameLabel.setForeground(foreground);
        layout.setConstraints(nameLabel, nameConstraints);
        add(nameLabel);
      }
    }
  }

  @Override
  public Dimension getMaximumSize ()
  {
    return new Dimension(getPreferredSize().width, super.getMaximumSize().height);
  }

  public ObjectMetaData getMetaObject ()
  {
    return metaObj;
  }

}
