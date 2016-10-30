// 
// Filename : NullConnection.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.processInterface;

public class NullConnection extends ProcessConnection
{

  public NullConnection ()
  {
  }

  @Override
  public String getConnectionTitle ()
  {
    return "No Connection";
  }

  @Override
  public ProcessMetaData getMetaData ()
  {
    return new ProcessMetaData()
    {

      @Override
      public DomainMetaData[] getDomains ()
      {
        return new DomainMetaData[0];
      }

      @Override
      public DomainMetaData getDomain ( final String name )
      {
        return null;
      }

      @Override
      public Plugin getPlugin ( final String pluginName )
      {
        return null;
      }

      @Override
      public Plugin[] getPlugins ()
      {
        return new Plugin[0];
      }
    };
  }
}
