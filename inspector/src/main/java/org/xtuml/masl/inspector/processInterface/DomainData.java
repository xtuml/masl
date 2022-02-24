// 
// Filename : DomainData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.processInterface;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public abstract class DomainData implements org.xtuml.masl.inspector.processInterface.XMLSerializable {

    protected DomainMetaData meta;

    protected Map<ObjectMetaData, Map<Object, InstanceData>> objectData = new HashMap<ObjectMetaData, Map<Object, InstanceData>>();
    protected Map<RelationshipMetaData, List<RelationshipData>> relData = new HashMap<RelationshipMetaData, List<RelationshipData>>();
    protected Map<SuperSubtypeMetaData, List<SuperSubtypeData>> ssData = new HashMap<SuperSubtypeMetaData, List<SuperSubtypeData>>();

    protected DomainData(final DomainMetaData meta) {
        this.meta = meta;
    }

    public DomainMetaData getMetaData() {
        return meta;
    }

    @Override
    public Node toXML(final Document document) {
        final Node domain = document.createElement(meta.getName());

        for (final Map.Entry<ObjectMetaData, Map<Object, InstanceData>> entry : objectData.entrySet()) {
            final ObjectMetaData objMeta = entry.getKey();
            final Collection<InstanceData> instances = entry.getValue().values();

            System.out.println("Dumping " + objMeta.getName() + " (" + instances.size() + " instances)");
            for (final InstanceData instance : instances) {
                domain.appendChild(instance.toXML(document));
            }
        }

        for (final Map.Entry<RelationshipMetaData, List<RelationshipData>> entry : relData.entrySet()) {
            final RelationshipMetaData relMeta = entry.getKey();
            final Collection<RelationshipData> links = entry.getValue();

            System.out.println("Dumping " + relMeta.getNumber() + " (" + links.size() + " links)");
            for (final RelationshipData link : links) {
                domain.appendChild(link.toXML(document));
            }
        }

        for (final Map.Entry<SuperSubtypeMetaData, List<SuperSubtypeData>> entry : ssData.entrySet()) {
            final SuperSubtypeMetaData relMeta = entry.getKey();
            final Collection<SuperSubtypeData> links = entry.getValue();

            System.out.println("Dumping " + relMeta.getNumber() + " (" + links.size() + " links)");
            for (final SuperSubtypeData link : links) {
                domain.appendChild(link.toXML(document));
            }
        }
        return domain;
    }

    @Override
    public void fromXML(final Node parent) {
        // Parse file
        for (int i = 0; i < parent.getChildNodes().getLength(); ++i) {
            final Node curNode = parent.getChildNodes().item(i);
            if (curNode.getNodeType() == Node.ELEMENT_NODE) {
                // Check for id attribute on node, indicating it must be an instance
                // node
                if (curNode.getAttributes().getLength() > 0) {
                    // Instance Node

                    final Integer instanceId = new Integer(curNode.getAttributes().getNamedItem("id").getNodeValue());
                    final ObjectMetaData objectMeta = meta.getObject(curNode.getNodeName());
                    if (objectMeta == null) {
                        throw new IndexOutOfBoundsException("Object " + curNode.getNodeName() + " not found.");
                    }

                    final InstanceData instance = objectMeta.getInstanceData();
                    instance.fromXML(curNode);

                    Map<Object, InstanceData> instanceData = objectData.get(objectMeta);
                    if (instanceData == null) {
                        instanceData = new HashMap<Object, InstanceData>();
                        objectData.put(objectMeta, instanceData);
                    }
                    instanceData.put(instanceId, instance);
                } else {
                    // Relationship node
                    final RelationshipMetaData relMeta = meta.getRelationship(curNode.getNodeName());
                    if (relMeta != null) {

                        final RelationshipData rel = relMeta.getRelationshipData();
                        rel.fromXML(curNode);

                        List<RelationshipData> linkData = relData.get(relMeta);
                        if (linkData == null) {
                            linkData = new ArrayList<RelationshipData>();
                            relData.put(relMeta, linkData);
                        }
                        linkData.add(rel);
                    } else {
                        final SuperSubtypeMetaData ssMeta = meta.getSuperSubtype(curNode.getNodeName());

                        if (ssMeta == null) {
                            throw new IndexOutOfBoundsException(
                                    "Relationship " + curNode.getNodeName() + " not found.");
                        }
                        final SuperSubtypeData ss = ssMeta.getSuperSubtypeData();
                        ss.fromXML(curNode);

                        List<SuperSubtypeData> ssLinkData = ssData.get(ssMeta);
                        if (ssLinkData == null) {
                            ssLinkData = new ArrayList<SuperSubtypeData>();
                            ssData.put(ssMeta, ssLinkData);
                        }
                        ssLinkData.add(ss);
                    }
                }
            }
        }
    }

    public void readFromProcess() throws java.rmi.RemoteException {
        for (final ObjectMetaData objMetaTmp : meta.getObjects()) {
            final ObjectMetaData objMeta = objMetaTmp;
            ProcessConnection.getConnection().getInstanceData(objMeta, new InstanceDataListener() {

                @Override
                public boolean addInstanceData(final InstanceData instance) {
                    Map<Object, InstanceData> instanceData = objectData.get(objMeta);
                    if (instanceData == null) {
                        instanceData = new HashMap<Object, InstanceData>();
                        objectData.put(objMeta, instanceData);
                    }
                    instanceData.put(instance.getPrimaryKey(), instance);

                    final RelationshipData[] rels = instance.getFormalisedRelationships();

                    for (int i = 0; i < rels.length; ++i) {
                        final RelationshipMetaData relMeta = rels[i].getMetaData();
                        List<RelationshipData> linkData = relData.get(relMeta);
                        if (linkData == null) {
                            linkData = new ArrayList<RelationshipData>();
                            relData.put(relMeta, linkData);
                        }
                        linkData.add(rels[i]);
                    }

                    final SuperSubtypeData[] sss = instance.getFormalisedSuperSubtypes();
                    for (int i = 0; i < sss.length; ++i) {
                        final SuperSubtypeMetaData ssMeta = sss[i].getMetaData();
                        List<SuperSubtypeData> linkData = ssData.get(ssMeta);
                        if (linkData == null) {
                            linkData = new ArrayList<SuperSubtypeData>();
                            ssData.put(ssMeta, linkData);
                        }
                        linkData.add(sss[i]);
                    }

                    return false;
                }

                @Override
                public void finished() {
                }

                @Override
                public void setInstanceCount(final int count) {
                    System.out.println("Reading " + objMeta.getName() + " (" + count + " instances)");
                }
            });
        }
    }

    public void writeToProcess() throws java.rmi.RemoteException {
        // Import the instance data into the process. This
        // will update the primary Key values on the actual
        // instances to those stored int the process, so that we can
        // look them up from the XML keys in a moment.
        for (final ObjectMetaData objectMeta : objectData.keySet()) {
            final Map<Object, InstanceData> instanceData = objectData.get(objectMeta);
            System.out.println("Creating " + objectMeta.getName() + " (" + instanceData.size() + " instances)");
            final InstanceData[] instances = instanceData.values().toArray(new InstanceData[0]);
            ProcessConnection.getConnection().createInstancePopulation(objectMeta, instances);
        }

        // Now update the XML key values on the relationships to
        // those used by the process, and create the relationships
        // in the process.
        for (final RelationshipMetaData relMeta : relData.keySet()) {
            final List<RelationshipData> linkData = relData.get(relMeta);
            System.out.println("Creating " + relMeta.getNumber() + " (" + linkData.size() + " links)");

            final ObjectMetaData leftObject = relMeta.getLeftObject();
            final ObjectMetaData rightObject = relMeta.getRightObject();
            final ObjectMetaData assocObject = relMeta.getAssocObject();

            final Map<Object, InstanceData> leftLookup = objectData.get(leftObject);
            final Map<Object, InstanceData> rightLookup = objectData.get(rightObject);
            final Map<Object, InstanceData> assocLookup = objectData.get(assocObject);

            final List<RelationshipData> validLinks = new ArrayList<RelationshipData>(linkData.size());

            for (final RelationshipData link : linkData) {
                final InstanceData leftObj = leftLookup.get(link.getLeftId());

                if (leftObj == null) {
                    System.out.println("Warning: " + leftObject.getName() + " id=" + link.getLeftId() + " not found.");
                    break;
                }

                final InstanceData rightObj = rightLookup.get(link.getRightId());
                if (rightObj == null) {
                    System.out
                            .println("Warning: " + rightObject.getName() + " id=" + link.getRightId() + " not found.");
                    break;
                }

                final Object newLeftId = leftObj.getPrimaryKey();
                final Object newRightId = rightObj.getPrimaryKey();

                link.setLeftId(newLeftId);
                link.setRightId(newRightId);
                if (relMeta.getAssocObject() != null) {
                    final InstanceData assocObj = assocLookup.get(link.getAssocId());
                    if (assocObj == null) {
                        System.out.println(
                                "Warning: " + assocObject.getName() + " id=" + link.getAssocId() + " not found.");
                        break;
                    }

                    final Object newAssocId = assocObj.getPrimaryKey();
                    link.setAssocId(newAssocId);
                }
                validLinks.add(link);
            }
            ProcessConnection.getConnection().createRelationships(relMeta, validLinks.toArray(new RelationshipData[0]));
        }

        // ...similarly for the superSubtypes
        for (final SuperSubtypeMetaData ssMeta : ssData.keySet()) {
            final List<SuperSubtypeData> ssLinkData = ssData.get(ssMeta);
            System.out.println("Creating " + ssMeta.getNumber() + " (" + ssLinkData.size() + " links)");

            final ObjectMetaData superObject = ssMeta.getSupertype();
            final Map<Object, InstanceData> superLookup = objectData.get(superObject);

            final List<SuperSubtypeData> validLinks = new ArrayList<SuperSubtypeData>(ssLinkData.size());

            for (final SuperSubtypeData link : ssLinkData) {
                final ObjectMetaData subObject = ssMeta.getSubtypes()[link.getSubtypeIndex()];
                final Map<Object, InstanceData> subLookup = objectData.get(subObject);

                final InstanceData superObj = superLookup.get(link.getSupertypeId());
                if (superObj == null) {
                    System.out.println(
                            "Warning: " + superObject.getName() + " id=" + link.getSupertypeId() + " not found.");
                    break;
                }
                final InstanceData subObj = subLookup.get(link.getSubtypeId());
                if (subObj == null) {
                    System.out
                            .println("Warning: " + subObject.getName() + " id=" + link.getSubtypeId() + " not found.");
                    break;
                }
                final Object newSuperId = superObj.getPrimaryKey();
                final Object newSubId = subObj.getPrimaryKey();

                link.setSupertypeId(newSuperId);
                link.setSubtypeId(newSubId);
                validLinks.add(link);
            }
            ProcessConnection.getConnection().createSuperSubtypes(ssMeta, validLinks.toArray(new SuperSubtypeData[0]));
        }

        System.out.print("Rescheduling timers");

        // Reschedule timers
        final boolean timersEnabled = ProcessConnection.getConnection().getEnableTimers();
        ProcessConnection.getConnection().setEnableTimers(false);

        int rescheduled = 0;

        for (final ObjectMetaData objectMeta : objectData.keySet()) {
            for (int i = 0; i < objectMeta.getAttributes().length; ++i) {
                final AttributeMetaData attMeta = objectMeta.getAttributes()[i];
                if (attMeta.getType().getBasicType() == TypeMetaData.BasicType.Timer) {
                    final Collection<InstanceData> instances = objectData.get(objectMeta).values();

                    for (final InstanceData localInstance : instances) {
                        final TimerData timer = (TimerData) localInstance.getAttributes()[i];
                        if (timer.isScheduled()) {
                            final InstanceData dbInstance = ProcessConnection.getConnection()
                                    .getInstanceData(objectMeta, localInstance.getPrimaryKey());
                            final TimerData dbTimer = (TimerData) dbInstance.getAttributes()[i];
                            timer.setId(dbTimer.getId());

                            if (timer.getEventData().getSourceInstanceId() != null) {
                                relinkKey(timer.getEventData().getSourceInstanceId());
                            }

                            if (timer.getEventData().getDestInstanceId() != null) {
                                relinkKey(timer.getEventData().getDestInstanceId());
                                if (timer.getEventData().getDestInstanceId().getId() == null) {
                                    // Destination not found, so have to abort this one
                                    break;
                                }
                            }

                            for (final DataValue<?> param : timer.getEventData().getParameters()) {
                                relinkKey(param);
                            }
                            ProcessConnection.getConnection().scheduleEvent(timer);
                            ++rescheduled;

                        }
                    }

                }
            }
        }

        System.out.println(" (" + rescheduled + " events)");
        ProcessConnection.getConnection().setEnableTimers(timersEnabled);

    }

    void relinkKey(final DataValue<?> value) {
        if (value instanceof InstanceIdData) {
            final InstanceIdData instance = (InstanceIdData) value;
            final InstanceData instanceData = objectData.get(instance.getMetaData()).get(instance.getId());
            if (instanceData == null) {
                System.out.println(
                        "Warning: " + instance.getMetaData().getName() + " id=" + instance.getId() + " not found.");
                instance.setId(null);
            } else {
                instance.setId(instanceData.getPrimaryKey());
            }
        } else if (value instanceof CollectionData) {
            final CollectionData collection = (CollectionData) value;
            for (final DataValue<?> item : collection.getData()) {
                relinkKey(item);
            }
        } else if (value instanceof StructureData) {
            final StructureData struct = (StructureData) value;
            for (final DataValue<?> element : struct.getAttributes()) {
                relinkKey(element);
            }
        } else if (value instanceof DictionaryData) {
            final DictionaryData dict = (DictionaryData) value;
            for (final DataValue<?> key : dict.getData().keySet()) {
                relinkKey(key);
            }
            for (final DataValue<?> val : dict.getData().values()) {
                relinkKey(val);
            }
        }
    }
}
