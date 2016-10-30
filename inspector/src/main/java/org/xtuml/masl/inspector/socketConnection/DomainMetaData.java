// 
// Filename : DomainMetaData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;
import org.xtuml.masl.inspector.socketConnection.ipc.ReadableObject;


public class DomainMetaData extends org.xtuml.masl.inspector.processInterface.DomainMetaData
    implements ReadableObject
{

  private final Map<Integer, DomainServiceMetaData> serviceLookup      = new HashMap<Integer, DomainServiceMetaData>();
  private final Map<Integer, TerminatorMetaData>    terminatorLookup   = new HashMap<Integer, TerminatorMetaData>();
  private final Map<Integer, ObjectMetaData>        objectLookup       = new HashMap<Integer, ObjectMetaData>();
  private final Map<Integer, RelationshipMetaData>  relationshipLookup = new HashMap<Integer, RelationshipMetaData>();
  private final Map<Integer, SuperSubtypeMetaData>  superSubtypeLookup = new HashMap<Integer, SuperSubtypeMetaData>();
  private final Map<Integer, StructureMetaData>     structureLookup    = new HashMap<Integer, StructureMetaData>();
  private final Map<Integer, EnumerateMetaData>     enumerateLookup    = new HashMap<Integer, EnumerateMetaData>();


  @Override
  public DomainServiceMetaData[] getServices ()
  {
    return services;
  }

  @Override
  public DomainServiceMetaData[] getDomainServices ()
  {
    return domainServices;
  }

  @Override
  public DomainServiceMetaData[] getExternals ()
  {
    return externals;
  }

  @Override
  public DomainServiceMetaData[] getScenarios ()
  {
    return scenarios;
  }

  @Override
  public ObjectMetaData[] getObjects ()
  {
    return objects;
  }

  @Override
  public TerminatorMetaData[] getTerminators ()
  {
    return terminators;
  }

  @Override
  public RelationshipMetaData[] getRelationships ()
  {
    return relationships;
  }

  @Override
  public SuperSubtypeMetaData[] getSuperSubtypes ()
  {
    return superSubtypes;
  }

  @Override
  public StructureMetaData[] getStructures ()
  {
    return structures;
  }

  @Override
  public EnumerateMetaData[] getEnumerates ()
  {
    return enumerates;
  }

  private DomainServiceMetaData[] services;
  private DomainServiceMetaData[] externals;
  private DomainServiceMetaData[] scenarios;
  private DomainServiceMetaData[] domainServices;
  private ObjectMetaData[]        objects;
  private TerminatorMetaData[]    terminators;
  private RelationshipMetaData[]  relationships;
  private SuperSubtypeMetaData[]  superSubtypes;
  private StructureMetaData[]     structures;
  private EnumerateMetaData[]     enumerates;

  private ProcessMetaData         process;

  @Override
  public ProcessMetaData getProcess ()
  {
    return process;
  }

  public void setProcess ( final ProcessMetaData process )
  {
    this.process = process;
  }

  @Override
  public org.xtuml.masl.inspector.processInterface.DomainData getDomainData ()
  {
    return new DomainData(this);
  }

  public void read ( final CommunicationChannel channel ) throws IOException
  {
    id = channel.readInt();
    name = channel.readString();
    isInterface = channel.readBoolean();
    services = channel.readData(DomainServiceMetaData[].class);
    terminators = channel.readData(TerminatorMetaData[].class);
    objects = channel.readData(ObjectMetaData[].class);
    relationships = channel.readData(RelationshipMetaData[].class);
    superSubtypes = channel.readData(SuperSubtypeMetaData[].class);
    structures = channel.readData(StructureMetaData[].class);
    enumerates = channel.readData(EnumerateMetaData[].class);

    final StringTokenizer tokenizer = new StringTokenizer(System.getProperty("sourceDir", ""), ":");

    while ( defaultDirectory == null && tokenizer.hasMoreTokens() )
    {
      final File testDirectory = new File(tokenizer.nextToken() + File.separator + name + "_OOA");
      if ( testDirectory.exists() )
      {
        defaultDirectory = testDirectory;
      }
    }

    if ( defaultDirectory == null )
    {
      defaultDirectory = new File(System.getProperty("user.dir"));
    }

    final List<DomainServiceMetaData> domainServiceList = new ArrayList<DomainServiceMetaData>();
    final List<DomainServiceMetaData> externalList = new ArrayList<DomainServiceMetaData>();
    final List<DomainServiceMetaData> scenarioList = new ArrayList<DomainServiceMetaData>();
    for ( final DomainServiceMetaData service : services )
    {
      service.setDomain(this);
      serviceLookup.put(service.getArchId(), service);
      switch ( service.getType() )
      {
        case Domain:
          domainServiceList.add(service);
          break;
        case External:
          externalList.add(service);
          break;
        case Scenario:
          scenarioList.add(service);
          break;
        default:
          throw new IllegalStateException("Invalid domain service type " + service.getType());
      }
    }
    domainServices = domainServiceList.toArray(new DomainServiceMetaData[domainServiceList.size()]);
    externals = externalList.toArray(new DomainServiceMetaData[externalList.size()]);
    scenarios = scenarioList.toArray(new DomainServiceMetaData[scenarioList.size()]);


    for ( final ObjectMetaData object : objects )
    {
      object.setDomain(this);
      objectLookup.put(object.getArchId(), object);
    }

    for ( final TerminatorMetaData terminator : terminators )
    {
      terminator.setDomain(this);
      terminatorLookup.put(terminator.getArchId(), terminator);
    }


    for ( final RelationshipMetaData rel : relationships )
    {
      rel.setDomain(this);
      relationshipLookup.put(rel.getArchId(), rel);
    }
    for ( final SuperSubtypeMetaData superSubtype : getSuperSubtypes() )
    {
      superSubtype.setDomain(this);
      superSubtypeLookup.put(superSubtype.getArchId(), superSubtype);
    }

    for ( final StructureMetaData structure : structures )
    {
      structure.setDomain(this);
      structureLookup.put(structure.getArchId(), structure);
    }

    for ( final EnumerateMetaData enumerate : enumerates )
    {
      enumerate.setDomain(this);
      enumerateLookup.put(enumerate.getArchId(), enumerate);
    }


    linkChildren();

    for ( final ObjectMetaData object : objects )
    {
      for ( final EventMetaData event : object.getEvents() )
      {
        if ( event.getParentObject() != object )
        {
          event.getParentObject().addPolymorphicSubObject(object);
        }
      }
    }


  }

  private int     id;
  private String  name;
  private boolean isInterface;

  @Override
  public int getId ()
  {
    return id;
  }

  @Override
  public String getName ()
  {
    return name;
  }

  @Override
  public boolean isInterface ()
  {
    return isInterface;
  }

  public DomainServiceMetaData getService ( final int id )
  {
    return serviceLookup.get(id);
  }

  public ObjectMetaData getObject ( final int id )
  {
    return objectLookup.get(id);
  }

  public TerminatorMetaData getTerminator ( final int id )
  {
    return terminatorLookup.get(id);
  }

  public RelationshipMetaData getRelationship ( final int id )
  {
    return relationshipLookup.get(id);
  }

  public SuperSubtypeMetaData getSuperSubtype ( final int id )
  {
    return superSubtypeLookup.get(id);
  }

  public StructureMetaData getStructure ( final int id )
  {
    return structureLookup.get(id);
  }

  public EnumerateMetaData getEnumerate ( final int id )
  {
    return enumerateLookup.get(id);
  }

}
