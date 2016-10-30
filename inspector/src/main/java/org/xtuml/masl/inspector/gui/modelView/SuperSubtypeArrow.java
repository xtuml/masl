// 
// Filename : SuperSubtypeArrow.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui.modelView;

import javax.swing.JComponent;
import javax.swing.UIManager;

import org.xtuml.masl.inspector.processInterface.ObjectMetaData;
import org.xtuml.masl.inspector.processInterface.SuperSubtypeMetaData;


class SuperSubtypeArrow extends JComponent
{

  private final String uiClassID;

  @Override
  public String getUIClassID ()
  {
    return uiClassID;
  }

  @Override
  public void updateUI ()
  {
    setUI(UIManager.getUI(this));
  }

  private final ObjectMetaData       source;
  private final SuperSubtypeMetaData ss;
  private ObjectButton               superObjectButton = null;
  private ObjectButton[]             subObjectButtons;
  private ObjectButton[]             objectButtons;

  public SuperSubtypeArrow ( final ObjectMetaData source, final SuperSubtypeMetaData ss )
  {
    this.ss = ss;
    this.source = source;
    uiClassID = "SuperSubtypeUI";

    if ( source.equals(ss.getSupertype()) )
    {
      // Sup -+------+
      // |
      // +----+----+
      // | | |
      // Sub1 Sub2 Sub3
      objectButtons = new ObjectButton[ss.getSubtypes().length];
      subObjectButtons = new ObjectButton[ss.getSubtypes().length];
      for ( int s = 0; s < ss.getSubtypes().length; s++ )
      {
        final ObjectMetaData meta = ss.getSubtypes()[s];
        final ObjectButton button = new ObjectButton(meta);
        add(button);
        subObjectButtons[s] = button;
        objectButtons[s] = button;
      }
    }
    else
    {
      // Sup
      // |
      // +
      // |
      // Sub1---+----+
      // | |
      // Sub2 Sub3

      objectButtons = new ObjectButton[ss.getSubtypes().length];
      subObjectButtons = new ObjectButton[ss.getSubtypes().length - 1];
      final ObjectMetaData superMeta = ss.getSupertype();
      superObjectButton = new ObjectButton(superMeta);
      objectButtons[0] = superObjectButton;
      add(superObjectButton);
      int i = 0;
      for ( int s = 0; s < ss.getSubtypes().length; s++ )
      {
        if ( !ss.getSubtypes()[s].equals(source) )
        {
          final ObjectMetaData meta = ss.getSubtypes()[s];
          final ObjectButton button = new ObjectButton(meta);
          add(button);
          subObjectButtons[i++] = button;
          objectButtons[i] = button;
        }
      }
    }
    setLayout(new PreferredSizeLayout());
    updateUI();
  }

  public SuperSubtypeMetaData getSuperSubtype ()
  {
    return ss;
  }

  public ObjectMetaData getSource ()
  {
    return source;
  }

  public ObjectButton getSuperObjectButton ()
  {
    return superObjectButton;
  }

  public ObjectButton[] getSubObjectButtons ()
  {
    return subObjectButtons;
  }

  public ObjectButton[] getObjectButtons ()
  {
    return objectButtons;
  }
}
