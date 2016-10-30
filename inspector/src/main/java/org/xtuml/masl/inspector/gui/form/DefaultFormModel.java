//
// File: DefaultFormModel.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.inspector.gui.form;

import java.util.List;


public class DefaultFormModel extends AbstractFormModel
{

  private final List<String> names;
  private final List<Object> values;

  public DefaultFormModel ( final List<String> names, final List<Object> values )
  {
    this.names = names;
    this.values = values;
  }

  @Override
  public Class<?> getFieldClass ( final int fieldIndex )
  {
    return values.get(fieldIndex).getClass();
  }

  @Override
  public String getFieldName ( final int field )
  {
    if ( field >= names.size() )
    {
      return super.getFieldName(field);
    }
    else
    {
      return names.get(field);
    }
  }

  @Override
  public int getFieldCount ()
  {
    return values.size();
  }

  @Override
  public Object getValueAt ( final int fieldIndex )
  {
    return values.get(fieldIndex);
  }

}
