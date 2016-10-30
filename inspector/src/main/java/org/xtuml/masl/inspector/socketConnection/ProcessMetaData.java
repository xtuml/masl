// 
// Filename : ProcessMetaData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;
import org.xtuml.masl.inspector.socketConnection.ipc.ReadableObject;


public class ProcessMetaData extends org.xtuml.masl.inspector.processInterface.ProcessMetaData
    implements ReadableObject
{

  private DomainMetaData[]                   domains;
  private Plugin[]                           plugins;
  private final Map<String, DomainMetaData>  domainLookupByName = new HashMap<String, DomainMetaData>();
  private final Map<String, Plugin>          pluginLookupByName = new HashMap<String, Plugin>();
  private final Map<Integer, DomainMetaData> domainLookupById   = new HashMap<Integer, DomainMetaData>();

  public ProcessMetaData ()
  {
  }

  @Override
  public DomainMetaData[] getDomains ()
  {
    return domains;
  }

  @Override
  public DomainMetaData getDomain ( final String domainName )
  {
    return domainLookupByName.get(domainName);
  }

  @Override
  public Plugin[] getPlugins ()
  {
    return plugins;
  }

  @Override
  public Plugin getPlugin ( final String pluginName )
  {
    return pluginLookupByName.get(pluginName);
  }

  public DomainMetaData getDomain ( final int domainNo )
  {
    return domainLookupById.get(domainNo);
  }

  public void read ( final CommunicationChannel channel ) throws IOException
  {
    name = channel.readString();
    plugins = channel.readData(Plugin[].class);

    for ( final Plugin plugin : plugins )
    {
      pluginLookupByName.put(plugin.getName(), plugin);
    }

    domains = channel.readData(DomainMetaData[].class);

    for ( final DomainMetaData domain : domains )
    {
      domainLookupByName.put(domain.getName(), domain);
      domainLookupById.put(new Integer(domain.getId()), domain);
      domain.setProcess(this);
    }

    final StringTokenizer tokenizer = new StringTokenizer(System.getProperty("sourceDir", ""), ":");

    while ( defaultDirectory == null && tokenizer.hasMoreTokens() )
    {
      final File testDirectory = new File(tokenizer.nextToken() + File.separator + name);
      if ( testDirectory.exists() )
      {
        defaultDirectory = testDirectory;
      }
    }

    if ( defaultDirectory == null )
    {
      defaultDirectory = new File(System.getProperty("user.dir"));
    }
  }

}
