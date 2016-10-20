// 
// Filename : ObjectServiceMetaData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.processInterface;

import java.io.File;


public abstract class TerminatorServiceMetaData extends ServiceMetaData
{

  public abstract TerminatorMetaData getTerminator ();

  protected abstract boolean isOverride ();

  @Override
  public String getFullyQualifiedName ()
  {
    return getTerminator().getFullyQualifiedName() + "~>" + getName();
  }

  @Override
  public File getDirectory ()
  {
    return isOverride() ? getTerminator().getDomain().getProcess().getDirectory() : getTerminator().getDomain().getDirectory();
  }

}
