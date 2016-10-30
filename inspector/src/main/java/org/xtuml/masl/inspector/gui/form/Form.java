//
// Filename : ParameterBox.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui.form;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.InputVerifier;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;


public class Form extends JPanel
    implements FormModelListener
{

  public static class BooleanEditor
      implements FormValueEditor
  {

    public BooleanEditor ( final FormModel model, final int fieldNo )
    {
      this.model = model;
      field = fieldNo;
      checkBox = new JCheckBox();
      checkBox.setHorizontalAlignment(SwingConstants.LEFT);
      checkBox.setSelected((Boolean)getModel().getValueAt(field));
      checkBox.setEnabled(getModel().isValueEditable(field));
      checkBox.setRequestFocusEnabled(false);
      checkBox.addActionListener((new ActionListener()
      {

        @Override
        public void actionPerformed ( final ActionEvent e )
        {
          getModel().setValueAt(checkBox.isSelected(), field);
        }
      }));
    }

    @Override
    public Component getFormValueEditorComponent ()
    {
      return checkBox;
    }

    protected FormModel getModel ()
    {
      return model;
    }

    private final JCheckBox checkBox;

    private final int       field;

    private final FormModel model;

  }

  public static class VoidEditor
      implements FormValueEditor
  {

    public VoidEditor ( final FormModel model, final int fieldNo )
    {
      this.model = model;
    }

    @Override
    public Component getFormValueEditorComponent ()
    {
      return new Container();
    }

    protected FormModel getModel ()
    {
      return model;
    }

    private final FormModel model;

  }


  public static class EnumEditor
      implements FormValueEditor
  {

    public EnumEditor ( final FormModel model, final int fieldNo )
    {
      this.model = model;
      this.field = fieldNo;

      final Class<?> enumerate = ((Enum<?>)getModel().getValueAt(field)).getDeclaringClass();

      comboBox = new JComboBox(enumerate.getEnumConstants());
      comboBox.setSelectedItem(getModel().getValueAt(field));
      comboBox.setEditable(getModel().isValueEditable(field));
      comboBox.setRequestFocusEnabled(false);
      comboBox.addActionListener((new ActionListener()
      {

        @Override
        public void actionPerformed ( final ActionEvent e )
        {
          getModel().setValueAt(comboBox.getSelectedItem(), field);
        }
      }));
    }

    @Override
    public Component getFormValueEditorComponent ()
    {
      return comboBox;
    }

    protected FormModel getModel ()
    {
      return model;
    }

    private final JComboBox comboBox;

    private final int       field;

    private final FormModel model;

  }

  public static class GenericEditor
      implements FormValueEditor
  {

    public GenericEditor ( final FormModel model, final int fieldNo )
    {
      this.model = model;
      this.field = fieldNo;
      this.textField = new JTextField();
      textField.setText(getModel().getValueAt(field).toString());
      final Border defaultBorder = textField.getBorder();
      textField.setEditable(getModel().isValueEditable(field));

      if ( getModel().isValueEditable(field) )
      {
        textField.setInputVerifier(new InputVerifier()
        {

          @Override
          public boolean shouldYieldFocus ( final JComponent component )
          {
            if ( verify(component) )
            {
              textField.setBorder(defaultBorder);
              final String text = ((JTextField)component).getText();
              try
              {
                getModel().setValueAt(getModel().getFieldClass(field).getConstructor(String.class).newInstance(text), field);
                textField.setText(getModel().getValueAt(field).toString());
                textField.setToolTipText(null);
              }
              catch ( final Exception e )
              {
                textField.setBorder(new LineBorder(Color.red));
                textField.setToolTipText(e.getMessage());
                return false;
              }
              return true;
            }
            else
            {
              textField.setBorder(new LineBorder(Color.red));
              return false;
            }

          }

          @Override
          public boolean verify ( final JComponent component )
          {
            try
            {
              final String text = ((JTextField)component).getText();
              getModel().getFieldClass(field).getConstructor(String.class).newInstance(text);
              textField.setToolTipText(null);
              return true;
            }
            catch ( final Exception e )
            {
              textField.setToolTipText(e.getMessage());
              return false;
            }
          }

        });

      }
    }

    @Override
    public Component getFormValueEditorComponent ()
    {
      return textField;
    }

    protected FormModel getModel ()
    {
      return model;
    }

    protected JTextField getTextField ()
    {
      return textField;
    }

    private final int        field;


    private final FormModel  model;

    private final JTextField textField;

  }


  public static class NumberEditor extends GenericEditor
  {

    public NumberEditor ( final FormModel model, final int fieldNo )
    {
      super(model, fieldNo);
    }
  }

  public static class StringEditor
      implements FormValueEditor
  {

    public StringEditor ( final FormModel model, final int fieldNo )
    {
      this.model = model;
      this.field = fieldNo;
      if ( getModel().isValueEditable(fieldNo) )
      {
        final JTextArea textArea = new JTextArea();
        final Object val = getModel().getValueAt(field);
        textArea.setText(val == null ? "" : val.toString());
        textArea.setWrapStyleWord(false);
        textArea.setLineWrap(false);
        textArea.setColumns(20);
        textArea.setRows(0);

        component = new JScrollPane(textArea,
                                    ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                                    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        textArea.setInputVerifier(new InputVerifier()
        {

          @Override
          public boolean shouldYieldFocus ( final JComponent component )
          {
            getModel().setValueAt(textArea.getText(), field);
            return super.shouldYieldFocus(component);
          }

          @Override
          public boolean verify ( final JComponent component )
          {
            return true;
          }

        });
      }
      else
      {
        final JTextField textField = new JTextField();
        textField.setText(getModel().getValueAt(field).toString());
        textField.setEditable(false);
        component = textField;
      }
    }

    @Override
    public Component getFormValueEditorComponent ()
    {
      return component;
    }

    protected FormModel getModel ()
    {
      return model;
    }

    private final int       field;
    private final FormModel model;

    private Component       component;
  }

  private enum Month
  {
    January,
    February,
    March,
    April,
    May,
    June,
    July,
    August,
    September,
    October,
    November,
    December
  }


  public static void main ( final String[] args )
  {
    final FormModel model = new AbstractFormModel()
    {

      @Override
      public Class<?> getFieldClass ( final int fieldIndex )
      {
        return classes[fieldIndex];
      }

      @Override
      public int getFieldCount ()
      {
        return classes.length;
      }

      @Override
      public Object getValueAt ( final int fieldIndex )
      {
        return values[fieldIndex];
      }

      @Override
      public void setValueAt ( final Object aValue, final int fieldIndex )
      {
        values[fieldIndex] = aValue;
      }


      @Override
      public boolean isValueEditable ( final int fieldIndex )
      {
        return true;
      }


      private final Class<?>[] classes = new Class[]
                                         { Void.class,
                                             Integer.class,
                                             Double.class,
                                             String.class,
                                             Boolean.class,
                                             Date.class,
                                             Enum.class,
                                             String.class,
                                             String.class };
      private final Object[]   values  = new Object[]
                                         {  null,
                                             500,
                                             123.456,
                                             "Hello",
                                             true,
                                             Calendar.getInstance().getTime(),
                                             Month.March,
                                             "This is some very long text\n It takes up lots of lines. \n\n\n\n Really it does!",
                                             "This is some very long text which goes on and on and on on the same line sdjh jkas hdkjas hdkjsadh kasjd hkasjd hkasjd kasj hsdkajh askjha ksdjah kjsdah kasjd askjdh askjd skajd kasjdh askjd aksdj kaj dajsa skajd " };

    };

    final JFrame frame = new JFrame("Test Form");
    frame.getContentPane().add(new JScrollPane(new Form(model)));
    frame.pack();
    frame.setVisible(true);

    final WindowListener l = new WindowAdapter()
    {

      @Override
      public void windowClosing ( final WindowEvent e )
      {
        System.exit(0);
      }
    };
    frame.addWindowListener(l);

  }

  public Form ( final FormModel model )
  {
    installEditorsPrivate();
    setModel(model);
  }

  @Override
  public void formChanged ( final FormModelEvent e )
  {
    if ( e.getStructureChanged() )
    {
      resetForm();
    }
  }

  public FormValueEditor getDefaultEditor ( final Class<?> fieldClass, final int field )
  {
    if ( fieldClass == null )
    {
      return null;
    }
    else
    {
      final Class<? extends FormValueEditor> editorClass = defaultEditorsByFieldClass.get(fieldClass);

      if ( editorClass != null )
      {
        try
        {
          return editorClass.getConstructor(FormModel.class, int.class).newInstance(getModel(), field);
        }
        catch ( final Exception e )
        {
          e.printStackTrace();
          return null;
        }
      }
      else
      {
        return getDefaultEditor(fieldClass.getSuperclass(), field);
      }
    }
  }


  public Class<?> getFieldClass ( final int field )
  {
    return getModel().getFieldClass(field);
  }


  /**
   * Returns the <code>TableModel</code> that provides the data displayed by
   * this <code>JTable</code>.
   *
   * @return the <code>TableModel</code> that provides the data displayed by
   *         this <code>JTable</code>
   * @see #setModel
   */
  public FormModel getModel ()
  {
    return dataModel;
  }

  public FormValueEditor getValueEditor ( final int field )
  {
    FormValueEditor editor = editorsByField.get(field);

    if ( editor == null )
    {
      editor = getDefaultEditor(dataModel.getFieldClass(field), field);
    }

    return editor;
  }

  public void resetForm ()
  {
    removeAll();

    final GridBagLayout layout = new GridBagLayout();
    setLayout(layout);


    for ( int i = 0; i < dataModel.getFieldCount(); ++i )
    {
      final GridBagConstraints labelConstraints = new GridBagConstraints();
      labelConstraints.ipadx = 20;
      labelConstraints.ipady = 10;
      labelConstraints.anchor = GridBagConstraints.NORTHWEST;
      labelConstraints.gridwidth = 1;
      labelConstraints.weightx = 0.0;
      labelConstraints.weighty = 0.0;

      final GridBagConstraints valueConstraints = new GridBagConstraints();
      valueConstraints.weightx = 1.0;
      valueConstraints.weighty = 0.0;
      valueConstraints.fill = GridBagConstraints.HORIZONTAL;
      valueConstraints.anchor = GridBagConstraints.NORTHWEST;
      valueConstraints.gridwidth = GridBagConstraints.REMAINDER;

      final FormValueEditor valueEditor = getValueEditor(i);
      final Component editorComponent = valueEditor.getFormValueEditorComponent();

      if ( editorComponent instanceof JScrollPane )
      {
        valueConstraints.weighty = 1.0;
        valueConstraints.fill = GridBagConstraints.BOTH;
      }
      else if ( editorComponent instanceof JTextField )
      {
        valueConstraints.weighty = 0.0;
        valueConstraints.fill = GridBagConstraints.HORIZONTAL;
        if ( !dataModel.isValueEditable(i) )
        {
          valueConstraints.fill = GridBagConstraints.NONE;
        }
        else
        {
          valueConstraints.fill = GridBagConstraints.HORIZONTAL;
        }
      }
      else if ( editorComponent instanceof Form )
      {
        valueConstraints.weighty = 1.0;
        valueConstraints.fill = GridBagConstraints.BOTH;
      }
      else if ( editorComponent instanceof JTable )
      {
        valueConstraints.weighty = 1.0;
        valueConstraints.fill = GridBagConstraints.BOTH;
      }
      else
      {
        valueConstraints.weighty = 0.0;
        valueConstraints.fill = GridBagConstraints.NONE;
      }

      if ( getFieldClass(i) == Void.class )
      {
        labelConstraints.gridwidth = GridBagConstraints.REMAINDER;
      }

      final JLabel label = new JLabel(dataModel.getFieldName(i));
      layout.setConstraints(label, labelConstraints);
      add(label);


      if ( getFieldClass(i) != Void.class )
      {
        layout.setConstraints(editorComponent, valueConstraints);
        add(editorComponent);
      }
    }

  }


  public void setDefaultValueEditor ( final Class<?> fieldClass, final Class<? extends FormValueEditor> editorClass )
  {
    defaultEditorsByFieldClass.put(fieldClass, editorClass);
  }

  /**
   * Sets the data model for this table to <code>newModel</code> and registers
   * with it for listener notifications from the new data model.
   *
   * @param dataModel
   *          the new data source for this table
   * @exception IllegalArgumentException
   *              if <code>newModel</code> is <code>null</code>
   * @see #getModel
   * @beaninfo bound: true description: The model that is the source of the data
   *           for this view.
   */
  public void setModel ( final FormModel dataModel )
  {
    if ( dataModel == null )
    {
      throw new IllegalArgumentException("Cannot set a null TableModel");
    }
    if ( this.dataModel != dataModel )
    {
      final FormModel old = this.dataModel;
      if ( old != null )
      {
        old.removeFormModelListener(this);
      }
      this.dataModel = dataModel;
      dataModel.addFormModelListener(this);

      formChanged(new FormModelEvent(dataModel, true));

      firePropertyChange("model", old, dataModel);
    }
  }


  public void setValueEditor ( final int field, final FormValueEditor editor )
  {
    editorsByField.put(field, editor);
  }

  protected void installEditors ()
  {
  }

  private void installEditorsPrivate ()
  {
    setDefaultValueEditor(Object.class, GenericEditor.class);
    setDefaultValueEditor(String.class, StringEditor.class);
    setDefaultValueEditor(Number.class, NumberEditor.class);
    setDefaultValueEditor(Boolean.class, BooleanEditor.class);
    setDefaultValueEditor(Enum.class, EnumEditor.class);
    setDefaultValueEditor(Void.class, VoidEditor.class);
    installEditors();
  }

  protected FormModel                             dataModel;

  Map<Class<?>, Class<? extends FormValueEditor>> defaultEditorsByFieldClass = new HashMap<Class<?>, Class<? extends FormValueEditor>>();

  private final Map<Integer, FormValueEditor>     editorsByField             = new HashMap<Integer, FormValueEditor>();

}
