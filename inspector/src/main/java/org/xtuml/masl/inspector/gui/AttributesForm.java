//
// File: ParameterBox.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.inspector.gui;

import java.awt.Component;

import org.xtuml.masl.inspector.gui.form.Form;
import org.xtuml.masl.inspector.gui.form.FormModel;
import org.xtuml.masl.inspector.gui.form.FormValueEditor;
import org.xtuml.masl.inspector.processInterface.CollectionData;
import org.xtuml.masl.inspector.processInterface.DataValue;
import org.xtuml.masl.inspector.processInterface.DictionaryData;
import org.xtuml.masl.inspector.processInterface.EnumerateData;
import org.xtuml.masl.inspector.processInterface.EventData;
import org.xtuml.masl.inspector.processInterface.InstanceData;
import org.xtuml.masl.inspector.processInterface.InstanceIdData;
import org.xtuml.masl.inspector.processInterface.StructureData;
import org.xtuml.masl.inspector.processInterface.TimerData;


public class AttributesForm extends Form
{

  public AttributesForm ( final StructureData structure, final boolean editable )
  {
    super(new AttributesModel(structure, editable));
  }

  public AttributesForm ( final InstanceIdData instance, final boolean editable )
  {
    super(new AttributesModel(instance.getInstanceData(), editable));
  }

  public AttributesForm ( final InstanceData instance, final boolean editable )
  {
    super(new AttributesModel(instance, editable));
  }

  public AttributesForm ( final TimerData instance )
  {
    super(new TimerFormModel(instance));
  }

  public AttributesForm ( final EventData instance )
  {
    super(new EventFormModel(instance));
  }

  public DataValue<?>[] getValues ()
  {
    return ((AttributesModel)getModel()).getValues();
  }

  @Override
  protected void installEditors ()
  {
    setDefaultValueEditor(EnumerateData.class, EnumerateEditor.class);
    setDefaultValueEditor(StructureData.class, AttributesForm.StructureEditor.class);
    setDefaultValueEditor(InstanceData.class, AttributesForm.InstanceEditor.class);
    setDefaultValueEditor(CollectionData.class, CollectionEditor.class);
    setDefaultValueEditor(DictionaryData.class, DictionaryEditor.class);
    setDefaultValueEditor(TimerData.class, AttributesForm.TimerEditor.class);
    setDefaultValueEditor(EventData.class, AttributesForm.EventEditor.class);
  }

  public static class StructureEditor
      implements FormValueEditor
  {

    public StructureEditor ( final FormModel model, final int fieldNo )
    {
      final StructureData structure = (StructureData)model.getValueAt(fieldNo);

      form = new AttributesForm(structure, model.isValueEditable(fieldNo));
    }

    public Component getFormValueEditorComponent ()
    {
      return form;
    }

    private final AttributesForm form;

  }

  public static class TimerEditor
      implements FormValueEditor
  {

    public TimerEditor ( final FormModel model, final int fieldNo )
    {
      final TimerData timer = (TimerData)model.getValueAt(fieldNo);

      form = new AttributesForm(timer);
    }

    public Component getFormValueEditorComponent ()
    {
      return form;
    }

    private final AttributesForm form;

  }

  public static class EventEditor
      implements FormValueEditor
  {

    public EventEditor ( final FormModel model, final int fieldNo )
    {
      final EventData event = (EventData)model.getValueAt(fieldNo);

      form = new AttributesForm(event);
    }

    public Component getFormValueEditorComponent ()
    {
      return form;
    }

    private final AttributesForm form;

  }


  public static class InstanceEditor
      implements FormValueEditor
  {

    public InstanceEditor ( final FormModel model, final int fieldNo )
    {
      final InstanceData instance = (InstanceData)model.getValueAt(fieldNo);

      form = new AttributesForm(instance, model.isValueEditable(fieldNo));
    }

    public Component getFormValueEditorComponent ()
    {
      return form;
    }

    private final AttributesForm form;

  }

}
