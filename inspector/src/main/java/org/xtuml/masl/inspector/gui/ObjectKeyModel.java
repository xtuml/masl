// 
// Filename : ParameterBox.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui;

import java.math.BigInteger;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.xtuml.masl.inspector.gui.form.AbstractFormModel;
import org.xtuml.masl.inspector.processInterface.AttributeMetaData;
import org.xtuml.masl.inspector.processInterface.BooleanData;
import org.xtuml.masl.inspector.processInterface.ByteData;
import org.xtuml.masl.inspector.processInterface.CharacterData;
import org.xtuml.masl.inspector.processInterface.DataValue;
import org.xtuml.masl.inspector.processInterface.InstanceData;
import org.xtuml.masl.inspector.processInterface.IntegerData;
import org.xtuml.masl.inspector.processInterface.LongData;
import org.xtuml.masl.inspector.processInterface.LongNaturalData;
import org.xtuml.masl.inspector.processInterface.NaturalData;
import org.xtuml.masl.inspector.processInterface.ObjectMetaData;
import org.xtuml.masl.inspector.processInterface.ProcessConnection;
import org.xtuml.masl.inspector.processInterface.RealData;
import org.xtuml.masl.inspector.processInterface.StringData;
import org.xtuml.masl.inspector.processInterface.StructureData;
import org.xtuml.masl.inspector.processInterface.TypeMetaData;
import org.xtuml.masl.inspector.processInterface.TypeMetaData.BasicType;


class ObjectKeyModel extends AbstractFormModel
{

  private static class KeyData
  {

    KeyData ( final String name, final TypeMetaData type, final DataValue<?> value )
    {
      this.name = name;
      this.type = type;
      this.value = value;
    }

    String getName ()
    {
      return name;
    }

    TypeMetaData getType ()
    {
      return type;
    }

    DataValue<?> getValue ()
    {
      return value;
    }

    private final String       name;

    private final TypeMetaData type;

    private final DataValue<?> value;
  }

  ObjectKeyModel ( final ObjectMetaData object, final Object pk )
  {
    try
    {
      final InstanceData instance = ProcessConnection.getConnection().getInstanceData(object, pk);

      if ( instance == null )
      {
        throw new IllegalStateException("Instance Deleted");
      }

      for ( int i = 0; i < object.getAttributes().length; i++ )
      {
        final AttributeMetaData a = object.getAttributes()[i];
        if ( a.isIdentifier() )
        {
          addKeyField(new KeyData(object.getAttributes()[i].getName(),
                                  object.getAttributes()[i].getType(),
                                  instance.getAttributes()[i]));
        }
      }
    }
    catch ( final RemoteException e )
    {
      e.printStackTrace();
    }


  }

  public DataValue<?> getDataValueAt ( final int fieldIndex )
  {
    return keyFields.get(fieldIndex).getValue();
  }

  @Override
  public Class<?> getFieldClass ( final int fieldIndex )
  {
    return getClassFromType(getFieldType(fieldIndex));
  }

  public int getFieldCount ()
  {
    return keyFields.size();
  }

  @Override
  public String getFieldName ( final int fieldIndex )
  {
    return keyFields.get(fieldIndex).getName();
  }

  public Object getValueAt ( final int fieldIndex )
  {
    final TypeMetaData type = getFieldType(fieldIndex);
    final DataValue<?> att = getDataValueAt(fieldIndex);

    switch ( type.getBasicType() )
    {
      case Boolean:
        return ((BooleanData)att).getValue();
      case Byte:
        return ((ByteData)att).getValue();
      case Character:
        return ((CharacterData)att).getValue();
      case Device:
        return att.toString();
      case Duration:
        return att.toString();
      case Enumeration:
        return att.toString();
      case Instance:
        return att.toString();
      case Integer:
        return ((IntegerData)att).getValue();
      case LongInteger:
        return ((LongData)att).getValue();
      case LongNatural:
        return ((LongNaturalData)att).getValue();
      case Natural:
        return ((NaturalData)att).getValue();
      case Real:
        return ((RealData)att).getValue();
      case State:
        return att.toString();
      case String:
        return ((StringData)att).getValue();
      case Structure:
        return att.toString();
      case Timestamp:
        return att.toString();
      case WCharacter:
        return ((CharacterData)att).getValue();
      case WString:
        return ((StringData)att).getValue();
      default:
        throw new IllegalStateException("Unrecognised type : " + type.getBasicType());
    }
  }

  @Override
  public boolean isValueEditable ( final int fieldIndex )
  {
    return false;
  }

  private void addKeyField ( final KeyData field )
  {
    if ( field.getType().getBasicType() != BasicType.Structure )
    {
      keyFields.add(field);
    }
    else
    {
      for ( int i = 0; i < field.getType().getStructure().getAttributes().length; ++i )
      {
        final AttributeMetaData att = field.getType().getStructure().getAttributes()[i];
        final KeyData attField = new KeyData(field.getName() + "." + att.getName(),
                                             att.getType(),
                                             ((StructureData)field.getValue()).getAttributes()[i]);
        addKeyField(attField);
      }
    }

  }

  private Class<?> getClassFromType ( final TypeMetaData type )
  {
    switch ( type.getBasicType() )
    {
      case Boolean:
        return Boolean.class;
      case Byte:
        return Byte.class;
      case Character:
        return Character.class;
      case Device:
        return String.class;
      case Duration:
        return String.class;
      case Enumeration:
        return String.class;
      case Instance:
        return String.class;
      case Integer:
        return Integer.class;
      case LongInteger:
        return Long.class;
      case LongNatural:
        return BigInteger.class;
      case Natural:
        return Long.class;
      case Real:
        return Double.class;
      case State:
        return String.class;
      case String:
        return String.class;
      case Structure:
        return String.class;
      case Timestamp:
        return String.class;
      case WCharacter:
        return Character.class;
      case WString:
        return String.class;
      default:
        throw new IllegalStateException("Unrecognised type : " + type.getBasicType());
    }
  }

  private TypeMetaData getFieldType ( final int fieldIndex )
  {
    return keyFields.get(fieldIndex).getType();
  }

  private final List<KeyData> keyFields = new ArrayList<KeyData>();


}
