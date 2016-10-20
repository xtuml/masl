// 
// Filename : EnumerateMetaData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.processInterface;

import java.util.ArrayList;
import java.util.HashMap;


public abstract class EnumerateMetaData
    implements Comparable<EnumerateMetaData>
{

  protected String                         name;
  protected java.util.Map<Integer, String> nameFromValueLookup = new HashMap<Integer, String>();
  protected java.util.Map<String, Integer> valueFromNameLookup = new HashMap<String, Integer>();
  protected java.util.Map<Integer, String> nameFromIndexLookup = new HashMap<Integer, String>();
  protected java.util.Map<String, Integer> indexFromNameLookup = new HashMap<String, Integer>();
  protected java.util.List<EnumerateData>  enums               = new ArrayList<EnumerateData>();

  public String getName ()
  {
    return name;
  }

  public String decodeNameFromValue ( final int value )
  {
    return nameFromValueLookup.get(new Integer(value));
  }

  public int decodeValueFromName ( final String name )
  {
    return valueFromNameLookup.get(name).intValue();
  }

  public String decodeNameFromIndex ( final int value )
  {
    return nameFromIndexLookup.get(new Integer(value));
  }

  public int decodeIndexFromName ( final String name )
  {
    return indexFromNameLookup.get(name).intValue();
  }

  public java.util.Collection<String> getNames ()
  {
    return nameFromIndexLookup.values();
  }

  public java.util.List<EnumerateData> getEnums ()
  {
    return enums;
  }

  protected void addEnum ( final Integer value, final String name )
  {
    final int index = enums.size();
    final EnumerateData enumerate = getEnumerate(index);
    enums.add(enumerate);
    nameFromValueLookup.put(value, name);
    valueFromNameLookup.put(name, value);
    nameFromIndexLookup.put(index, name);
    indexFromNameLookup.put(name, index);
  }

  public abstract EnumerateData getEnumerate ( int index );

  public abstract DomainMetaData getDomain ();

  @Override
  public String toString ()
  {
    return name;
  }

  public int compareTo ( final EnumerateMetaData o )
  {
    return name.compareTo(o.name);
  }

}
