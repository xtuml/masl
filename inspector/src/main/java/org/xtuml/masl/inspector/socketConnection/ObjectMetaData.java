// 
// Filename : ObjectMetaData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;
import org.xtuml.masl.inspector.socketConnection.ipc.ReadableObject;


public class ObjectMetaData extends org.xtuml.masl.inspector.processInterface.ObjectMetaData
    implements ReadableObject
{

  public int getArchId ()
  {
    return archId;
  }

  @Override
  public EventMetaData[] getAssignerEvents ()
  {
    return assignerEvents;
  }

  @Override
  public StateMetaData[] getAssignerStates ()
  {
    return assignerStates;
  }

  @Override
  public AttributeMetaData[] getAttributes ()
  {
    return attributes;
  }

  @Override
  public DomainMetaData getDomain ()
  {
    return domain;
  }

  public EventMetaData getEvent ( final int number )
  {
    return eventLookup.get(number);
  }

  @Override
  public EventMetaData[] getEvents ()
  {
    return events;
  }

  @Override
  public InstanceData getInstanceData ()
  {
    return new InstanceData(this);
  }

  @Override
  public EventMetaData[] getInstanceEvents ()
  {
    return instanceEvents;
  }

  @Override
  public ObjectServiceMetaData[] getInstanceServices ()
  {
    return instanceServices;
  }

  @Override
  public StateMetaData[] getInstanceStates ()
  {
    return instanceStates;
  }

  @Override
  public ObjectServiceMetaData[] getObjectServices ()
  {
    return objectServices;
  }

  @Override
  public ObjectRelationshipMetaData[] getRelationships ()
  {
    return relationships;
  }

  public ObjectServiceMetaData getService ( final int number )
  {
    return serviceLookup.get(number);
  }

  @Override
  public ObjectServiceMetaData[] getServices ()
  {
    return services;
  }

  public StateMetaData getState ( final int number )
  {
    return stateLookup.get(number);
  }

  public StateMetaData getState ( final String name )
  {
    return stateNameLookup.get(name);
  }

  @Override
  public StateMetaData[] getStates ()
  {
    return states;
  }

  public TypeMetaData getInstanceType ()
  {
    return TypeMetaData.createInstanceType(domain.getId(), archId);
  }

  public void read ( final CommunicationChannel channel ) throws IOException
  {
    archId = channel.readInt();
    name = channel.readString();
    keyLetters = channel.readString();

    attributes = channel.readData(AttributeMetaData[].class);
    relationships = channel.readData(ObjectRelationshipMetaData[].class);
    services = channel.readData(ObjectServiceMetaData[].class);
    states = channel.readData(StateMetaData[].class);
    events = channel.readData(EventMetaData[].class);

    for ( final AttributeMetaData att : attributes )
    {
      att.setObject(this);
    }

    for ( final ObjectRelationshipMetaData rel : getRelationships() )
    {
      rel.setObject(this);
    }

    final List<ObjectServiceMetaData> objectServiceList = new ArrayList<ObjectServiceMetaData>();
    final List<ObjectServiceMetaData> instanceServiceList = new ArrayList<ObjectServiceMetaData>();

    for ( final ObjectServiceMetaData service : services )
    {
      service.setObject(this);
      serviceLookup.put(service.getArchId(), service);
      switch ( service.getType() )
      {
        case Object:
          objectServiceList.add(service);
          break;
        case Instance:
          instanceServiceList.add(service);
          break;
        default:
          throw new IllegalStateException("Invalid object service type " + service.getType());
      }
    }
    objectServices = objectServiceList.toArray(new ObjectServiceMetaData[objectServiceList.size()]);
    instanceServices = instanceServiceList.toArray(new ObjectServiceMetaData[instanceServiceList.size()]);

    final List<StateMetaData> assignerStateList = new ArrayList<StateMetaData>();
    final List<StateMetaData> instanceStateList = new ArrayList<StateMetaData>();

    for ( final StateMetaData state : states )
    {
      state.setObject(this);
      stateLookup.put(state.getId(), state);
      stateNameLookup.put(state.getName(), state);
      switch ( state.getType() )
      {
        case Assigner:
        case Start:
          assignerStateList.add(state);
          break;
        case Normal:
        case Creation:
        case Terminal:
          instanceStateList.add(state);
          break;
        default:
          throw new IllegalStateException("Invalid state type " + state.getType());
      }
    }
    assignerStates = assignerStateList.toArray(new StateMetaData[assignerStateList.size()]);
    instanceStates = instanceStateList.toArray(new StateMetaData[instanceStateList.size()]);

    final List<EventMetaData> assignerEventList = new ArrayList<EventMetaData>();
    final List<EventMetaData> instanceEventList = new ArrayList<EventMetaData>();
    for ( final EventMetaData event : events )
    {
      event.setObject(this);
      eventLookup.put(event.getArchId(), event);
      switch ( event.getType() )
      {
        case Assigner:
          assignerEventList.add(event);
          break;
        case Normal:
        case Creation:
          instanceEventList.add(event);
          break;
        default:
          throw new IllegalStateException("Invalid state type " + event.getType());
      }
    }
    assignerEvents = assignerEventList.toArray(new EventMetaData[assignerEventList.size()]);
    instanceEvents = instanceEventList.toArray(new EventMetaData[instanceEventList.size()]);

  }

  public void setDomain ( final DomainMetaData domain )
  {
    this.domain = domain;
    for ( final ObjectServiceMetaData service : services )
    {
      service.setDomain(domain);
    }
    for ( final StateMetaData state : states )
    {
      state.setDomain(domain);
    }
    for ( final EventMetaData event : events )
    {
      event.setDomain(domain);
    }
    for ( final AttributeMetaData attribute : attributes )
    {
      attribute.setDomain(domain);
    }

  }

  private DomainMetaData                            domain                = null;

  private final Map<Integer, ObjectServiceMetaData> serviceLookup         = new HashMap<Integer, ObjectServiceMetaData>();

  private final Map<Integer, StateMetaData>         stateLookup           = new HashMap<Integer, StateMetaData>();

  private final Map<String, StateMetaData>          stateNameLookup       = new HashMap<String, StateMetaData>();

  private final Map<Integer, EventMetaData>         eventLookup           = new HashMap<Integer, EventMetaData>();

  private int                                       archId;

  private AttributeMetaData[]                       attributes;

  private ObjectRelationshipMetaData[]              relationships;

  private StateMetaData[]                           states;

  private StateMetaData[]                           instanceStates;

  private StateMetaData[]                           assignerStates;

  private ObjectServiceMetaData[]                   services;

  private ObjectServiceMetaData[]                   instanceServices;

  private ObjectServiceMetaData[]                   objectServices;

  private EventMetaData[]                           events;

  private EventMetaData[]                           instanceEvents;

  private EventMetaData[]                           assignerEvents;
  private final Set<ObjectMetaData>                 polymorphicSubObjects = new HashSet<ObjectMetaData>();

  @Override
  public ObjectMetaData[] getPolymorphicSubObjects ()
  {
    return polymorphicSubObjects.toArray(new ObjectMetaData[polymorphicSubObjects.size()]);
  }

  public void addPolymorphicSubObject ( final ObjectMetaData object )
  {
    polymorphicSubObjects.add(object);
  }

}
