// 
// Filename : StructureMetaData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.processInterface;


public abstract class StructureMetaData
    implements Comparable<StructureMetaData>
{

  public abstract String getName ();

  public abstract AttributeMetaData[] getAttributes ();

  public abstract DomainMetaData getDomain ();

  @Override
  public String toString ()
  {
    return getName();
  }

  public int compareTo ( final StructureMetaData o )
  {
    return getName().compareTo(o.getName());
  }


}
